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
    Settlement startLocation = null;
    if (hasStartLocation) {
      startLocation = generateSettlement(stringGenerator, plane.getCenter());
    }
    generateSettlements();
    connectCloseSettlements();
    connectAtLeastOneSettlement();
    logOutcome(startTime);
    return startLocation;
  }

  private void connectCloseSettlements() {
    List<Vertex<AbstractLocation>> vertices = map.getVertices();
    for (var ref : vertices) {
      var refCoordinates = ref.getLocation().getCoordinates();
      vertices.stream()
          .filter(neighbour -> !ref.equals(neighbour))
          .forEach(
              neighbour -> {
                var neighbourCoordinates = neighbour.getLocation().getCoordinates();
                var distance = plane.calculateDistance(refCoordinates, neighbourCoordinates);
                log.info(
                    "Distance between {} and {} is {} km",
                    ref.getLocation().getName(),
                    neighbour.getLocation().getName(),
                    distance);
                addConnectionsWithinRange(ref, neighbour, distance);
              });
    }
  }

  // TODO: Complete this method
  private void connectAtLeastOneSettlement() {
    List<Vertex<AbstractLocation>> vertices = map.getVertices();
    for (var ref : vertices) {
      var refCoordinates = ref.getLocation().getCoordinates();
      vertices.stream()
        .filter(neighbour -> !ref.equals(neighbour))
        .forEach(
          neighbour -> {
            var neighbourCoordinates = neighbour.getLocation().getCoordinates();
            var distance = plane.calculateDistance(refCoordinates, neighbourCoordinates);
            log.info(
              "Distance between {} and {} is {} km",
              ref.getLocation().getName(),
              neighbour.getLocation().getName(),
              distance);
            addConnectionsWithinRange(ref, neighbour, distance);
          });
    }
  }

  private void addConnectionsWithinRange(
      Vertex<AbstractLocation> ref, Vertex<AbstractLocation> neighbour, int distance) {
    if (distance < ChunkComponent.MAX_NEIGHBOUR_DISTANCE) {
      map.addEdge(ref, neighbour, distance);
      if (ref.getLocation() instanceof Settlement settlement) {
        settlement.addNeighbour(neighbour.getLocation());
      }
      if (neighbour.getLocation() instanceof Settlement settlement) {
        settlement.addNeighbour(ref.getLocation());
      }
      log.info(
          "Added {} as neighbour of {} and vice versa",
          neighbour.getLocation().getName(),
          ref.getLocation().getName());
    }
  }

  private void generateSettlements() {
    var settlementsCount = plane.getDensity();
    for (int i = 0; i < settlementsCount; i++) {
      var coordinates = plane.getRandomCoordinates(Chunk.LocationType.SETTLEMENT);
      var settlement = generateSettlement(stringGenerator, coordinates);
      log.info("Added settlement: {}", settlement.getName());
    }
  }

  public Settlement generateSettlement(
      StringGenerator stringGenerator, Pair<Integer, Integer> coordinates) {
    var settlement = new Settlement(stringGenerator, coordinates);
    map.addVertex(settlement);
    plane.place(coordinates.getFirst(), coordinates.getSecond(), Chunk.LocationType.SETTLEMENT);
    return settlement;
  }

  // TODO: Replace this by an algorithm that places random coordinates on a sphere
  //  and then calculates the distance between them to determine the neighbours
  //  private void generateNeighbours(
  //      Random random, Vertex<AbstractSettlement> previous, int distanceFromStart) {
  //    var neighboursCount = ChunkComponent.randomNeighboursCount(random);
  //    var neighbours = new ArrayList<Settlement>();
  //    var current = previous;
  //    var distance = 0;
  //    for (int i = 0; i < neighboursCount; i++) {
  //      distance = ChunkComponent.randomSettlementDistance(random);
  //      current = createSettlement(previous, distanceFromStart, distance, current, neighbours);
  //    }
  //    for (Settlement neighbour : neighbours) {
  //      log.info("Generating neighbours of {}", neighbour.getName());
  //      generateNeighbours(random, current, distanceFromStart + distance);
  //    }
  //  }
  //
  //  private Vertex<AbstractSettlement> createSettlement(
  //      Vertex<AbstractSettlement> previous,
  //      int distanceFromStart,
  //      int distance,
  //      Vertex<AbstractSettlement> current,
  //      ArrayList<Settlement> neighbours) {
  //    if (distanceFromStart + distance < ChunkComponent.MAX_DISTANCE_FROM_START) {
  //      var coordinates = Pair.of(0F, 0F);
  //      var neighbour = new Settlement(stringGenerator, coordinates);
  //      current = map.addVertex(neighbour);
  //      neighbours.add(neighbour);
  //      map.addEdge(previous, current, distance);
  //      current.getLocation().addNeighbour(previous.getLocation());
  //      previous.getLocation().addNeighbour(current.getLocation());
  //      log.info(
  //          "Added {} as neighbour of {} and vice versa",
  //          neighbour.getName(),
  //          previous.getLocation().getName());
  //    } else {
  //      log.info(
  //          "Stopped because next neighbour of {} would be {} km away (max: {})",
  //          previous.getLocation().getName(),
  //          distanceFromStart + distance,
  //          ChunkComponent.MAX_DISTANCE_FROM_START);
  //    }
  //    return current;
  //  }

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
