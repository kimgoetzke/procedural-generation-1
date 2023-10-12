package com.hindsight.king_of_castrop_rauxel.cli;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.cli.loop.*;
import com.hindsight.king_of_castrop_rauxel.configuration.EnvironmentResolver;
import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.location.AbstractLocation;
import com.hindsight.king_of_castrop_rauxel.world.World;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import de.codeshelf.consoleui.prompt.ConsolePrompt;
import de.codeshelf.consoleui.prompt.PromtResultItemIF;
import jline.TerminalFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fusesource.jansi.AnsiConsole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.fusesource.jansi.Ansi.ansi;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired), access = AccessLevel.PRIVATE)
public class CliGame {

  // https://github.com/awegmann/consoleui/blob/master/doc/howto.md

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
    AnsiConsole.systemInstall();
    initialise();
    System.out.println(ansi().eraseScreen().render("Simple list example:"));
    var prompt = new ConsolePrompt();
    var promptBuilder = prompt.getPromptBuilder();
    promptBuilder
        .createListPrompt()
        .name("King of Castrop-Rauxel")
        .message("What would you like to do?")
        .newItem("Explore")
        .text("Explore the world")
        .add()
        .addPrompt();


    try {
      HashMap<String, ? extends PromtResultItemIF> result = prompt.prompt(promptBuilder.build());
      System.out.println("result = " + result);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
    try {
      TerminalFactory.get().restore();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


    //    while (true) {
    //      switch (player.getState()) {
    //        case AT_LOCATION -> locationLoop.execute(actions);
    //        case CHOOSING_POI -> choosePoiLoop.execute(actions);
    //        case AT_POI -> poiLoop.execute(actions);
    //        case IN_DIALOGUE -> dialogueLoop.execute(actions);
    //        case DEBUGGING -> debugLoop.execute(actions);
    //      }
    //    }
  }

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
