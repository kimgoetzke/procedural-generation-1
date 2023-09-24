package com.hindsight.king_of_castrop_rauxel.world;

import static com.hindsight.king_of_castrop_rauxel.configuration.AppConstants.*;

import com.hindsight.king_of_castrop_rauxel.location.Generatable;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

@Slf4j
public class Chunk implements Generatable {

  @Getter private final String id;
  @Getter private final int density;
  @Getter private final int[][] plane = new int[CHUNK_SIZE][CHUNK_SIZE];
  @Getter private final Coordinates coordinates;
  private Random random;
  @Getter @Setter private boolean isLoaded;

  public Chunk(Pair<Integer, Integer> worldCoords) {
    var seed = SeedComponent.seedFrom(worldCoords);
    this.coordinates = new Coordinates(worldCoords, Coordinates.CoordType.WORLD);
    this.random = new Random(seed);
    this.density = ChunkComponent.randomDensity(random);
    this.id = "CHU~" + coordinates.getWorld().getFirst() + coordinates.getWorld().getSecond();
    load();
  }

  @Override
  public void load() {
    log.info("Generating chunk '{}'...", id);
    setLoaded(true);
    logResult();
  }

  @Override
  public void unload() {
    var seed = SeedComponent.seedFrom(coordinates.getGlobal());
    random = new Random(seed);
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
        SeedComponent.seedFrom(coordinates.getGlobal()));
  }

  public String getSummary() {
    return String.format("Chunk '%s' at %s with density %d", id, coordinates, density);
  }

  public enum LocationType {
    EMPTY,
    SETTLEMENT
  }

  public Pair<Integer, Integer> getCenterCoords() {
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
