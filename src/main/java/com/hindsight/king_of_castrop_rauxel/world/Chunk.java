package com.hindsight.king_of_castrop_rauxel.world;

import static com.hindsight.king_of_castrop_rauxel.configuration.AppConstants.*;

import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.location.AbstractLocation;
import com.hindsight.king_of_castrop_rauxel.location.Settlement;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

@Slf4j
public class Chunk implements Generatable {

  private final WorldHandler worldHandler;

  @Getter private final String id;
  @Getter private final int density;
  @Getter private final int[][] plane = new int[CHUNK_SIZE][CHUNK_SIZE];
  @Getter private final Coordinates coordinates;
  private final Random random;
  private final WorldHandler.Strategy strategy;

  @Getter @Setter private boolean isLoaded;

  public enum LocationType {
    EMPTY,
    SETTLEMENT
  }

  public Chunk(
      Pair<Integer, Integer> worldCoords,
      WorldHandler worldHandler,
      WorldHandler.Strategy strategy) {
    var seed = SeedBuilder.seedFrom(worldCoords);
    this.coordinates = new Coordinates(worldCoords, Coordinates.CoordType.WORLD);
    this.random = new Random(seed);
    this.density = ChunkBuilder.randomDensity(random);
    this.id = IdBuilder.idFrom(this.getClass(), coordinates);
    this.worldHandler = worldHandler;
    this.strategy = strategy;
    load();
  }

  public Chunk(Pair<Integer, Integer> worldCoords, WorldHandler worldHandler) {
    var seed = SeedBuilder.seedFrom(worldCoords);
    this.coordinates = new Coordinates(worldCoords, Coordinates.CoordType.WORLD);
    this.random = new Random(seed);
    this.density = ChunkBuilder.randomDensity(random);
    this.id = IdBuilder.idFrom(this.getClass(), coordinates);
    this.worldHandler = worldHandler;
    this.strategy = WorldHandler.Strategy.DEFAULT;
    load();
  }

  @Override
  public void load() {
    worldHandler.populate(this, strategy);
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
    return Pair.of(CHUNK_SIZE / 2, CHUNK_SIZE / 2);
  }

  public Pair<Integer, Integer> getRandomCoords() {
    var x = -1;
    var y = -1;
    while (!isValidPosition(x, y) || hasNeighbors(x, y, MIN_PLACEMENT_DISTANCE)) {
      x = random.nextInt(CHUNK_SIZE + 1);
      y = random.nextInt(CHUNK_SIZE + 1);
    }
    return Pair.of(x, y);
  }

  public Settlement getCentralLocation(World world, Graph<AbstractLocation> map) {
    var globalCoords = world.getCurrentChunk().getCoordinates().getGlobal();
    var startVertex = worldHandler.closestLocationTo(globalCoords, map.getVertices());
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
    return x >= 0 && x < CHUNK_SIZE && y >= 0 && y < CHUNK_SIZE;
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
}
