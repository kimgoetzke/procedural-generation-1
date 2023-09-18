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

  public void printWorld() {
    ChunkComponent.log(
        world.getChunk(WorldBuildingComponent.RelativePosition.LEFT),
        WorldBuildingComponent.RelativePosition.LEFT);
    ChunkComponent.log(
        world.getChunk(WorldBuildingComponent.RelativePosition.ABOVE),
        WorldBuildingComponent.RelativePosition.ABOVE);
    ChunkComponent.log(
        world.getChunk(WorldBuildingComponent.RelativePosition.RIGHT),
        WorldBuildingComponent.RelativePosition.RIGHT);
    ChunkComponent.log(
        world.getChunk(WorldBuildingComponent.RelativePosition.BELOW),
        WorldBuildingComponent.RelativePosition.BELOW);
    ChunkComponent.log(
        world.getChunk(WorldBuildingComponent.RelativePosition.THIS),
        WorldBuildingComponent.RelativePosition.THIS);
  }
}
