package com.hindsight.king_of_castrop_rauxel.cli;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.action.ActionComponent;
import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.world.Chunk;
import com.hindsight.king_of_castrop_rauxel.world.ChunkComponent;
import com.hindsight.king_of_castrop_rauxel.world.SeedComponent;
import com.hindsight.king_of_castrop_rauxel.world.WorldBuildingComponent;
import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.location.AbstractLocation;
import com.hindsight.king_of_castrop_rauxel.location.Location;
import com.hindsight.king_of_castrop_rauxel.location.Settlement;
import com.hindsight.king_of_castrop_rauxel.utils.StringGenerator;
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

  private final StringGenerator stringGenerator;
  private final Scanner input = new Scanner(System.in);
  private final Graph<AbstractLocation> map = new Graph<>(true);
  private Player player;

  @SuppressWarnings("InfiniteLoopStatement")
  public void start() {
    var startLocation = generateFirstChunk();
    var actions = ActionComponent.empty();
    this.player = new Player("Traveller", startLocation);
    displayWelcome(player);
    while (true) {
      buildActions(actions);
      displayActions(actions);
      processAction(actions);
    }
  }

  private Settlement generateFirstChunk() {
    var startTime = System.currentTimeMillis();
    var random = SeedComponent.getInstance();
    Chunk chunk = ChunkComponent.generateChunk(random);
    var startLocation = WorldBuildingComponent.build(map, chunk, stringGenerator);
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
        showStats(true);
        ActionComponent.defaultPoi(player, actions);
        break;
      case CHOOSE_POI:
        showStats(false);
        ActionComponent.allPois(player, actions);
        break;
      case AT_SPECIFIC_POI:
        showStats(true);
        ActionComponent.thisPoi(player, actions);
        break;
      default:
        showStats(false);
        ActionComponent.fallback(player, actions);
        break;
    }
  }

  private void displayActions(List<Action> actions) {
    System.out.println("What's next?");
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

  private void showStats(boolean showLocation) {
    System.out.printf(
        "STATS: [ Gold: %s | Level: %s | Age: %s | Activity points left: %s ]%n",
        player.getGold(), player.getLevel(), player.getAge(), player.getActivityPoints());
    if (showLocation) {
      Location currentLocation = player.getCurrentLocation();
      System.out.printf("CURRENT LOCATION: %s%n%n", currentLocation.getFullSummary());
      System.out.printf("You are at: %s. ", player.getCurrentPoi().getName());
    } else {
      System.out.println();
    }
  }
}
