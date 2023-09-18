package com.hindsight.king_of_castrop_rauxel.cli;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.action.ActionHandler;
import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.location.AbstractLocation;
import com.hindsight.king_of_castrop_rauxel.location.Location;
import com.hindsight.king_of_castrop_rauxel.location.Settlement;
import com.hindsight.king_of_castrop_rauxel.utils.StringGenerator;
import com.hindsight.king_of_castrop_rauxel.world.*;
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
public class NewGame {

  private final ActionHandler actionHandler;
  private final StringGenerator stringGenerator;
  private final World world;
  private final Graph<AbstractLocation> map;
  private final Scanner input;
  private Player player;

  @SuppressWarnings("InfiniteLoopStatement")
  public void play() {
    var startLocation = generateFirstChunk();
    var actions = actionHandler.getEmpty();
    var worldCoordinates = world.getCurrentChunk().getWorldCoordinates();
    this.player = new Player("Traveller", startLocation, worldCoordinates);
    displayWelcome(player);
    while (true) {
      buildActions(actions);
      displayActions(actions);
      processAction(actions);
      updateWorld();
    }
  }

  private Settlement generateFirstChunk() {
    var startTime = System.currentTimeMillis();
    var random = SeedComponent.getInstance();
    var chunk = ChunkComponent.generateChunk(random, world.getCenter());
    var startLocation = WorldBuildingComponent.build(map, chunk, stringGenerator);
    world.placeChunk(chunk);
    startLocation.generate();
    logOutcome(startTime);
    return startLocation;
  }

  private void logOutcome(long startTime) {
    map.log();
    log.info("Generation took {} seconds", (System.currentTimeMillis() - startTime) / 1000);
    log.info("Generated {} settlements", map.getVertices().size());
    log.info("List of settlements generated:");
    map.getVertices().forEach(vertex -> log.info("- " + vertex.getLocation().getBriefSummary()));
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

  // TODO: Ensure getDefaultPoiActions() displays relative location to player i.e. North, South, etc.
  // TODO: Add settlement as well as chunk unloading/destroying to free up memory
  // TODO: Add method to log connections between two chunks to "printConnectivity()"
  // TODO: Create method to ensure currentChunk and nextChunk are always connected
  private void updateWorld() {
    updatePlayerWorldCoordinates();
    generateNextChunk();
  }

  private void updatePlayerWorldCoordinates() {
    var worldCoordinates = world.getCurrentChunk().getWorldCoordinates();
    if (player.getCurrentWorldCoordinates() != worldCoordinates) {
      log.info("Player is entering a new chunk");
      player.setCurrentWorldCoordinates(worldCoordinates);
    }
  }

  private void generateNextChunk() {
    var chunkCoordinates = player.getCurrentLocation().getCoordinates();
    if (ChunkComponent.isInsideTriggerZone(chunkCoordinates)) {
      var whereNext = ChunkComponent.nextChunkPosition(chunkCoordinates);
      if (world.hasChunk(whereNext)) {
        log.info("Player is inside trigger zone but next chunk already exists");
        return;
      }
      generateNextChunk(whereNext);
    }
  }

  private void generateNextChunk(WorldBuildingComponent.CardinalDirection whereNext) {
    log.info("Generating next chunk...");
    var startTime = System.currentTimeMillis();
    var random = SeedComponent.getInstance();
    var currentChunk = world.getCurrentChunk();
    Chunk nextChunk = ChunkComponent.generateChunk(random, world.getPosition(whereNext));
    world.placeChunk(nextChunk, whereNext);
    WorldBuildingComponent.buildNext(map, currentChunk, nextChunk, whereNext, stringGenerator);
    logOutcome(startTime);
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
