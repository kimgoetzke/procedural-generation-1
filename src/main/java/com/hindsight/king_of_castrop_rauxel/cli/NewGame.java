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
  public void start() {
    var name = "Player";
    var startLocation = generateFirstChunk();
    this.player = new Player(name, startLocation);
    play();
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
    System.out.printf(
        "%n%nSTATS: [ Gold: %s | Level: %s | Age: %s | Activity points left: %s ]%n",
        player.getGold(), player.getLevel(), player.getAge(), player.getActivityPoints());
    Location currentLocation = player.getCurrentLocation();
    Optional<List<Action>> actions = Optional.empty();
    System.out.printf("CURRENT LOCATION: %s%n%n", currentLocation.getSummary());
    if (currentLocation instanceof Settlement settlement) {
      System.out.printf("You are at: %s. ", settlement.getDefaultAmenity().getName());
      actions = Optional.of(ActionTemplate.defaultSettlementActions(settlement));
    }
    System.out.printf("What do you want to do?");
    actions.ifPresent(list -> list.forEach(Action::print));
    System.out.println("> ");
    var choice = this.input.nextInt();
    if (choice >= 0 && actions.isPresent() && choice <= actions.get().size()) {
      actions.get().get(choice - 1).execute(player);
    } else {
      System.out.printf("Invalid choice! Try again...%n");
    }
    play();
  }
}
