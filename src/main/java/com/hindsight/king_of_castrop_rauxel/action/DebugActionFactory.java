package com.hindsight.king_of_castrop_rauxel.action;

import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
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
}
