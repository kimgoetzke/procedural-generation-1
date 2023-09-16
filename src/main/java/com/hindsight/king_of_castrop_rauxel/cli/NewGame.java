package com.hindsight.king_of_castrop_rauxel.cli;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.action.ActionComponent;
import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.components.Chunk;
import com.hindsight.king_of_castrop_rauxel.components.ChunkComponent;
import com.hindsight.king_of_castrop_rauxel.components.SeedComponent;
import com.hindsight.king_of_castrop_rauxel.components.WorldBuildingComponent;
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

  // TODO: Create basic CLI game loop
  //  - Consider whether to use actions from templates or from location/POI
  //  - Implement PoiAction
  @SuppressWarnings("InfiniteLoopStatement")
  public void start() {
    var name = "Player";
    var startLocation = generateFirstChunk();
    var actions = ActionComponent.emptyActions();
    this.player = new Player(name, startLocation);
    while (true) {
      play(actions);
    }
  }

  private Settlement generateFirstChunk() {
    var startTime = System.currentTimeMillis();
    var random = SeedComponent.getInstance();
    Chunk chunk = ChunkComponent.generateChunk(random);
    var startLocation = WorldBuildingComponent.build(map, chunk, stringGenerator);
    logOutcome(startTime);
    return startLocation;
  }

  private void logOutcome(long startTime) {
    map.log();
    log.info("Generation took {} seconds", (System.currentTimeMillis() - startTime) / 1000);
    log.info("Generated {} settlements", map.getVertices().size());
    log.info("List of settlements generated:");
    map.getVertices().forEach(vertex -> log.info("-> " + vertex.getLocation().getSummary()));
  }

  private void play(List<Action> actions) {
    showStats();
    buildActions(actions);
    displayActions(actions);
    processAction(actions);
  }

  private void showStats() {
    System.out.printf(
        "%n%nSTATS: [ Gold: %s | Level: %s | Age: %s | Activity points left: %s ]%n",
        player.getGold(), player.getLevel(), player.getAge(), player.getActivityPoints());
    Location currentLocation = player.getCurrentLocation();
    System.out.printf("CURRENT LOCATION: %s%n%n", currentLocation.getSummary());
    System.out.printf("You are at: %s. ", player.getCurrentPoi().getName());
  }

  private void buildActions(List<Action> actions) {
    actions.clear();
    switch (player.getState()) {
      case AT_DEFAULT_POI -> actions.addAll(ActionComponent.listForLocation(player));
      case INSIDE_LOCATION -> actions.addAll(ActionComponent.listPois(player));
      case AT_SPECIFIC_POI -> actions.addAll(ActionComponent.listForPoi(player));
    }
  }

  private void displayActions(List<Action> actions) {
    System.out.println("What do you want to do?");
    actions.forEach(a -> System.out.println(a.print()));
    System.out.printf("%n> ");
  }

  private void processAction(List<Action> actions) {
    var choice = this.input.nextInt();
    var action = actions.stream().filter(a -> a.getIndex() == choice).findFirst();
    if (action.isPresent()) {
      action.get().execute(player, actions);
    } else {
      System.out.println("Invalid choice! Try again...");
    }
  }
}
