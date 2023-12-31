package com.hindsight.king_of_castrop_rauxel.action.debug;

import static com.hindsight.king_of_castrop_rauxel.cli.CliComponent.*;
import static com.hindsight.king_of_castrop_rauxel.world.Coordinates.*;

import com.hindsight.king_of_castrop_rauxel.character.Player;
import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.configuration.EnvironmentResolver;
import com.hindsight.king_of_castrop_rauxel.graph.Graph;
import com.hindsight.king_of_castrop_rauxel.graph.Vertex;
import com.hindsight.king_of_castrop_rauxel.world.CardinalDirection;
import com.hindsight.king_of_castrop_rauxel.world.Chunk;
import com.hindsight.king_of_castrop_rauxel.world.ChunkHandler;
import com.hindsight.king_of_castrop_rauxel.world.World;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DebugActionFactory {

  private final Graph graph;
  private final World world;
  private final ChunkHandler chunkHandler;
  private final AppProperties appProperties;
  private final EnvironmentResolver environmentResolver;

  public DebugAction create(int index, String name, Runnable runnable) {
    return DebugAction.builder()
        .environment(environmentResolver.getEnvironment())
        .index(index)
        .name(name)
        .runnable(runnable)
        .graph(graph)
        .world(world)
        .build();
  }

  public void logGraph() {
    graph.log();
  }

  public void printConnectivity() {
    chunkHandler.logDisconnectedVertices(graph);
  }

  public void logVertices() {
    log.info("All locations/vertices:");
    graph.getVertices().stream().map(Vertex::getDto).forEach(l -> log.info("- " + l.getSummary()));
  }

  public void logMemoryStats() {
    var rt = Runtime.getRuntime();
    var totalMemory = rt.totalMemory();
    var freeMemory = rt.freeMemory();
    var usedMemory = totalMemory - freeMemory;
    log.info(String.format("Total memory in JVM: %.2f MB", totalMemory / (1024.0 * 1024)));
    log.info(String.format("- Free:  %.2f MB", freeMemory / (1024.0 * 1024)));
    log.info(String.format("- Used:  %.2f MB", usedMemory / (1024.0 * 1024)));
  }

  public void logWorld() {
    log(world.getChunk(CardinalDirection.WEST), CardinalDirection.WEST);
    log(world.getChunk(CardinalDirection.NORTH), CardinalDirection.NORTH);
    log(world.getChunk(CardinalDirection.EAST), CardinalDirection.EAST);
    log(world.getChunk(CardinalDirection.SOUTH), CardinalDirection.SOUTH);
    log(world.getChunk(CardinalDirection.THIS), CardinalDirection.THIS);
  }

  public static void log(Chunk chunk, CardinalDirection where) {
    if (chunk == null) {
      log.info("{} chunk does not exist yet", where);
      return;
    }
    log.info(
        "{} chunk at {} has a density of {} and {} settlements",
        where,
        chunk.getCoordinates(),
        chunk.getDensity(),
        chunk.getLocations().size());
  }

  public void logPlane() {
    logPlane(world);
  }

  public void logPlane(World world) {
    var chunk = world.getChunk(CardinalDirection.THIS);
    var scale = 10;
    var chunkSize = appProperties.getChunkProperties().size();
    var downscaledPlane = new String[chunkSize / scale][chunkSize / scale];
    var numRows = downscaledPlane.length;
    var numCols = downscaledPlane[0].length;
    var locationCount = 0;

    // Shrink data into the new array and convert to location names
    for (var i = 0; i < chunkSize; i++) {
      for (var j = 0; j < chunkSize; j++) {
        var l = chunk.findLocation(Pair.of(i, j));
        if (l != null) {
          downscaledPlane[i / scale][j / scale] = l.getName();
        }
      }
    }

    // Print column numbers
    log.info("Visualising: {}", chunk.getSummary());
    var sb = new StringBuilder();
    sb.append("   ");
    for (int col = 0; col < numCols; col++) {
      sb.append("%3d".formatted(col));
    }
    log.info(sb.toString());

    // Print row numbers and array contents
    for (var row = 0; row < numRows; row++) {
      sb = new StringBuilder();
      sb.append("%2d|".formatted(row));
      for (var col = 0; col < numCols; col++) {
        if (downscaledPlane[row][col] == null) {
          sb.append("   ");
          continue;
        }
        sb.append(
            "%s%s%s".formatted(FMT.CYAN, downscaledPlane[row][col].substring(0, 3), FMT.RESET));
        locationCount++;
      }
      log.info(sb.toString());
    }

    log.info("");
    log.info("- Locations in this chunk: " + locationCount);
  }

  public void logVisitedLocations(Player player) {
    log.info("Visited locations:");
    player.getVisitedLocations().forEach(l -> log.info("- " + l.getName()));
  }

  public void addGold(Player player) {
    player.changeGoldBy(1000);
    log.info("Gifted 1000 gold to player {}", player.getName());
  }

  public void setHealth(Player player) {
    player.setHealth(1);
    log.info("Set health of player {} to 1", player.getName());
  }

  public void logLocationsInsideTriggerZone(Player player) {
    var vertices =
        graph.getVertices().stream()
            .map(Vertex::getDto)
            .filter(l -> chunkHandler.isInsideTriggerZone((l.coordinates().getChunk())))
            .toList();
    var allPlayerCords = player.getCoordinates();
    var whereNext = chunkHandler.nextChunkPosition(allPlayerCords.getChunk());
    var whereNextCoords = world.getChunk(whereNext).getCoordinates();
    if (whereNext == CardinalDirection.THIS) {
      log.info("Player is not inside any trigger zone of {}", whereNextCoords.worldToString());
    } else {
      log.info(
          "Player is inside trigger zone for chunk {} of {}:",
          whereNext.toString().toUpperCase(),
          allPlayerCords.worldToString());
      vertices.stream()
          .filter(l -> l.coordinates().equalTo(whereNextCoords.getWorld(), CoordType.WORLD))
          .filter(l -> chunkHandler.isInsideTriggerZone(l.coordinates().getChunk()))
          .forEach(l -> log.info("- " + l.getSummary()));
    }
  }

  public void logWorldLevels() {
    logWorldLevels(world);
  }

  public void logWorldLevels(World world) {
    var chunk = world.getCurrentChunk();
    var radius = 10;
    var startX = chunk.getCoordinates().wX() - radius;
    var endX = chunk.getCoordinates().wY() + radius;
    var startY = chunk.getCoordinates().wX() - radius;
    var endY = chunk.getCoordinates().wY() + radius;

    // Print column numbers
    log.info("Visualising world target level values within radius of {} chunks: ", radius);
    var sb = new StringBuilder();
    sb.append("  ");
    for (var col = startX; col < endX; col++) {
      sb.append("%3d".formatted(col));
    }
    log.info(sb.toString());

    // Print row numbers and array contents
    for (var row = startX; row < endX; row++) {
      sb = new StringBuilder();
      sb.append("%2d|".formatted(row));
      for (var col = startY; col < endY; col++) {
        var targetLevel = getProcessedTargetLevelString(row, col, world);
        sb.append("%s%s%s".formatted(FMT.CYAN, targetLevel, FMT.RESET));
      }
      log.info(sb.toString());
    }
  }

  private static String getProcessedTargetLevelString(int i, int j, World world) {
    var worldCoords = Pair.of(i, j);
    if (world.hasChunk(worldCoords)) {
      var rawStr = String.valueOf(world.getChunk(worldCoords).getTargetLevel());
      return rawStr.length() == 1 ? " %s ".formatted(rawStr) : rawStr;
    } else {
      return " X ";
    }
  }
}
