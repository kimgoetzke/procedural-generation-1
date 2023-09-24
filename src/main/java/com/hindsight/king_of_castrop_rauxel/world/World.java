package com.hindsight.king_of_castrop_rauxel.world;

import static com.hindsight.king_of_castrop_rauxel.configuration.AppProperties.CHUNK_SIZE;
import static com.hindsight.king_of_castrop_rauxel.configuration.AppProperties.WORLD_SIZE;
import static com.hindsight.king_of_castrop_rauxel.configuration.AppProperties.REMOVAL_ZONE;

import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.world.WorldBuildingComponent.CardinalDirection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

@Slf4j
@RequiredArgsConstructor
public class World {

  private final AppProperties appProperties;

  @Getter private Chunk currentChunk;
  private final Chunk[][] plane = new Chunk[WORLD_SIZE][WORLD_SIZE];

  public boolean hasChunk(CardinalDirection position) {
    var x = (int) currentChunk.getCoordinates().getWorld().getFirst();
    var y = (int) currentChunk.getCoordinates().getWorld().getSecond();
    return switch (position) {
      case THIS -> plane[x][y] != null;
      case NORTH -> plane[x][y + 1] != null;
      case NORTH_EAST -> plane[x + 1][y + 1] != null;
      case EAST -> plane[x + 1][y] != null;
      case SOUTH_EAST -> plane[x + 1][y - 1] != null;
      case SOUTH -> plane[x][y - 1] != null;
      case SOUTH_WEST -> plane[x - 1][y - 1] != null;
      case WEST -> plane[x - 1][y] != null;
      case NORTH_WEST -> plane[x - 1][y + 1] != null;
    };
  }

  public Chunk getChunk(CardinalDirection position) {
    var x = (int) currentChunk.getCoordinates().getWorld().getFirst();
    var y = (int) currentChunk.getCoordinates().getWorld().getSecond();
    return switch (position) {
      case THIS -> plane[x][y];
      case NORTH -> plane[x][y + 1];
      case NORTH_EAST -> plane[x + 1][y + 1];
      case EAST -> plane[x + 1][y];
      case SOUTH_EAST -> plane[x + 1][y - 1];
      case SOUTH -> plane[x][y - 1];
      case SOUTH_WEST -> plane[x - 1][y - 1];
      case WEST -> plane[x - 1][y];
      case NORTH_WEST -> plane[x - 1][y + 1];
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
    if (appProperties.getAutoUnload().isWorld()) {
      removeFarAwayChunks();
    }
    var chunk = getChunk(worldCoords);
    if (chunk == null) {
      throw new IllegalStateException(
          "%s cannot be the current chunk because it is null".formatted(worldCoords));
    }
    currentChunk = chunk;
  }

  private void removeFarAwayChunks() {
    log.info("Unloading far chunks...");
    var x = (int) currentChunk.getCoordinates().getWorld().getFirst();
    var y = (int) currentChunk.getCoordinates().getWorld().getSecond();

    for (int i = 0; i < WORLD_SIZE; i++) {
      for (int j = 0; j < WORLD_SIZE; j++) {
        if ((Math.abs(i - x) >= REMOVAL_ZONE || Math.abs(j - y) >= REMOVAL_ZONE)
            && isValidPosition(i, j)) {
          plane[i][j] = null;
        }
      }
    }
  }

  private boolean isValidPosition(int x, int y) {
    return x >= 0 && x < CHUNK_SIZE && y >= 0 && y < CHUNK_SIZE;
  }

  private static Pair<Integer, Integer> getPosition(CardinalDirection position, Chunk chunk) {
    var x = (int) chunk.getCoordinates().getWorld().getFirst();
    var y = (int) chunk.getCoordinates().getWorld().getSecond();
    return switch (position) {
      case THIS -> Pair.of(x, y);
      case NORTH -> Pair.of(x, y + 1);
      case NORTH_EAST -> Pair.of(x + 1, y + 1);
      case EAST -> Pair.of(x + 1, y);
      case SOUTH_EAST -> Pair.of(x + 1, y - 1);
      case SOUTH -> Pair.of(x, y - 1);
      case SOUTH_WEST -> Pair.of(x - 1, y - 1);
      case WEST -> Pair.of(x - 1, y);
      case NORTH_WEST -> Pair.of(x - 1, y + 1);
    };
  }
}
