package com.hindsight.king_of_castrop_rauxel.cli;

import static java.lang.System.out;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.action.ActionHandler;
import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.configuration.EnvironmentResolver;
import com.hindsight.king_of_castrop_rauxel.game.GameHandler;
import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.location.AbstractLocation;
import com.hindsight.king_of_castrop_rauxel.location.Location;
import com.hindsight.king_of_castrop_rauxel.world.World;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
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
  private final ActionHandler actionHandler;
  private final GameHandler gameHandler;
  private final Scanner scanner;
  private final World world;
  private final Graph<AbstractLocation> map;
  private Player player;

  @SuppressWarnings("InfiniteLoopStatement")
  public void play() {
    if (environmentResolver.isNotCli()) {
      log.info("Not running in CLI mode, CLI game will not be started");
      return;
    }
    var actions = actionHandler.getEmpty();
    initialise();
    while (true) {
      printHeaders();
      prepareActions(actions);
      printActions(actions);
      takeAction(actions);
      printResponse();
      postProcess();
    }
  }

  private void initialise() {
    world.generateChunk(world.getCentreCoords(), map);
    world.setCurrentChunk(world.getCentreCoords());
    var startLocation = world.getCurrentChunk().getCentralLocation(world, map);
    var worldCoordinates = world.getCurrentChunk().getCoordinates().getWorld();
    player = new Player("Traveller", startLocation, worldCoordinates);
  }

  private void printHeaders() {
    if (!player.getCli().printHeaders()) {
      return;
    }
    CliComponent.clearConsole();
    switch (player.getState()) {
      case AT_DEFAULT_POI, AT_SPECIFIC_POI, CHOOSE_POI -> showInfo(true, true, true);
      case EVENT -> showInfo(false, false, false);
      case DEBUG -> showInfo(true, true, false);
    }
  }

  private void prepareActions(List<Action> actions) {
    if (!player.getCli().prepareActions()) {
      return;
    }
    switch (player.getState()) {
      case AT_DEFAULT_POI -> actionHandler.getDefaultPoiActions(player, actions);
      case CHOOSE_POI -> actionHandler.getAllPoiActions(player, actions);
      case AT_SPECIFIC_POI -> actionHandler.getThisPoiActions(player, actions);
      case DEBUG -> actionHandler.getDebugActions(player, actions);
      case EVENT -> actionHandler.getDialogueActions(player, actions);
    }
  }

  private void printActions(List<Action> actions) {
    if (!player.getCli().printActions()) {
      return;
    }
    out.printf("%sWhat's next?%s%n", CliComponent.FMT.DEFAULT_BOLD, CliComponent.FMT.RESET);
    actions.forEach(a -> out.println(a.print()));
    out.printf("%n%s>%s ", CliComponent.FMT.WHITE_BOLD_BRIGHT, CliComponent.FMT.RESET);
  }

  private void takeAction(List<Action> actions) {
    if (!player.getCli().takeAction()) {
      return;
    }
    var anyInput = this.scanner.next();
    try {
      var validInput = Integer.parseInt(anyInput);
      var action = actions.stream().filter(a -> a.getIndex() == validInput).findFirst();
      if (action.isPresent()) {
        action.ifPresent(chosenAction -> chosenAction.execute(player));
      } else {
        out.println(CliComponent.FMT.RED + "Invalid choice, try again..." + CliComponent.FMT.RESET);
      }
    } catch (NumberFormatException e) {
      out.println(CliComponent.FMT.RED + "Invalid choice, try again..." + CliComponent.FMT.RESET);
    }
    out.printf("%n%n");
  }

  // TODO: Clean up CLI loop to enable/disable relevant steps
  // TODO: Implement CliComponent.clearConsole() in CLI loop
  // TODO: Add single-step dialogue, multi-step dialogue, kill quest and go-to quest
  private void printResponse() {
    if (!player.getCli().printResponse()) {
      return;
    }
    showText();
    try {
      out.println("Press enter to continue...");
      System.in.read();
    } catch (IOException e) {
      log.error("Could not read input from console", e);
    }
  }

  private void postProcess() {
    if (!player.getCli().postProcess()) {
      return;
    }
    gameHandler.updateWorld(player);
    updateCurrentEvent();
  }

  private void updateCurrentEvent() {
    if (player.getState() == Player.PlayerState.EVENT) {
      var event = player.getCurrentEvent();
      event.progress();
      if (!event.hasNext()) {
        event.setComplete();
        player.setCurrentEvent(null);
        player.setState(Player.PlayerState.AT_SPECIFIC_POI);
      }
    }
  }

  private void showInfo(boolean showStats, boolean showLocation, boolean showPoi) {
    if (showStats) {
      out.printf(
          "%sSTATS: [ Gold: %s%s%s | Level: %s%s%s | Age: %s%s%s | Activity points left: %s%s%s ]%s%n",
          CliComponent.FMT.DEFAULT_BOLD,
          CliComponent.FMT.YELLOW_BOLD,
          player.getGold(),
          CliComponent.FMT.DEFAULT_BOLD,
          CliComponent.FMT.BLUE_BOLD,
          player.getLevel(),
          CliComponent.FMT.DEFAULT_BOLD,
          CliComponent.FMT.MAGENTA_BOLD,
          player.getAge(),
          CliComponent.FMT.DEFAULT_BOLD,
          CliComponent.FMT.GREEN_BOLD,
          player.getActivityPoints(),
          CliComponent.FMT.DEFAULT_BOLD,
          CliComponent.FMT.RESET);
    }
    if (showLocation) {
      Location currentLocation = player.getCurrentLocation();
      out.printf(
          "%sCURRENT LOCATION: %s%s%n%n",
          CliComponent.FMT.DEFAULT_BOLD, currentLocation.getFullSummary(), CliComponent.FMT.RESET);
    }
    if (showPoi) {
      out.printf(
          "%sYou are at: %s.%s ",
          CliComponent.FMT.DEFAULT_BOLD, player.getCurrentPoi().getName(), CliComponent.FMT.RESET);
    }
    if (showStats && !showLocation && !showPoi) {
      out.printf("%n%n");
    }
  }

  private void showText() {
    var event = player.getCurrentEvent();
    if (event.hasNext()) {
      out.printf(
          "%s%s%s%s: %s%n%n",
          CliComponent.FMT.BLACK,
          CliComponent.FMT.WHITE_BACKGROUND,
          event.getNpc().getName(),
          CliComponent.FMT.RESET,
          event.getNext().text());
    }
  }
}
