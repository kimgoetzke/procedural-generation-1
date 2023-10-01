package com.hindsight.king_of_castrop_rauxel.action.debug;

import static com.hindsight.king_of_castrop_rauxel.cli.CliComponent.*;
import static com.hindsight.king_of_castrop_rauxel.configuration.AppConstants.*;
import static com.hindsight.king_of_castrop_rauxel.world.ChunkBuilder.*;
import static com.hindsight.king_of_castrop_rauxel.world.Coordinates.*;
import static com.hindsight.king_of_castrop_rauxel.world.WorldHandler.*;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.graphs.Vertex;
import com.hindsight.king_of_castrop_rauxel.location.AbstractLocation;
import com.hindsight.king_of_castrop_rauxel.world.World;
import com.hindsight.king_of_castrop_rauxel.world.WorldHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DebugActionFactory {

  private final Graph<AbstractLocation> map;
  private final World world;
  private final WorldHandler worldHandler;

  public DebugAction create(int index, String name, Debuggable debuggable) {
    return DebugAction.builder()
        .index(index)
        .name(name)
        .debuggable(debuggable)
        .map(map)
        .world(world)
        .build();
  }

  public void logGraph() {
    map.log();
  }

  public void printConnectivity() {
    worldHandler.logDisconnectedVertices(map);
  }

  public void logVertices() {
    log.info("All locations/vertices:");
    map.getVertices().stream()
        .map(Vertex::getLocation)
        .forEach(l -> log.info("- " + l.getFullSummary()));
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

  public void printPlane() {
    printPlane(world, map);
  }

  public void printPlane(World world, Graph<AbstractLocation> map) {
    var chunk = world.getChunk(CardinalDirection.THIS);
    var plane = chunk.getPlane();
    var scale = 10;
    var downscaledPlane = new String[CHUNK_SIZE / scale][CHUNK_SIZE / scale];
    var numRows = downscaledPlane.length;
    var numCols = downscaledPlane[0].length;
    var locationCount = 0;

    // Shrink data into the new array and convert to location names
    for (int i = 0; i < CHUNK_SIZE; i++) {
      for (int j = 0; j < CHUNK_SIZE; j++) {
        if (plane[i][j] > 0) {
          downscaledPlane[i / scale][j / scale] =
              map.getVertexByValue(Pair.of(i, j), CoordType.CHUNK).getLocation().getName();
        }
      }
    }

    // Print column numbers
    log.info("Visualising: {}", chunk.getSummary());
    StringBuilder sb = new StringBuilder();
    sb.append("   ");
    for (int col = 0; col < numCols; col++) {
      sb.append("%3d".formatted(col));
    }
    log.info(sb.toString());

    // Print row numbers and array contents
    for (int row = 0; row < numRows; row++) {
      sb = new StringBuilder();
      sb.append("%2d|".formatted(row));
      for (int col = 0; col < numCols; col++) {
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

  public void logLocationsInsideTriggerZone(Player player) {
    var vertices =
        map.getVertices().stream()
            .map(Vertex::getLocation)
            .filter(l -> isInsideTriggerZone(l.getCoordinates().getChunk()))
            .toList();
    var allPlayerCords = player.getCoordinates();
    var whereNext = nextChunkPosition(allPlayerCords.getChunk());
    var whereNextAllCoords = world.getChunk(whereNext).getCoordinates();
    if (whereNext == CardinalDirection.THIS) {
      log.info("Player is not inside any trigger zone of {}", whereNextAllCoords.worldToString());
    } else {
      log.info(
          "Player is inside trigger zone for chunk {} of {}:",
          whereNext.toString().toUpperCase(),
          allPlayerCords.worldToString());
      vertices.stream()
          .filter(l -> l.getCoordinates().equalTo(whereNextAllCoords.getWorld(), CoordType.WORLD))
          .filter(l -> isInsideTriggerZone(l.getCoordinates().getChunk()))
          .forEach(l -> log.info("- " + l.getBriefSummary()));
    }
  }
}