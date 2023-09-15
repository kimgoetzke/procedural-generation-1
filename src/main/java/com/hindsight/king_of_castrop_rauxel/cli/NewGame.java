package com.hindsight.king_of_castrop_rauxel.cli;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.graphs.WorldBuildingComponent;
import com.hindsight.king_of_castrop_rauxel.location.AbstractLocation;
import com.hindsight.king_of_castrop_rauxel.location.Location;
import com.hindsight.king_of_castrop_rauxel.location.Settlement;
import com.hindsight.king_of_castrop_rauxel.settings.Chunk;
import com.hindsight.king_of_castrop_rauxel.settings.ChunkComponent;
import com.hindsight.king_of_castrop_rauxel.settings.SeedComponent;
import com.hindsight.king_of_castrop_rauxel.utils.StringGenerator;
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
    System.out.printf("%nWelcome to King of Castrop-Rauxel, %s!%n%n", player.getName());
    System.out.printf(
        "STATS: [ Gold: %s | Level: %s | Age: %s | Activity points left: %s ]%n",
        player.getGold(), player.getLevel(), player.getAge(), player.getActivityPoints());
    Location currentLocation = player.getCurrentLocation();
    System.out.printf("CURRENT LOCATION: %s%n%n", currentLocation.getSummary());
    System.out.printf("What do you want to do?%n");
    player
        .getCurrentLocation()
        .getAvailableActions()
        .forEach(action -> System.out.printf("%s%n", action.getName()));
  }
}
