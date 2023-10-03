package com.hindsight.king_of_castrop_rauxel.cli;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.cli.loop.*;
import com.hindsight.king_of_castrop_rauxel.configuration.EnvironmentResolver;
import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.location.AbstractLocation;
import com.hindsight.king_of_castrop_rauxel.world.World;
import java.util.ArrayList;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired), access = AccessLevel.PRIVATE)
public class CliGame {

  private final EnvironmentResolver environmentResolver;
  private final World world;
  private final Graph<AbstractLocation> map;
  private final LocationLoop locationLoop;
  private final ChoosePoiLoop choosePoiLoop;
  private final PoiLoop poiLoop;
  private final DialogueLoop dialogueLoop;
  private final DebugLoop debugLoop;
  private Player player;

  @SuppressWarnings("InfiniteLoopStatement")
  public void play() {
    if (environmentResolver.isNotCli()) {
      log.info("Not running in CLI mode, CLI game will not be started");
      return;
    }
    var actions = new ArrayList<Action>();
    initialise();
    while (true) {
      switch (player.getState()) {
        case AT_LOCATION -> locationLoop.execute(actions);
        case CHOOSE_POI -> choosePoiLoop.execute(actions);
        case AT_POI -> poiLoop.execute(actions);
        case DIALOGUE -> dialogueLoop.execute(actions);
        case DEBUG -> debugLoop.execute(actions);
      }
    }
  }

  // TODO: Add single-step dialogue, multi-step dialogue, kill quest and go-to quest
  private void initialise() {
    world.generateChunk(world.getCentreCoords(), map);
    world.setCurrentChunk(world.getCentreCoords());
    var startLocation = world.getCurrentChunk().getCentralLocation(world, map);
    var worldCoordinates = world.getCurrentChunk().getCoordinates().getWorld();
    player = new Player("Traveller", startLocation, worldCoordinates);
    locationLoop.initialise(player);
    dialogueLoop.initialise(player);
    poiLoop.initialise(player);
    choosePoiLoop.initialise(player);
    debugLoop.initialise(player);
  }
}
