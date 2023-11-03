package com.hindsight.king_of_castrop_rauxel.world;

import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.graphs.Vertex;
import com.hindsight.king_of_castrop_rauxel.location.Location;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

import java.util.List;

@Slf4j
public class World {

  private final ChunkHandler chunkHandler;
  private final Chunk[][] plane;
  private final int worldSize;
  private final int chunkSize;
  private final int retentionZone;
  private final boolean autoUnload;

  @Getter private Chunk currentChunk;

  public World(AppProperties appProperties, ChunkHandler chunkHandler) {
    this.chunkHandler = chunkHandler;
    this.worldSize = appProperties.getWorldProperties().size();
    this.chunkSize = appProperties.getChunkProperties().size();
    this.autoUnload = appProperties.getGeneralProperties().autoUnload();
    this.retentionZone = appProperties.getWorldProperties().retentionZone();
    this.plane = new Chunk[worldSize][worldSize];
  }

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
    return Pair.of(worldSize / 2, worldSize / 2);
  }

  public void generateChunk(Pair<Integer, Integer> worldCoords, Graph map) {
    throwErrorIfChunkExists(worldCoords);
    var stats = getStats(map);
    var chunk = new Chunk(worldCoords, chunkHandler);
    place(chunk);
    logOutcome(stats, map, this.getClass());
  }

  public void generateChunk(CardinalDirection where, Graph map) {
    var worldCoords = getCoordsFor(where);
    throwErrorIfChunkExists(worldCoords);
    var stats = getStats(map);
    var nextChunk = new Chunk(getCoordsFor(where), chunkHandler);
    place(nextChunk, where);
    logOutcome(stats, map, this.getClass());
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
    var newCoords = getCoordsFor(where);
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
    if (autoUnload) {
      unloadChunks();
    }
  }

  private void unloadChunks() {
    log.info("Unloading chunks outside retention zone...");
    var x = (int) currentChunk.getCoordinates().getWorld().getFirst();
    var y = (int) currentChunk.getCoordinates().getWorld().getSecond();
    for (int i = 0; i < worldSize; i++) {
      for (int j = 0; j < worldSize; j++) {
        if (isValidPosition(i, j)
            && hasLoadedChunk(Pair.of(i, j))
            && isInsideRemovalZone(i, x, j, y)) {
          getChunk(Pair.of(i, j)).unload();
        }
      }
    }
  }

  private boolean isInsideRemovalZone(int targetX, int currX, int targetY, int currY) {
    return Math.abs(targetX - currX) > retentionZone || Math.abs(targetY - currY) > retentionZone;
  }

  private boolean isValidPosition(int x, int y) {
    return x >= 0 && x < chunkSize && y >= 0 && y < chunkSize;
  }

  /**
   * Returns the world coordinates of the chunk in the given position relative to the current chunk.
   */
  private Pair<Integer, Integer> getCoordsFor(CardinalDirection where) {
    var x = (int) currentChunk.getCoordinates().getWorld().getFirst();
    var y = (int) currentChunk.getCoordinates().getWorld().getSecond();
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

  private void throwErrorIfChunkExists(Pair<Integer, Integer> worldCoords) {
    if (hasChunk(worldCoords)) {
      throw new IllegalStateException(
          String.format(
              "Chunk %s of w(%d, %d) already exists",
              getChunk(worldCoords).getSummary().toLowerCase(),
              worldCoords.getFirst(),
              worldCoords.getSecond()));
    }
  }

  private LogStats getStats(Graph map) {
    return new LogStats(
        System.currentTimeMillis(),
        map.getVertices().size(),
        map.getVertices().stream().map(Vertex::getLocation).toList());
  }

  private <T> void logOutcome(LogStats stats, Graph map, Class<T> clazz) {
    log.info("Generation took {} seconds", (System.currentTimeMillis() - stats.startT) / 1000.0);
    if (clazz.equals(World.class)) {
      log.info("Generated {} settlements", map.getVertices().size() - stats.prevSetCount);
    }
  }

  private record LogStats(long startT, int prevSetCount, List<? extends Location> prevSet) {}
}
