package com.hindsight.king_of_castrop_rauxel.world;

import static com.hindsight.king_of_castrop_rauxel.configuration.AppConstants.CHUNK_SIZE;
import static com.hindsight.king_of_castrop_rauxel.configuration.AppConstants.RETENTION_ZONE;
import static com.hindsight.king_of_castrop_rauxel.configuration.AppConstants.WORLD_SIZE;

import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.location.AbstractLocation;
import com.hindsight.king_of_castrop_rauxel.world.WorldHandler.CardinalDirection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

@Slf4j
@RequiredArgsConstructor
public class World {

  private final AppProperties appProperties;
  private final WorldHandler worldHandler;

  @Getter private Chunk currentChunk;
  private final Chunk[][] plane = new Chunk[WORLD_SIZE][WORLD_SIZE];

  public boolean hasChunk(CardinalDirection where) {
    var x = (int) currentChunk.getCoordinates().getWorld().getFirst();
    var y = (int) currentChunk.getCoordinates().getWorld().getSecond();
    return switch (where) {
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

  public boolean hasChunk(Pair<Integer, Integer> worldCoords) {
    return getChunk(worldCoords) != null;
  }

  public boolean hasLoadedChunk(CardinalDirection where) {
    var chunk = getChunk(where);
    return chunk != null && chunk.isLoaded();
  }

  public boolean hasLoadedChunk(Pair<Integer, Integer> worldCoords) {
    var chunk = getChunk(worldCoords);
    return chunk != null && chunk.isLoaded();
  }

  public Chunk getChunk(CardinalDirection where) {
    var x = (int) currentChunk.getCoordinates().getWorld().getFirst();
    var y = (int) currentChunk.getCoordinates().getWorld().getSecond();
    return switch (where) {
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

  /** Returns the world coordinates of the chunk in the center of the world. */
  public Pair<Integer, Integer> getCentreCoords() {
    return Pair.of(WORLD_SIZE / 2, WORLD_SIZE / 2);
  }

  /**
   * Returns the world coordinates of the chunk in the given position relative to the current chunk.
   */
  public Pair<Integer, Integer> getCoordsFor(CardinalDirection where) {
    return getCoordsFor(where, currentChunk);
  }

  public void generateChunk(Pair<Integer, Integer> worldCoords, Graph<AbstractLocation> map) {
    throwErrorIfChunkExists(worldCoords, this);
    var stats = worldHandler.getStats(map);
    var chunk = new Chunk(worldCoords, worldHandler);
    place(chunk);
    chunk.logResult();
    worldHandler.logOutcome(stats, map, this.getClass());
  }

  public void generateChunk(CardinalDirection where, Graph<AbstractLocation> map) {
    throwErrorIfChunkExists(where, this);
    var stats = worldHandler.getStats(map);
    var nextChunk = new Chunk(getCoordsFor(where), worldHandler);
    place(nextChunk, where);
    worldHandler.logOutcome(stats, map, this.getClass());
  }

  /** Places a chunk in the center of the world. Used to place the first chunk in a new world. */
  private void place(Chunk chunk) {
    var center = getCentreCoords();
    plane[center.getFirst()][center.getSecond()] = chunk;
  }

  /**
   * Places a chunk in the given position relative to the current chunk. Used to place any
   * subsequently created chunks in an existing world.
   */
  private void place(Chunk chunk, CardinalDirection where) {
    var newCoords = getCoordsFor(where, currentChunk);
    if (where == CardinalDirection.THIS) {
      log.warn(
          "You are placing a chunk in the same position as the current chunk, if it exists - if this is intentional, you must setCurrentChunk first");
    }
    plane[newCoords.getFirst()][newCoords.getSecond()] = chunk;
  }

  /**
   * Places a chunk in the specified position. Mostly used when testing but would be used more
   * frequently if more than one player was supported.
   */
  public void place(Chunk chunk, Pair<Integer, Integer> worldCoords) {
    plane[worldCoords.getFirst()][worldCoords.getSecond()] = chunk;
  }

  public void setCurrentChunk(Pair<Integer, Integer> worldCoords) {
    var chunk = getChunk(worldCoords);
    if (chunk == null) {
      throw new IllegalStateException(
          "%s cannot be the current chunk because it is null".formatted(worldCoords));
    }
    if (!chunk.isLoaded()) {
      chunk.load();
    }
    currentChunk = chunk;
    log.info("Set current chunk to: {}", chunk.getSummary());
    if (appProperties.getAutoUnload().isWorld()) {
      unloadChunks();
    }
  }

  private void unloadChunks() {
    log.info("Unloading chunks outside retention zone...");
    var x = (int) currentChunk.getCoordinates().getWorld().getFirst();
    var y = (int) currentChunk.getCoordinates().getWorld().getSecond();
    for (int i = 0; i < WORLD_SIZE; i++) {
      for (int j = 0; j < WORLD_SIZE; j++) {
        if (isValidPosition(i, j)
            && hasLoadedChunk(Pair.of(i, j))
            && isInsideRemovalZone(i, x, j, y)) {
          getChunk(Pair.of(i, j)).unload();
        }
      }
    }
  }

  private static boolean isInsideRemovalZone(int targetX, int currX, int targetY, int currY) {
    return Math.abs(targetX - currX) > RETENTION_ZONE || Math.abs(targetY - currY) > RETENTION_ZONE;
  }

  private boolean isValidPosition(int x, int y) {
    return x >= 0 && x < CHUNK_SIZE && y >= 0 && y < CHUNK_SIZE;
  }

  private static Pair<Integer, Integer> getCoordsFor(CardinalDirection where, Chunk chunk) {
    var x = (int) chunk.getCoordinates().getWorld().getFirst();
    var y = (int) chunk.getCoordinates().getWorld().getSecond();
    return switch (where) {
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

  private static void throwErrorIfChunkExists(CardinalDirection where, World world) {
    if (world.hasChunk(where)) {
      throw new IllegalStateException(
          String.format(
              "Chunk %s of w(%d, %d) already exists",
              where.name().toLowerCase(),
              world.getCoordsFor(where).getFirst(),
              world.getCoordsFor(where).getSecond()));
    }
  }

  private static void throwErrorIfChunkExists(Pair<Integer, Integer> worldCoords, World world) {
    if (world.hasChunk(worldCoords)) {
      throw new IllegalStateException(
          String.format(
              "Chunk at w(%d, %d) already exists",
              worldCoords.getFirst(), worldCoords.getSecond()));
    }
  }
}
