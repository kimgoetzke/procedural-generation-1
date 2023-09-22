package com.hindsight.king_of_castrop_rauxel.cli;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.action.ActionHandler;
import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.location.AbstractLocation;
import com.hindsight.king_of_castrop_rauxel.location.Location;
import com.hindsight.king_of_castrop_rauxel.utils.StringGenerator;
import com.hindsight.king_of_castrop_rauxel.world.*;
import java.util.List;
import java.util.Scanner;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.hindsight.king_of_castrop_rauxel.world.Coordinates.*;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired), access = AccessLevel.PRIVATE)
public class NewGame {

  private final ActionHandler actionHandler;
  private final StringGenerator stringGenerator;
  private final World world;
  private final Graph<AbstractLocation> map;
  private final Scanner input;
  private Player player;

  @SuppressWarnings("InfiniteLoopStatement")
  public void play() {
    var startLocation = WorldBuildingComponent.build(world, map, stringGenerator);
    var actions = actionHandler.getEmpty();
    var worldCoordinates = world.getCurrentChunk().getCoordinates().getWorld();
    this.player = new Player("Traveller", startLocation, worldCoordinates);
    displayWelcome(player);
    while (true) {
      buildActions(actions);
      displayActions(actions);
      processAction(actions);
      updateWorld();
    }
  }

  private void displayWelcome(Player player) {
    System.out.printf("%n%nHello, %s!%n%n%n", player.getName());
  }

  private void buildActions(List<Action> actions) {
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
        System.out.printf("Not implemented yet...%n%n");
        showInfo(false, false);
        actionHandler.getDefaultPoiActions(player, actions);
        break;
    }
  }

  private void displayActions(List<Action> actions) {
    System.out.printf("What's next?%n");
    actions.forEach(a -> System.out.println(a.print()));
    System.out.printf("%n> ");
  }

  private void processAction(List<Action> actions) {
    var choice = this.input.nextInt();
    var action = actions.stream().filter(a -> a.getIndex() == choice).findFirst();
    if (action.isPresent()) {
      action.get().execute(player, actions);
    } else {
      System.out.println("Invalid choice, try again...");
    }
    System.out.printf("%n%n");
  }

  // TODO: Fix bug where graph doesn't check which chunk a location is in
  // TODO: Add chunk unloading to free up memory
  // TODO: Add method to log connections between two chunks to "printConnectivity()"
  // TODO: Create method to ensure currentChunk and nextChunk are always connected
  private void updateWorld() {
    updateWorldCoords();
    generateNextChunk();
  }

  private void updateWorldCoords() {
    var worldCoords = world.getCurrentChunk().getCoordinates().getWorld();
    if (!player.getCoordinates().equalTo(worldCoords, CoordType.WORLD)) {
      log.info(String.format("Player is entering: %s%n", world.getCurrentChunk().getSummary()));
      world.setCurrentChunk(player.getCoordinates().getWorld());
    }
  }

  private void generateNextChunk() {
    var chunkCoords = player.getCurrentLocation().getCoordinates().getChunk();
    if (ChunkComponent.isInsideTriggerZone(chunkCoords)) {
      var whereNext = ChunkComponent.nextChunkPosition(chunkCoords);
      if (world.hasChunk(whereNext)) {
        log.info(
            String.format(
                "Player is inside trigger zone but %s chunk already exists%n",
                whereNext.getName().toLowerCase()));
        return;
      }
      WorldBuildingComponent.buildNext(whereNext, world, map, stringGenerator);
    }
  }

  private void showInfo(boolean showStats, boolean showLocation) {
    if (showStats) {
      System.out.printf(
          "STATS: [ Gold: %s | Level: %s | Age: %s | Activity points left: %s ]%n",
          player.getGold(), player.getLevel(), player.getAge(), player.getActivityPoints());
    }
    if (showLocation) {
      Location currentLocation = player.getCurrentLocation();
      System.out.printf("CURRENT LOCATION: %s%n%n", currentLocation.getFullSummary());
      System.out.printf("You are at: %s. ", player.getCurrentPoi().getName());
    }
    if (showStats || showLocation) {
      System.out.println();
    }
  }
}
