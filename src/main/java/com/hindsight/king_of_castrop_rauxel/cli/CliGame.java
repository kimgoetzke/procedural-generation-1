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
  private final ChoosePoiLoop choosePoiLoop;
  private final PoiLoop poiLoop;
  private final DialogueLoop dialogueLoop;
  private final CombatLoop combatLoop;
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
        case CHOOSING_POI -> choosePoiLoop.execute(actions);
        case AT_POI -> poiLoop.execute(actions);
        case IN_DIALOGUE -> dialogueLoop.execute(actions);
        case IN_COMBAT -> combatLoop.execute(actions);
        case DEBUGGING -> debugLoop.execute(actions);
      }
    }
  }

  private void initialise() {
    world.generateChunk(world.getCentreCoords(), map);
    world.setCurrentChunk(world.getCentreCoords());
    var startLocation = world.getCurrentChunk().getCentralLocation(world, map);
    var worldCoordinates = world.getCurrentChunk().getCoordinates().getWorld();
    player = new Player("Traveller", startLocation, worldCoordinates);
    dialogueLoop.initialise(player);
    poiLoop.initialise(player);
    choosePoiLoop.initialise(player);
    debugLoop.initialise(player);
    combatLoop.initialise(player);
  }
}
