package com.hindsight.king_of_castrop_rauxel.world;

import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.graphs.LocationDto;
import com.hindsight.king_of_castrop_rauxel.graphs.Vertex;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.data.util.Pair;

@Slf4j
public class World {

  private final ApplicationContext ctx;
  private final Graph graph;
  private final Chunk[][] plane;
  private final int worldSize;
  private final int chunkSize;
  private final int retentionZone;
  private final boolean autoUnload;

  @Getter private Chunk currentChunk;

  public World(ApplicationContext ctx, Graph graph, AppProperties appProperties) {
    this.ctx = ctx;
    this.graph = graph;
    this.worldSize = appProperties.getWorldProperties().size();
    this.chunkSize = appProperties.getChunkProperties().size();
    this.autoUnload = appProperties.getGeneralProperties().autoUnload();
    this.retentionZone = appProperties.getWorldProperties().retentionZone();
    this.plane = new Chunk[worldSize][worldSize];
  }

  public boolean hasChunk(CardinalDirection where) {
    var coords = getCoordsFor(where);
    return hasChunk(coords);
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
    var coords = getCoordsFor(where);
    return getChunk(coords);
  }

  public Chunk getChunk(Pair<Integer, Integer> worldCoords) {
    var x = (int) worldCoords.getFirst();
    var y = (int) worldCoords.getSecond();
    if (!isValidPosition(x, y)) {
      return null;
    }
    return plane[x][y];
  }

  public Chunk getOrGenerateChunk(Pair<Integer, Integer> worldCoords) {
    var chunk = plane[worldCoords.getFirst()][worldCoords.getSecond()];
    if (chunk == null) {
      generateChunk(worldCoords);
    }
    return plane[worldCoords.getFirst()][worldCoords.getSecond()];
  }

  public void setCurrentChunk(Pair<Integer, Integer> worldCoords) {
    if (!hasChunk(worldCoords)) {
      generateChunk(worldCoords);
    }
    currentChunk = getChunk(worldCoords);
    log.info("Set current chunk to: {}", currentChunk.getSummary());
    loadChunksInsideRetentionZone();
    currentChunk.load();
    if (autoUnload) {
      unloadChunks();
    }
  }

  private void generateChunk(Pair<Integer, Integer> worldCoords) {
    var stats = getStats(graph);
    var chunkHandler = ctx.getBean(ChunkHandler.class, graph);
    var chunk = ctx.getBean(Chunk.class, worldCoords, chunkHandler);
    plane[worldCoords.getFirst()][worldCoords.getSecond()] = chunk;
    chunk.load();
    logOutcome(stats, graph, this.getClass());
  }

  /** Places a chunk in the specified position on the plane. */
  public void place(Chunk chunk, Pair<Integer, Integer> worldCoords) {
    plane[worldCoords.getFirst()][worldCoords.getSecond()] = chunk;
  }

  private void loadChunksInsideRetentionZone() {
    log.info("Loading chunks inside retention zone...");
    for (var direction : CardinalDirection.values()) {
      if (direction.equals(CardinalDirection.THIS)) {
        continue;
      }
      var coords = getCoordsFor(direction);
      if (!hasChunk(coords) && isValidPosition(coords.getFirst(), coords.getSecond())) {
        generateChunk(coords);
      }
      if (hasChunk(coords)) {
        getChunk(coords).load();
        continue;
      }
      log.info("No chunk can be generated at c({},{})", coords.getFirst(), coords.getSecond());
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

  /** Returns the world coordinates of the chunk in the center of the world. */
  public Pair<Integer, Integer> getCentreCoords() {
    return Pair.of(worldSize / 2, worldSize / 2);
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

  private LogStats getStats(Graph graph) {
    return new LogStats(
        System.currentTimeMillis(),
        graph.getVertices().size(),
        graph.getVertices().stream().map(Vertex::getDto).toList());
  }

  private <T> void logOutcome(LogStats stats, Graph graph, Class<T> clazz) {
    log.info("Generation took {} seconds", (System.currentTimeMillis() - stats.startT) / 1000.0);
    if (clazz.equals(World.class)) {
      log.info("Generated {} settlements", graph.getVertices().size() - stats.prevSetCount);
    }
  }

  private record LogStats(long startT, int prevSetCount, List<LocationDto> prevSet) {}
}
