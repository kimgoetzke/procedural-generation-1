package com.hindsight.king_of_castrop_rauxel.world;

import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.graphs.LocationDto;
import com.hindsight.king_of_castrop_rauxel.location.Location;
import com.hindsight.king_of_castrop_rauxel.location.Settlement;
import com.hindsight.king_of_castrop_rauxel.world.Coordinates.CoordType;
import java.util.Random;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class Chunk implements Generatable, Unloadable {

  private final ChunkHandler chunkHandler;
  private final Random random;
  private final int chunkSize;
  private final int minPlacementDistance;
  private final Strategy strategy;
  @Getter private final String id;
  @Getter private final int density;
  @Getter private final Coordinates coordinates;
  @Getter private final int targetLevel;

  // Status-dependent fields (only set when chunk is loaded)
  @Getter private Location[][] plane;
  @Getter @Setter private boolean isLoaded;

  /**
   * Determines the strategy for populating the chunk with regard to connecting locations to the
   * graph. NONE will not connect any locations to the graph.
   */
  public enum Strategy {
    DEFAULT,
    NONE,
  }

  public Chunk(Pair<Integer, Integer> worldCoords, ChunkHandler chunkHandler) {
    this(worldCoords, chunkHandler, Strategy.DEFAULT);
  }

  public Chunk(Pair<Integer, Integer> worldCoords, ChunkHandler chunkHandler, Strategy strategy) {
    var chunkProperties = chunkHandler.getAppProperties().getChunkProperties();
    var seed = SeedBuilder.seedFrom(worldCoords);
    var cf = new CoordinateFactory(chunkHandler.getAppProperties());
    this.coordinates = cf.create(worldCoords, CoordType.WORLD);
    this.random = new Random(seed);
    this.id = IdBuilder.idFrom(this.getClass(), coordinates);
    this.chunkHandler = chunkHandler.initialise(random);
    this.density = randomDensity(chunkProperties.density());
    this.chunkSize = chunkProperties.size();
    this.minPlacementDistance = chunkProperties.minPlacementDistance();
    this.strategy = strategy;
    this.plane = new Location[chunkSize][chunkSize];
    this.targetLevel = this.chunkHandler.getTargetLevel(coordinates);
  }

  @Override
  public void load() {
    plane = new Location[chunkSize][chunkSize];
    chunkHandler.populate(this, strategy);
    setLoaded(true);
    logResult();
  }

  @Override
  public void unload() {
    setLoaded(false);
    logResult();
  }

  public Pair<Integer, Integer> getCentreCoords() {
    return Pair.of(chunkSize / 2, chunkSize / 2);
  }

  public Pair<Integer, Integer> getRandomCoords() {
    var x = -1;
    var y = -1;
    while (!isValidPosition(x, y) || hasNeighbors(x, y, minPlacementDistance)) {
      x = random.nextInt(chunkSize + 1);
      y = random.nextInt(chunkSize + 1);
    }
    return Pair.of(x, y);
  }

  public Settlement getCentralLocation(World world, Graph map) {
    var globalCoords = world.getCurrentChunk().getCoordinates().getGlobal();
    var startVertex = chunkHandler.closestLocationTo(globalCoords, map.getVertices());
    var locationCoords = startVertex.getDto().coordinates();
    var centralLocation = getLoadedLocation(locationCoords);
    if (centralLocation != null) {
      log.info("Found central location: {}", centralLocation.getFullSummary());
      return (Settlement) centralLocation;
    }
    throw new IllegalStateException("Could not find any central location");
  }

  /** Returns the specified location (or null), regardless of whether it is loaded or not. */
  public Location getLocation(Coordinates coordinates) {
    var x = coordinates.getChunk().getFirst();
    var y = coordinates.getChunk().getSecond();
    return plane[x][y];
  }

  public Location getLoadedLocation(Coordinates coordinates) {
    var x = coordinates.getChunk().getFirst();
    var y = coordinates.getChunk().getSecond();
    var location = plane[x][y];
    if (location != null) {
      location.load();
    }
    return location;
  }

  /** Only used by ChunkHandlerTest */
  public void place(LocationDto dto) {
    var coords = dto.coordinates();
    var location = chunkHandler.generateSettlement(this, coords.getChunk());
    var x = coords.cX();
    var y = coords.cY();
    if (isValidPosition(x, y)) {
      plane[x][y] = location;
    }
  }

  public void place(Location location) {
    var x = location.getCoordinates().cX();
    var y = location.getCoordinates().cY();
    if (isValidPosition(x, y)) {
      plane[x][y] = location;
    }
  }

  private boolean isValidPosition(int x, int y) {
    return x >= 0 && x < chunkSize && y >= 0 && y < chunkSize;
  }

  private boolean hasNeighbors(int x, int y, int distance) {
    for (var dx = -distance; dx <= distance; dx++) {
      for (var dy = -distance; dy <= distance; dy++) {
        var neighborX = x + dx;
        var neighborY = y + dy;
        if (isValidPosition(neighborX, neighborY) && plane[neighborX][neighborY] != null) {
          return true;
        }
      }
    }
    return false;
  }

  public int randomDensity(Bounds density) {
    return random.nextInt(density.getUpper() - density.getLower() + 1) + density.getLower();
  }

  @Override
  public void logResult() {
    var action = isLoaded ? "Generated" : "Unloaded";
    log.info(
        "{}: Chunk '{}' at {} with density {} using seed {}",
        action,
        id,
        coordinates,
        density,
        SeedBuilder.seedFrom(coordinates.getGlobal()));
  }

  public String getSummary() {
    return String.format("Chunk '%s' at %s with density %d", id, coordinates, density);
  }
}
