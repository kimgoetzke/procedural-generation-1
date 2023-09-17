package com.hindsight.king_of_castrop_rauxel.world;

import java.util.Random;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

@Slf4j
public class Chunk {

  @Getter private final int density;
  private final Random random;
  private final int[][] plane = new int[ChunkComponent.CHUNK_SIZE][ChunkComponent.CHUNK_SIZE];

  public Chunk(Random random, int density) {
    this.random = random;
    this.density = density;
  }

  public enum LocationType {
    EMPTY,
    SETTLEMENT
  }

  public Pair<Integer, Integer> getCenter() {
    return Pair.of(ChunkComponent.CHUNK_SIZE / 2, ChunkComponent.CHUNK_SIZE / 2);
  }

  public Pair<Integer, Integer> getRandomCoordinates(LocationType type) {
    var x = -1;
    var y = -1;
    while (!isValidPosition(x, y) || hasNeighbors(x, y, ChunkComponent.MIN_PLACEMENT_DISTANCE)) {
      x = random.nextInt(ChunkComponent.CHUNK_SIZE + 1);
      y = random.nextInt(ChunkComponent.CHUNK_SIZE + 1);
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
    return x >= 0 && x < ChunkComponent.CHUNK_SIZE && y >= 0 && y < ChunkComponent.CHUNK_SIZE;
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
