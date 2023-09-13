package com.hindsight.king_of_castrop_rauxel.cli;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.graphs.Vertex;
import com.hindsight.king_of_castrop_rauxel.location.Location;
import com.hindsight.king_of_castrop_rauxel.location.Settlement;
import com.hindsight.king_of_castrop_rauxel.settings.LocationComponent;
import com.hindsight.king_of_castrop_rauxel.settings.SeedComponent;
import com.hindsight.king_of_castrop_rauxel.utils.StringGenerator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired), access = AccessLevel.PRIVATE)
public class NewGame {

  private final StringGenerator stringGenerator;
  private final Graph map = new Graph(true, false);
  private Player player;

  public void start() {
    var name = "Player";
    var startLocation = generateMap();
    this.player = new Player(name, startLocation);
    play();
  }

  private Settlement generateMap() {
    var startTime = System.currentTimeMillis();
    var random = SeedComponent.getInstance();
    var startLocation = new Settlement(stringGenerator);
    var startVertex = map.addVertex(startLocation);
    generateNeighbours(random, startVertex, 0);
    logOutcome(startTime);
    return startLocation;
  }

  private void logOutcome(long startTime) {
    map.log();
    log.info("#");
    log.info("# Summary:");
    log.info("# Generated {} settlements", map.getVertices().size());
    log.info("# Generation took {} seconds", (System.currentTimeMillis() - startTime) / 1000);
    log.info("# List of settlements generated:");
    map.getVertices().forEach(vertex -> log.info("# -> " + vertex.getLocation().getSummary()));
    log.info("#");
  }

  // TODO: Add neighbours to list of neighbours
  // TODO: Link other vertices to neighbours
  private void generateNeighbours(Random random, Vertex previous, int distanceFromStart) {
    var neighboursCount = LocationComponent.randomNeighboursCount(random);
    var neighbours = new ArrayList<Settlement>();
    var current = previous;
    var distance = 0;
    for (int i = 0; i < neighboursCount; i++) {
      distance = LocationComponent.randomSettlementDistance(random);
      if (distanceFromStart + distance < LocationComponent.MAX_DISTANCE_FROM_START) {
        var neighbour = new Settlement(stringGenerator);
        neighbours.add(neighbour);
        current = map.addVertex(neighbour);
        map.addEdge(previous, current, distance);
        log.info(
            "Added {} as neighbour of {}", neighbour.getName(), previous.getLocation().getName());
      } else {
        log.info(
            "Stopped because next neighbour of {} would be {} km away (max: {})",
            previous.getLocation().getName(),
            distanceFromStart + distance,
            LocationComponent.MAX_DISTANCE_FROM_START);
      }
    }
    for (int i = 0; i < neighbours.size(); i++) {

      log.info("Generating neighbours of {}", neighbours.get(i).getName());
      generateNeighbours(random, current, distanceFromStart + distance);
    }
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
