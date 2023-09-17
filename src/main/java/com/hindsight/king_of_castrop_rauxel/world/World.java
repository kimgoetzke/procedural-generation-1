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

  public boolean hasChunk(RelativePosition position) {
    var x = (int) currentChunk.getWorldCoordinates().getFirst();
    var y = (int) currentChunk.getWorldCoordinates().getSecond();
    return switch (position) {
      case ABOVE -> plane[x][y + 1] != null;
      case RIGHT -> plane[x + 1][y] != null;
      case BELOW -> plane[x][y - 1] != null;
      case LEFT -> plane[x - 1][y] != null;
      default -> true;
    };
  }

  public Chunk getChunk(RelativePosition position) {
    var x = (int) currentChunk.getWorldCoordinates().getFirst();
    var y = (int) currentChunk.getWorldCoordinates().getSecond();
    return switch (position) {
      case THIS -> plane[x][y];
      case ABOVE -> plane[x][y + 1];
      case RIGHT -> plane[x + 1][y];
      case BELOW -> plane[x][y - 1];
      case LEFT -> plane[x - 1][y];
    };
  }

  public void placeChunk(Chunk chunk) {
    currentChunk = chunk;
    var center = getCenter();
    plane[center.getFirst()][center.getSecond()] = chunk;
    chunk.setWorldCoordinates(center);
  }

  public Pair<Integer, Integer> getCenter() {
    return Pair.of(WORLD_SIZE / 2, WORLD_SIZE / 2);
  }

  public Pair<Integer, Integer> getPosition(RelativePosition position) {
    var x = (int) currentChunk.getWorldCoordinates().getFirst();
    var y = (int) currentChunk.getWorldCoordinates().getSecond();
    return getPosition(position, currentChunk.getWorldCoordinates(), x, y);
  }

  public void placeChunk(Chunk chunk, RelativePosition position) {
    var x = (int) currentChunk.getWorldCoordinates().getFirst();
    var y = (int) currentChunk.getWorldCoordinates().getSecond();
    var coordinates = getPosition(position, currentChunk.getWorldCoordinates(), x, y);
    if (position == RelativePosition.THIS) {
      throw new IllegalStateException(
          "Unexpected coordinates for placing a new chunk: "
              + position
              + " - this would overwrite the current chunk");
    }
    plane[coordinates.getFirst()][coordinates.getSecond()] = chunk;
    chunk.setWorldCoordinates(coordinates);
  }

  private static Pair<Integer, Integer> getPosition(
      RelativePosition position, Pair<Integer, Integer> coordinates, int x, int y) {
    return switch (position) {
      case THIS -> coordinates;
      case ABOVE -> Pair.of(x, y + 1);
      case RIGHT -> Pair.of(x + 1, y);
      case BELOW -> Pair.of(x, y - 1);
      case LEFT -> Pair.of(x - 1, y);
    };
  }
}
