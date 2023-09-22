package com.hindsight.king_of_castrop_rauxel.action;

import static com.hindsight.king_of_castrop_rauxel.cli.CliComponent.*;
import static com.hindsight.king_of_castrop_rauxel.world.ChunkComponent.*;
import static com.hindsight.king_of_castrop_rauxel.world.WorldBuildingComponent.*;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.graphs.Vertex;
import com.hindsight.king_of_castrop_rauxel.location.AbstractLocation;
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

  private final Graph<AbstractLocation> map;
  private final World world;

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
    logDisconnectedVertices(map);
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
              map.getVertexByValue(Pair.of(i, j)).getLocation().getName();
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
        sb.append("%s%s%s".formatted(FMT.CYAN, downscaledPlane[row][col], FMT.RESET), 0, 3);
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
}
