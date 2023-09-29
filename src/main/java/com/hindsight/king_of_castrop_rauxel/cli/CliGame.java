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
import java.util.List;
import java.util.Optional;
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
      return;
    }
    var actions = actionHandler.getEmpty();
    initialise();
    while (true) {
      getActions(actions);
      displayActions(actions);
      processInput(actions);
      gameHandler.updateWorld(player);
    }
  }

  public void initialise() {
    world.generateChunk(world.getCentreCoords(), map);
    world.setCurrentChunk(world.getCentreCoords());
    var startLocation = world.getCurrentChunk().getCentralLocation(world, map);
    var worldCoordinates = world.getCurrentChunk().getCoordinates().getWorld();
    player = new Player("Traveller", startLocation, worldCoordinates);
  }

  public void getActions(List<Action> actions) {
    switch (player.getState()) {
      case AT_DEFAULT_POI:
        showInfo(true, true);
        actionHandler.getDefaultPoiActions(player, actions);
        break;
      case CHOOSE_POI:
        showInfo(true, false);
        actionHandler.getAllPoiActions(player, actions);
        break;
      case AT_SPECIFIC_POI:
        showInfo(true, true);
        actionHandler.getThisPoiActions(player, actions);
        break;
      case DEBUG:
        showInfo(false, false);
        actionHandler.getDebugActions(player, actions);
        break;
      default:
        showInfo(false, false);
        actionHandler.getDefaultPoiActions(player, actions);
        break;
    }
  }

  public void displayActions(List<Action> actions) {
    out.printf("%sWhat's next?%s%n", CliComponent.FMT.DEFAULT_BOLD, CliComponent.FMT.RESET);
    actions.forEach(a -> out.println(a.print()));
    out.printf("%n%s>%s ", CliComponent.FMT.WHITE_BOLD_BRIGHT, CliComponent.FMT.RESET);
  }

  public void processInput(List<Action> actions) {
    var anyInput = this.scanner.next();
    try {
      var validInput = Integer.parseInt(anyInput);
      var action = actions.stream().filter(a -> a.getIndex() == validInput).findFirst();
      processAction(action);
    } catch (NumberFormatException e) {
      out.println(CliComponent.FMT.RED + "Invalid choice, try again..." + CliComponent.FMT.RESET);
    }
    out.printf("%n%n");
  }

  public void processAction(Optional<Action> action) {
    if (action.isPresent()) {
      action.ifPresent(chosenAction -> chosenAction.execute(player));
    } else {
      out.println(CliComponent.FMT.RED + "Invalid choice, try again..." + CliComponent.FMT.RESET);
    }
  }

  private void showInfo(boolean showStats, boolean showLocation) {
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
      out.printf(
          "%sYou are at: %s.%s ",
          CliComponent.FMT.DEFAULT_BOLD, player.getCurrentPoi().getName(), CliComponent.FMT.RESET);
    }
  }
}
