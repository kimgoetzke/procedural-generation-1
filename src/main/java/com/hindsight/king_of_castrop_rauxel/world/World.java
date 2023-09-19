package com.hindsight.king_of_castrop_rauxel.world;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

import static com.hindsight.king_of_castrop_rauxel.world.WorldBuildingComponent.*;

@Slf4j
@NoArgsConstructor
public class World {

  @Getter @Setter private Chunk currentChunk;
  private final Chunk[][] plane = new Chunk[WORLD_SIZE][WORLD_SIZE];

  public boolean hasChunk(CardinalDirection position) {
    var x = (int) currentChunk.getWorldCoords().getFirst();
    var y = (int) currentChunk.getWorldCoords().getSecond();
    return switch (position) {
      case NORTH -> plane[x][y + 1] != null;
      case EAST -> plane[x + 1][y] != null;
      case SOUTH -> plane[x][y - 1] != null;
      case WEST -> plane[x - 1][y] != null;
      default -> true;
    };
  }

  public Chunk getChunk(CardinalDirection position) {
    var x = (int) currentChunk.getWorldCoords().getFirst();
    var y = (int) currentChunk.getWorldCoords().getSecond();
    return switch (position) {
      case THIS -> plane[x][y];
      case NORTH -> plane[x][y + 1];
      case EAST -> plane[x + 1][y];
      case SOUTH -> plane[x][y - 1];
      case WEST -> plane[x - 1][y];
      default -> throw new IllegalStateException("Unexpected value: " + position);
    };
  }

  public void placeChunk(Chunk chunk) {
    currentChunk = chunk;
    var center = getCenter();
    plane[center.getFirst()][center.getSecond()] = chunk;
    chunk.setWorldCoords(center);
  }

  public Pair<Integer, Integer> getCenter() {
    return Pair.of(WORLD_SIZE / 2, WORLD_SIZE / 2);
  }

  public Pair<Integer, Integer> getPosition(CardinalDirection position) {
    var x = (int) currentChunk.getWorldCoords().getFirst();
    var y = (int) currentChunk.getWorldCoords().getSecond();
    return getPosition(position, currentChunk.getWorldCoords(), x, y);
  }

  public void placeChunk(Chunk chunk, CardinalDirection position) {
    var x = (int) currentChunk.getWorldCoords().getFirst();
    var y = (int) currentChunk.getWorldCoords().getSecond();
    var coordinates = getPosition(position, currentChunk.getWorldCoords(), x, y);
    if (position == CardinalDirection.THIS) {
      throw new IllegalStateException(
          "Unexpected coordinates for placing a new chunk: "
              + position
              + " - this would overwrite the current chunk");
    }
    plane[coordinates.getFirst()][coordinates.getSecond()] = chunk;
    chunk.setWorldCoords(coordinates);
  }

  private static Pair<Integer, Integer> getPosition(
      CardinalDirection position, Pair<Integer, Integer> coordinates, int x, int y) {
    return switch (position) {
      case THIS -> coordinates;
      case NORTH -> Pair.of(x, y + 1);
      case EAST -> Pair.of(x + 1, y);
      case SOUTH -> Pair.of(x, y - 1);
      case WEST -> Pair.of(x - 1, y);
      default -> throw new IllegalStateException("Unexpected value: " + position);
    };
  }
}
