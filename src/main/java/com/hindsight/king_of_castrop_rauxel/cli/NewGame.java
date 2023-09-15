package com.hindsight.king_of_castrop_rauxel.cli;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.graphs.Vertex;
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
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired), access = AccessLevel.PRIVATE)
public class NewGame {

  private final StringGenerator stringGenerator;

  private Chunk plane;
  private final Graph<AbstractLocation> map = new Graph<>(true, false);
  private Player player;

  public void start() {
    var name = "Player";
    var startLocation = generateChunk(true);
    this.player = new Player(name, startLocation);
    play();
  }

  private Settlement generateChunk(boolean hasStartLocation) {
    var startTime = System.currentTimeMillis();
    var random = SeedComponent.getInstance();
    plane = ChunkComponent.generateChunk(random);
    var startLocation = placeStartLocation(hasStartLocation);
    generateSettlements();
    connectCloseSettlements();
    connectAtLeastOneSettlement();
    logOutcome(startTime);
    return startLocation;
  }

  private Settlement placeStartLocation(boolean hasStartLocation) {
    Settlement startLocation = null;
    if (hasStartLocation) {
      startLocation = placeSettlement(stringGenerator, plane.getCenter());
    }
    return startLocation;
  }

  private void generateSettlements() {
    var settlementsCount = plane.getDensity();
    for (int i = 0; i < settlementsCount; i++) {
      var coordinates = plane.getRandomCoordinates(Chunk.LocationType.SETTLEMENT);
      var settlement = placeSettlement(stringGenerator, coordinates);
      log.info("Added settlement: {}", settlement.getName());
    }
  }

  public Settlement placeSettlement(
      StringGenerator stringGenerator, Pair<Integer, Integer> coordinates) {
    var settlement = new Settlement(stringGenerator, coordinates);
    map.addVertex(settlement);
    plane.place(coordinates.getFirst(), coordinates.getSecond(), Chunk.LocationType.SETTLEMENT);
    return settlement;
  }

  private void connectCloseSettlements() {
    var vertices = map.getVertices();
    for (var reference : vertices) {
      var refLocation = reference.getLocation();
      for (var other : vertices) {
        if (reference.equals(other)) {
          continue;
        }
        var otherLocation = other.getLocation();
        var otherCoordinates = otherLocation.getCoordinates();
        var distance = plane.calculateDistance(refLocation.getCoordinates(), otherCoordinates);
        log.info(
            "Distance between {} and {} is {} km",
            refLocation.getName(),
            otherLocation.getName(),
            distance);
        if (distance < ChunkComponent.MAX_NEIGHBOUR_DISTANCE) {
          addConnections(reference, other, distance);
        }
      }
    }
  }

  private void connectAtLeastOneSettlement() {
    var vertices = map.getVertices();
    for (var reference : vertices) {
      if (reference.getLocation() instanceof Settlement settlement
          && settlement.getNeighbours().isEmpty()) {
        var closetNeighbour = findClosestNeighbours(settlement, reference, vertices);
        if (closetNeighbour != null) {
          var distance =
              plane.calculateDistance(
                  settlement.getCoordinates(), closetNeighbour.getLocation().getCoordinates());
          addConnections(reference, closetNeighbour, distance);
        }
      }
    }
  }

  private Vertex<AbstractLocation> findClosestNeighbours(
      Settlement settlement,
      Vertex<AbstractLocation> reference,
      List<Vertex<AbstractLocation>> vertices) {
    Vertex<AbstractLocation> closestNeighbor = null;
    var minDistance = Integer.MAX_VALUE;
    for (var other : vertices) {
      if (reference.equals(other)) {
        continue;
      }
      var otherCoordinates = other.getLocation().getCoordinates();
      var distance = plane.calculateDistance(settlement.getCoordinates(), otherCoordinates);
      if (distance < minDistance) {
        minDistance = distance;
        closestNeighbor = other;
      }
    }
    return closestNeighbor;
  }

  private void addConnections(
      Vertex<AbstractLocation> l1, Vertex<AbstractLocation> l2, int distance) {
    map.addEdge(l1, l2, distance);
    if (l1.getLocation() instanceof Settlement s1 && l2.getLocation() instanceof Settlement s2) {
      s1.addNeighbour(s2);
      s2.addNeighbour(s1);
    }
    log.info(
        "Added {} and {} as neighbours of each other",
        l2.getLocation().getName(),
        l1.getLocation().getName());
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
