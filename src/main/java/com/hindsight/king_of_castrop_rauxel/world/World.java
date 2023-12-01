package com.hindsight.king_of_castrop_rauxel.world;

import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.graph.Graph;
import com.hindsight.king_of_castrop_rauxel.graph.LocationDto;
import com.hindsight.king_of_castrop_rauxel.graph.Vertex;
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
  private final int retentionZone;
  private final boolean autoUnload;

  @Getter private Chunk currentChunk;

  public World(ApplicationContext ctx, Graph graph, AppProperties appProperties) {
    this.ctx = ctx;
    this.graph = graph;
    this.worldSize = appProperties.getWorldProperties().size();
    this.autoUnload = appProperties.getGeneralProperties().autoUnload();
    this.retentionZone = appProperties.getWorldProperties().retentionZone();
    this.plane = new Chunk[worldSize][worldSize];
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

  public void setCurrentChunk(Pair<Integer, Integer> worldCoords) {
    if (!hasChunk(worldCoords)) {
      generateChunk(worldCoords);
    }
    currentChunk = getChunk(worldCoords);
    log.info("Set current chunk to: {}", currentChunk.getSummary());
    loadAllSurroundingChunks();
    currentChunk.load();
    if (autoUnload) {
      unloadChunks();
    }
  }

  private void generateChunk(Pair<Integer, Integer> worldCoords) {
    var stats = getStats(graph);
    var chunkHandler = ctx.getBean(ChunkHandler.class);
    var chunk = ctx.getBean(Chunk.class, worldCoords, chunkHandler);
    placeChunk(chunk, worldCoords);
    logOutcome(stats, graph, this.getClass());
  }

  /** Places a chunk in the specified position on the plane. */
  public void placeChunk(Chunk chunk, Pair<Integer, Integer> worldCoords) {
    plane[worldCoords.getFirst()][worldCoords.getSecond()] = chunk;
  }

  /**
   * Generates (if required) and loads (i.e. places unloaded locations) all chunks around the
   * current chunk which is important to determine the connections between locations on the current
   * chunk conclusively. Without this, connections of an already visited location could change upon
   * loading a neighbouring chunk. Example: A location has one connection outside the neighbour
   * distance but when generating the neighbouring chunk, a closer location is found and connected
   * while the previous connection is dropped.
   */
  private void loadAllSurroundingChunks() {
    log.info("Loading chunks inside retention zone...");
    for (var direction : CardinalDirection.values()) {
      if (direction.equals(CardinalDirection.THIS)) {
        continue;
      }
      var worldCoords = getCoordsFor(direction);
      attemptToGenerateAndLoadChunk(worldCoords);
    }
  }

  private void attemptToGenerateAndLoadChunk(Pair<Integer, Integer> worldCoords) {
    var x = worldCoords.getFirst();
    var y = worldCoords.getSecond();
    if (!hasChunk(worldCoords) && isValidPosition(x, y)) {
      generateChunk(worldCoords);
    }
    if (hasChunk(worldCoords)) {
      getChunk(worldCoords).load();
    } else {
      log.info("No chunk can be generated at c({},{})", x, y);
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
    return x >= 0 && x < worldSize && y >= 0 && y < worldSize;
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
