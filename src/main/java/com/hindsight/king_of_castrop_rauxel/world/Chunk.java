package com.hindsight.king_of_castrop_rauxel.world;

import static com.hindsight.king_of_castrop_rauxel.world.ChunkComponent.*;

import java.util.Random;

import com.hindsight.king_of_castrop_rauxel.location.Generatable;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

@Slf4j
public class Chunk implements Generatable {

  @Getter private final int density;

  @Getter private final int[][] plane = new int[CHUNK_SIZE][CHUNK_SIZE];

  private Random random;
  @Getter @Setter private Pair<Integer, Integer> worldCoords;
  @Getter @Setter private boolean isLoaded;

  public Chunk(int density, Pair<Integer, Integer> worldCoords) {
    var seed = SeedComponent.seedFrom(worldCoords);
    this.worldCoords = worldCoords;
    this.random = new Random(seed);
    this.density = density;
    load();
  }

  @Override
  public void load() {
    log.info("Generating chunk...");
    setLoaded(true);
    logResult();
  }

  @Override
  public void unload() {
    var seed = SeedComponent.seedFrom(worldCoords);
    random = new Random(seed);
    setLoaded(false);
    logResult();
  }

  @Override
  public void logResult() {
    var action = isLoaded ? "Generated" : "Unloaded";
    log.info("{}: Chunk at ({}, {})", action, worldCoords.getFirst(), worldCoords.getSecond());
  }

  public enum LocationType {
    EMPTY,
    SETTLEMENT
  }

  public Pair<Integer, Integer> getCenter() {
    return Pair.of(CHUNK_SIZE / 2, CHUNK_SIZE / 2);
  }

  public Pair<Integer, Integer> getRandomCoordinates(LocationType type) {
    var x = -1;
    var y = -1;
    while (!isValidPosition(x, y) || hasNeighbors(x, y, MIN_PLACEMENT_DISTANCE)) {
      x = random.nextInt(CHUNK_SIZE + 1);
      y = random.nextInt(CHUNK_SIZE + 1);
    }
    plane[x][y] = type.ordinal();
    return Pair.of(x, y);
  }

  public void place(int x, int y, LocationType type) {
    if (isValidPosition(x, y)) {
      plane[x][y] = type.ordinal();
    }
  }

  private boolean isValidPosition(int x, int y) {
    return x >= 0 && x < CHUNK_SIZE && y >= 0 && y < CHUNK_SIZE;
  }

  public boolean hasNeighbors(int x, int y, int distance) {
    for (int dx = -distance; dx <= distance; dx++) {
      for (int dy = -distance; dy <= distance; dy++) {
        int neighborX = x + dx;
        int neighborY = y + dy;
        if (isValidPosition(neighborX, neighborY)
            && plane[neighborX][neighborY] > LocationType.EMPTY.ordinal()) {
          return true;
        }
      }
    }
    return false;
  }

  public int calculateDistance(Pair<Integer, Integer> start, Pair<Integer, Integer> end) {
    int deltaX = end.getFirst() - start.getFirst();
    int deltaY = end.getSecond() - start.getSecond();
    double distance = Math.sqrt((double) deltaX * deltaX + deltaY * deltaY);
    return (int) Math.round(distance);
  }
}
