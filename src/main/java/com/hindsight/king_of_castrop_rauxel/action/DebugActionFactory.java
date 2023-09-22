package com.hindsight.king_of_castrop_rauxel.action;

import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.graphs.Vertex;
import com.hindsight.king_of_castrop_rauxel.location.AbstractLocation;
import com.hindsight.king_of_castrop_rauxel.world.ChunkComponent;
import com.hindsight.king_of_castrop_rauxel.world.World;
import com.hindsight.king_of_castrop_rauxel.world.WorldBuildingComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

  public void printGraph() {
    map.log();
  }

  public void printConnectivity() {
    WorldBuildingComponent.logDisconnectedVertices(map);
  }

  public void printLocations() {
    map.getVertices().stream()
        .map(Vertex::getLocation)
        .forEach(l -> log.info("- " + l.getFullSummary()));
  }

  public void printMemoryUsage() {
    var rt = Runtime.getRuntime();
    var totalMemory = rt.totalMemory();
    var freeMemory = rt.freeMemory();
    var usedMemory = totalMemory - freeMemory;
    log.info(
        String.format(
            "Memory Usage [ Total: %.2f MB | Free: %.2f MB | Used: %.2f MB ]",
            totalMemory / (1024.0 * 1024),
            freeMemory / (1024.0 * 1024),
            usedMemory / (1024.0 * 1024)));
  }

  public void printWorld() {
    ChunkComponent.log(
        world.getChunk(WorldBuildingComponent.CardinalDirection.WEST),
        WorldBuildingComponent.CardinalDirection.WEST);
    ChunkComponent.log(
        world.getChunk(WorldBuildingComponent.CardinalDirection.NORTH),
        WorldBuildingComponent.CardinalDirection.NORTH);
    ChunkComponent.log(
        world.getChunk(WorldBuildingComponent.CardinalDirection.EAST),
        WorldBuildingComponent.CardinalDirection.EAST);
    ChunkComponent.log(
        world.getChunk(WorldBuildingComponent.CardinalDirection.SOUTH),
        WorldBuildingComponent.CardinalDirection.SOUTH);
    ChunkComponent.log(
        world.getChunk(WorldBuildingComponent.CardinalDirection.THIS),
        WorldBuildingComponent.CardinalDirection.THIS);
  }

  public void printPlane() {
    var chunk = world.getChunk(WorldBuildingComponent.CardinalDirection.THIS);
    var plane = chunk.getPlane();
    var scale = 10;
    var smallerSize = ChunkComponent.CHUNK_SIZE / scale;
    var newPlane = new int[smallerSize][smallerSize];
    int numRows = newPlane.length;
    int numCols = newPlane[0].length;

    // Shrink data into the new array
    for (int i = 0; i < ChunkComponent.CHUNK_SIZE; i++) {
      for (int j = 0; j < ChunkComponent.CHUNK_SIZE; j++) {
        newPlane[i / scale][j / scale] += plane[i][j];
      }
    }

    // Print column numbers
    System.out.print("   ");
    for (int col = 0; col < numCols; col++) {
      System.out.printf("%3d", col);
    }
    System.out.println();

    // Print row numbers and array contents
    for (int row = 0; row < numRows; row++) {
      System.out.printf("%2d|", row);
      for (int col = 0; col < numCols; col++) {
        if (newPlane[row][col] != 0) {
          System.out.print(newPlane[row][col]);
        } else {
          System.out.print("   ");
        }
      }
      System.out.println();
    }
  }
}
