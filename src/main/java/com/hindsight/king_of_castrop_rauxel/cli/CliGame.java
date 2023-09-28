package com.hindsight.king_of_castrop_rauxel.cli;

import static com.hindsight.king_of_castrop_rauxel.cli.CliComponent.*;
import static java.lang.System.out;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.action.ActionHandler;
import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.game.Game;
import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.location.AbstractLocation;
import com.hindsight.king_of_castrop_rauxel.location.Location;
import com.hindsight.king_of_castrop_rauxel.world.ChunkBuilder;
import com.hindsight.king_of_castrop_rauxel.world.Coordinates;
import com.hindsight.king_of_castrop_rauxel.world.World;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CliGame implements Game {

  private final ActionHandler actionHandler;
  private final Scanner scanner;
  private final World world;
  private final Graph<AbstractLocation> map;
  private Player player;

  @Override
  public void start() {
    world.generateChunk(world.getCentreCoords(), map);
    var startLocation = world.getCurrentChunk().getCentralLocation(world, map);
    var worldCoordinates = world.getCurrentChunk().getCoordinates().getWorld();
    player = new Player("Traveller", startLocation, worldCoordinates);
  }

  @Override
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
    out.printf("%sWhat's next?%s%n", FMT.DEFAULT_BOLD, FMT.RESET);
    actions.forEach(a -> out.println(a.print()));
    out.printf("%n%s>%s ", FMT.WHITE_BOLD_BRIGHT, FMT.RESET);
  }

  public void processInput(List<Action> actions) {
    var anyInput = this.scanner.next();
    try {
      var validInput = Integer.parseInt(anyInput);
      var action = actions.stream().filter(a -> a.getIndex() == validInput).findFirst();
      processAction(action);
    } catch (NumberFormatException e) {
      out.println(FMT.RED + "Invalid choice, try again..." + FMT.RESET);
    }
    out.printf("%n%n");
  }

  @Override
  public void processAction(Optional<Action> action) {
    if (action.isPresent()) {
      action.ifPresent(chosenAction -> chosenAction.execute(player));
    } else {
      out.println(FMT.RED + "Invalid choice, try again..." + FMT.RESET);
    }
  }

  @Override
  public void updateWorld() {
    updateWorldCoords();
    generateNextChunk();
  }

  private void updateWorldCoords() {
    var worldCoords = world.getCurrentChunk().getCoordinates().getWorld();
    if (!player.getCoordinates().equalTo(worldCoords, Coordinates.CoordType.WORLD)) {
      log.info(String.format("Player is leaving: %s%n", world.getCurrentChunk().getSummary()));
      world.setCurrentChunk(player.getCoordinates().getWorld());
      log.info(String.format("Player is entering: %s%n", world.getCurrentChunk().getSummary()));
    }
  }

  private void generateNextChunk() {
    var chunkCoords = player.getCurrentLocation().getCoordinates().getChunk();
    if (ChunkBuilder.isInsideTriggerZone(chunkCoords)) {
      var whereNext = ChunkBuilder.nextChunkPosition(chunkCoords);
      if (world.hasChunk(whereNext)) {
        log.info(
            String.format(
                "Player is inside trigger zone but %s chunk already exists%n",
                whereNext.getName().toLowerCase()));
        return;
      }
      world.generateChunk(whereNext, map);
    }
  }

  private void showInfo(boolean showStats, boolean showLocation) {
    if (showStats) {
      out.printf(
          "%sSTATS: [ Gold: %s%s%s | Level: %s%s%s | Age: %s%s%s | Activity points left: %s%s%s ]%s%n",
          FMT.DEFAULT_BOLD,
          FMT.YELLOW_BOLD,
          player.getGold(),
          FMT.DEFAULT_BOLD,
          FMT.BLUE_BOLD,
          player.getLevel(),
          FMT.DEFAULT_BOLD,
          FMT.MAGENTA_BOLD,
          player.getAge(),
          FMT.DEFAULT_BOLD,
          FMT.GREEN_BOLD,
          player.getActivityPoints(),
          FMT.DEFAULT_BOLD,
          FMT.RESET);
    }
    if (showLocation) {
      Location currentLocation = player.getCurrentLocation();
      out.printf(
          "%sCURRENT LOCATION: %s%s%n%n",
          FMT.DEFAULT_BOLD, currentLocation.getFullSummary(), FMT.RESET);
      out.printf(
          "%sYou are at: %s.%s ", FMT.DEFAULT_BOLD, player.getCurrentPoi().getName(), FMT.RESET);
    }
  }
}
