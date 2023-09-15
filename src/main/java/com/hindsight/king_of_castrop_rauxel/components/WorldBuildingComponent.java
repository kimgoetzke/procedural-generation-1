package com.hindsight.king_of_castrop_rauxel.components;

import static com.hindsight.king_of_castrop_rauxel.components.Chunk.*;

import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.graphs.Vertex;
import com.hindsight.king_of_castrop_rauxel.location.AbstractLocation;
import com.hindsight.king_of_castrop_rauxel.location.Settlement;
import com.hindsight.king_of_castrop_rauxel.utils.StringGenerator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WorldBuildingComponent {

  public static Settlement build(
      Graph<AbstractLocation> map, Chunk chunk, StringGenerator stringGenerator) {
    var startLocation = placeSettlement(map, chunk, stringGenerator, chunk.getCenter());
    generateSettlements(map, chunk, stringGenerator);
    connectCloseSettlements(map, chunk);
    connectAtLeastOneSettlement(map, chunk);
    connectDisconnectedGroups(map, chunk);
    return startLocation;
  }

  private static void generateSettlements(
      Graph<AbstractLocation> map, Chunk chunk, StringGenerator stringGenerator) {
    var settlementsCount = chunk.getDensity();
    for (int i = 0; i < settlementsCount; i++) {
      var coordinates = chunk.getRandomCoordinates(LocationType.SETTLEMENT);
      var settlement = placeSettlement(map, chunk, stringGenerator, coordinates);
      log.info("Added settlement: {}", settlement.getName());
    }
  }

  private static Settlement placeSettlement(
      Graph<AbstractLocation> map,
      Chunk chunk,
      StringGenerator stringGenerator,
      Pair<Integer, Integer> coordinates) {
    var settlement = new Settlement(stringGenerator, coordinates);
    map.addVertex(settlement);
    chunk.place(coordinates.getFirst(), coordinates.getSecond(), LocationType.SETTLEMENT);
    return settlement;
  }

  private static void connectCloseSettlements(Graph<AbstractLocation> map, Chunk chunk) {
    var vertices = map.getVertices();
    for (var reference : vertices) {
      var refLocation = reference.getLocation();
      for (var other : vertices) {
        if (reference.equals(other)) {
          continue;
        }
        var otherLocation = other.getLocation();
        var otherCoordinates = otherLocation.getCoordinates();
        var distance = chunk.calculateDistance(refLocation.getCoordinates(), otherCoordinates);
        log.info(
            "Distance between {} and {} is {} km",
            refLocation.getName(),
            otherLocation.getName(),
            distance);
        if (distance < ChunkComponent.MAX_NEIGHBOUR_DISTANCE) {
          addConnections(map, reference, other, distance);
        }
      }
    }
  }

  private static void connectAtLeastOneSettlement(Graph<AbstractLocation> map, Chunk chunk) {
    var vertices = map.getVertices();
    for (var reference : vertices) {
      if (reference.getLocation() instanceof Settlement settlement
          && settlement.getNeighbours().isEmpty()) {
        var closetNeighbour = findClosestNeighbour(chunk, settlement, reference, vertices);
        if (closetNeighbour != null) {
          var distance =
              chunk.calculateDistance(
                  settlement.getCoordinates(), closetNeighbour.getLocation().getCoordinates());
          addConnections(map, reference, closetNeighbour, distance);
        }
      }
    }
  }

  private static void connectDisconnectedGroups(Graph<AbstractLocation> map, Chunk chunk) {
    var connectivityResult = findDisconnectedVertices(map);
    var unvisitedVertices = connectivityResult.unvisitedVertices();
    if (unvisitedVertices.isEmpty()) {
      return;
    }
    for (var unvisitedVertex : unvisitedVertices) {
      if (unvisitedVertex.getLocation() instanceof Settlement settlement) {
        var closestNeighbour =
            findClosestNeighbour(
                chunk,
                settlement,
                unvisitedVertex,
                connectivityResult.visitedVertices().stream().toList());
        if (closestNeighbour != null) {
          var distance =
              chunk.calculateDistance(
                  settlement.getCoordinates(), closestNeighbour.getLocation().getCoordinates());
          addConnections(map, unvisitedVertex, closestNeighbour, distance);
        }
      }
    }
  }

  private static void addConnections(
      Graph<AbstractLocation> map,
      Vertex<AbstractLocation> l1,
      Vertex<AbstractLocation> l2,
      int distance) {
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

  private static Vertex<AbstractLocation> findClosestNeighbour(
      Chunk chunk,
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
      var distance = chunk.calculateDistance(settlement.getCoordinates(), otherCoordinates);
      if (distance < minDistance) {
        minDistance = distance;
        closestNeighbor = other;
      }
    }
    return closestNeighbor;
  }

  private static ConnectivityResult findDisconnectedVertices(Graph<AbstractLocation> graph) {
    Set<Vertex<AbstractLocation>> visitedVertices = new HashSet<>();
    Set<Vertex<AbstractLocation>> unvisitedVertices = new HashSet<>(graph.getVertices());

    if (!unvisitedVertices.isEmpty()) {
      Vertex<AbstractLocation> startVertex = unvisitedVertices.iterator().next();
      Graph.traverseGraphDepthFirst(startVertex, visitedVertices, unvisitedVertices);
    }
    return new ConnectivityResult(visitedVertices, unvisitedVertices);
  }

  private record ConnectivityResult(
      Set<Vertex<AbstractLocation>> visitedVertices,
      Set<Vertex<AbstractLocation>> unvisitedVertices) {}
}
