package com.hindsight.king_of_castrop_rauxel.cli;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.action.ActionTemplate;
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
public class NewGame {

  private final StringGenerator stringGenerator;
  private final Scanner input = new Scanner(System.in);
  private final Graph<AbstractLocation> map = new Graph<>(true);
  private Player player;

  // TODO: Create basic CLI game loop
  //  - Display amenity you're at
  //  - Allow using PlayerAction to visit other locations
  @SuppressWarnings("InfiniteLoopStatement")
  public void start() {
    var name = "Player";
    var startLocation = generateFirstChunk();
    this.player = new Player(name, startLocation);
    while (true) {
      showStats();
      play();
    }
  }

  private void showStats() {
    System.out.printf(
        "%n%nSTATS: [ Gold: %s | Level: %s | Age: %s | Activity points left: %s ]%n",
        player.getGold(), player.getLevel(), player.getAge(), player.getActivityPoints());
    Location currentLocation = player.getCurrentLocation();
    System.out.printf("CURRENT LOCATION: %s%n%n", currentLocation.getSummary());
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

  private void play() {
    var actions = buildAndPrintActions();
    System.out.printf("%n> ");
    var choice = this.input.nextInt();
    var action = actions.stream().filter(a -> a.getNumber() == choice).findFirst();
    if (action.isPresent()) {
      action.get().execute(player);
    } else {
      System.out.println("Invalid choice! Try again...");
    }
  }

  private List<Action> buildAndPrintActions() {
    Optional<List<Action>> actions = Optional.empty();
    if (player.getCurrentLocation() instanceof Settlement settlement) {
      System.out.printf("You are at: %s. ", settlement.getDefaultPoi().getName());
      actions = Optional.of(ActionTemplate.defaultLocationActions(settlement));
    }
    System.out.println("What do you want to do?");
    actions.ifPresent(list -> list.forEach(a -> System.out.println(a.print())));
    return actions.orElse(List.of());
  }
}
