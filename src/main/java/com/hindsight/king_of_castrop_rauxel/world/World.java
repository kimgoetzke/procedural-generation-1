package com.hindsight.king_of_castrop_rauxel.world;


import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

@Slf4j
@NoArgsConstructor
public class World {

  private final Chunk[][] plane =
      new Chunk[WorldBuildingComponent.WORLD_SIZE][WorldBuildingComponent.WORLD_SIZE];

  public void placeCenter(Chunk chunk) {
    var center = getCenter();
    plane[center.getFirst()][center.getSecond()] = chunk;
  }

  public Pair<Integer, Integer> getCenter() {
    return Pair.of(WorldBuildingComponent.WORLD_SIZE / 2, WorldBuildingComponent.WORLD_SIZE / 2);
  }

  private boolean isValidPosition(int x, int y) {
    return x >= 0
        && x < WorldBuildingComponent.WORLD_SIZE
        && y >= 0
        && y < WorldBuildingComponent.WORLD_SIZE;
  }

  public boolean hasNeighbors(int x, int y, int distance) {
    for (int dx = -distance; dx <= distance; dx++) {
      for (int dy = -distance; dy <= distance; dy++) {
        int neighborX = x + dx;
        int neighborY = y + dy;
        if (isValidPosition(neighborX, neighborY) && plane[neighborX][neighborY] != null) {
          return true;
        }
      }
    }
    return false;
  }
}
