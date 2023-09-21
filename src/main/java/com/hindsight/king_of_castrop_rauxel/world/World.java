package com.hindsight.king_of_castrop_rauxel.world;

import static com.hindsight.king_of_castrop_rauxel.world.WorldBuildingComponent.*;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

@Slf4j
@NoArgsConstructor
public class World {

  @Getter private Chunk currentChunk;
  private final Chunk[][] plane = new Chunk[WORLD_SIZE][WORLD_SIZE];

  public boolean hasChunk(CardinalDirection position) {
    var x = (int) currentChunk.getCoordinates().getWorld().getFirst();
    var y = (int) currentChunk.getCoordinates().getWorld().getSecond();
    return switch (position) {
      case NORTH -> plane[x][y + 1] != null;
      case EAST -> plane[x + 1][y] != null;
      case SOUTH -> plane[x][y - 1] != null;
      case WEST -> plane[x - 1][y] != null;
      default -> true;
    };
  }

  public Chunk getChunk(CardinalDirection position) {
    var x = (int) currentChunk.getCoordinates().getWorld().getFirst();
    var y = (int) currentChunk.getCoordinates().getWorld().getSecond();
    return switch (position) {
      case THIS -> plane[x][y];
      case NORTH -> plane[x][y + 1];
      case EAST -> plane[x + 1][y];
      case SOUTH -> plane[x][y - 1];
      case WEST -> plane[x - 1][y];
      default -> throw new IllegalStateException("Unexpected value: " + position);
    };
  }

  private Chunk getChunk(Pair<Integer, Integer> worldCoords) {
    return plane[worldCoords.getFirst()][worldCoords.getSecond()];
  }

  public Pair<Integer, Integer> getCenter() {
    return Pair.of(WORLD_SIZE / 2, WORLD_SIZE / 2);
  }

  public Pair<Integer, Integer> getPosition(CardinalDirection position) {
    return getPosition(position, currentChunk);
  }

  public void placeChunk(Chunk chunk) {
    currentChunk = chunk;
    var center = getCenter();
    plane[center.getFirst()][center.getSecond()] = chunk;
  }

  public void placeChunk(Chunk chunk, CardinalDirection position) {
    var newCoords = getPosition(position, currentChunk);
    if (position == CardinalDirection.THIS) {
      throw new IllegalStateException(
          "Unexpected coordinates for placing a new chunk: "
              + position
              + " - this would overwrite the current chunk");
    }
    plane[newCoords.getFirst()][newCoords.getSecond()] = chunk;
  }

  public void setCurrentChunk(Pair<Integer, Integer> worldCoords) {
    var chunk = getChunk(worldCoords);
    if (chunk == null) {
      throw new IllegalStateException(
          "Current chunk can't be set to %s because there is no chunk".formatted(worldCoords));
    }
    currentChunk = chunk;
  }

  private static Pair<Integer, Integer> getPosition(CardinalDirection position, Chunk chunk) {
    var x = (int) chunk.getCoordinates().getWorld().getFirst();
    var y = (int) chunk.getCoordinates().getWorld().getSecond();
    return switch (position) {
      case THIS -> Pair.of(x, y);
      case NORTH -> Pair.of(x, y + 1);
      case EAST -> Pair.of(x + 1, y);
      case SOUTH -> Pair.of(x, y - 1);
      case WEST -> Pair.of(x - 1, y);
      default -> throw new IllegalStateException("Unexpected value: " + position);
    };
  }
}
