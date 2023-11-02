package com.hindsight.king_of_castrop_rauxel.world;

import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.location.AbstractLocation;
import com.hindsight.king_of_castrop_rauxel.location.Settlement;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

@Slf4j
public class Chunk implements Generatable, Unloadable {

  private final ChunkHandler chunkHandler;

  @Getter private final String id;
  @Getter private final int density;
  @Getter private final int[][] plane;
  @Getter private final Coordinates coordinates;
  private final Random random;
  private final Strategy strategy;
  private final int chunkSize;
  private final int minPlacementDistance;

  @Getter @Setter private boolean isLoaded;

  public enum LocationType {
    EMPTY,
    SETTLEMENT
  }

  public enum Strategy {
    DEFAULT,
    NONE,
  }

  public Chunk(Pair<Integer, Integer> worldCoords, ChunkHandler chunkHandler) {
    this(worldCoords, chunkHandler, Strategy.DEFAULT);
  }

  public Chunk(
      Pair<Integer, Integer> worldCoords,
      ChunkHandler chunkHandler,
      Strategy strategy) {
    var chunkProperties = chunkHandler.getAppProperties().getChunkProperties();
    var seed = SeedBuilder.seedFrom(worldCoords);
    this.coordinates = new Coordinates(worldCoords, Coordinates.CoordType.WORLD);
    this.random = new Random(seed);
    this.id = IdBuilder.idFrom(this.getClass(), coordinates);
    this.chunkHandler = chunkHandler;
    this.density = randomDensity(chunkProperties.density());
    this.chunkSize = chunkProperties.size();
    this.minPlacementDistance = chunkProperties.minPlacementDistance();
    this.strategy = strategy;
    this.plane = new int[chunkSize][chunkSize];
    load();
  }

  @Override
  public void load() {
    chunkHandler.populate(this, strategy);
    setLoaded(true);
    logResult();
  }

  @Override
  public void unload() {
    setLoaded(false);
    logResult();
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

  public Settlement getCentralLocation(World world, Graph<AbstractLocation> map) {
    var globalCoords = world.getCurrentChunk().getCoordinates().getGlobal();
    var startVertex = chunkHandler.closestLocationTo(globalCoords, map.getVertices());
    var centralLocation = startVertex.getLocation();
    if (centralLocation != null) {
      log.info("Found central location: {}", centralLocation.getBriefSummary());
      centralLocation.load();
      return (Settlement) centralLocation;
    }
    throw new IllegalStateException("Could not find any central location");
  }

  public void place(Pair<Integer, Integer> chunkCoords, LocationType type) {
    var x = chunkCoords.getFirst();
    var y = chunkCoords.getSecond();
    if (isValidPosition(x, y)) {
      plane[x][y] = type.ordinal();
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
        if (isValidPosition(neighborX, neighborY)
            && plane[neighborX][neighborY] > LocationType.EMPTY.ordinal()) {
          return true;
        }
      }
    }
    return false;
  }

  public int randomDensity(Bounds density) {
    return random.nextInt(density.getUpper() - density.getLower() + 1) + density.getLower();
  }
}
