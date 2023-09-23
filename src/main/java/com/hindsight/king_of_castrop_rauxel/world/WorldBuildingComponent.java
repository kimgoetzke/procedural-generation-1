package com.hindsight.king_of_castrop_rauxel.world;

import static com.hindsight.king_of_castrop_rauxel.world.Chunk.*;

import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.graphs.Vertex;
import com.hindsight.king_of_castrop_rauxel.location.AbstractLocation;
import com.hindsight.king_of_castrop_rauxel.location.Settlement;
import com.hindsight.king_of_castrop_rauxel.utils.StringGenerator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WorldBuildingComponent {

  public static final int WORLD_SIZE = 50;
  public static final int REMOVAL_ZONE = 2;

  @Getter
  public enum CardinalDirection {
    THIS("This", 0),
    NORTH("North", 1),
    NORTH_EAST("North-east", 2),
    EAST("East", 3),
    SOUTH_EAST("South-east", 4),
    SOUTH("South", 5),
    SOUTH_WEST("South-west", 6),
    WEST("West", 7),
    NORTH_WEST("North-west", 8);

    private final String name;
    private final int ordinal;

    CardinalDirection(String s, int i) {
      this.name = s;
      this.ordinal = i;
    }
  }

  public static Settlement build(
      World world, Graph<AbstractLocation> map, StringGenerator stringGenerator) {
    var stats = getStats(map);
    var chunk = new Chunk(world.getCenter());
    var startLocation = placeSettlement(map, chunk, chunk.getCenterCoords(), stringGenerator);
    generateSettlements(map, chunk, stringGenerator);
    connectCloseSettlements(map);
    connectAtLeastOneSettlement(map);
    connectDisconnectedGroups(map);
    world.placeChunk(chunk);
    startLocation.load();
    logOutcome(stats, map);
    return startLocation;
  }

  public static void buildNext(
      CardinalDirection whereNext,
      World world,
      Graph<AbstractLocation> map,
      StringGenerator stringGenerator) {
    var stats = getStats(map);
    Chunk nextChunk = new Chunk(world.getPosition(whereNext));
    generateSettlements(map, nextChunk, stringGenerator);
    connectCloseSettlements(map);
    connectAtLeastOneSettlement(map);
    connectDisconnectedGroups(map);
    world.placeChunk(nextChunk, whereNext);
    logOutcome(stats, map);
  }

  private static void logOutcome(LogStats stats, Graph<AbstractLocation> map) {
    map.log();
    log.info("Generation took {} seconds", (System.currentTimeMillis() - stats.startTime) / 1000.0);
    log.info("Generated {} settlements", map.getVertices().size() - stats.prevSettlementCount);
    log.info("List of generated settlements:");
    map.getVertices().stream()
        .filter(v -> !stats.prevSettlements.contains(v.getLocation()))
        .forEach(vertex -> log.info("- " + vertex.getLocation().getBriefSummary()));
  }

  private static void generateSettlements(
      Graph<AbstractLocation> map, Chunk chunk, StringGenerator stringGenerator) {
    var settlementsCount = chunk.getDensity();
    for (int i = 0; i < settlementsCount; i++) {
      var chunkCoords = chunk.getRandomCoords();
      placeSettlement(map, chunk, chunkCoords, stringGenerator);
    }
  }

  private static Settlement placeSettlement(
      Graph<AbstractLocation> map,
      Chunk chunk,
      Pair<Integer, Integer> chunkCoords,
      StringGenerator stringGenerator) {
    var worldCoords = chunk.getCoordinates().getWorld();
    var settlement = new Settlement(worldCoords, chunkCoords, stringGenerator);
    map.addVertex(settlement);
    chunk.place(chunkCoords, LocationType.SETTLEMENT);
    return settlement;
  }

  private static void connectCloseSettlements(Graph<AbstractLocation> map) {
    var vertices = map.getVertices();
    for (var reference : vertices) {
      for (var other : vertices) {
        if (reference.equals(other)) {
          continue;
        }
        var distance = reference.getLocation().distanceTo(other.getLocation());
        if (distance < ChunkComponent.MAX_NEIGHBOUR_DISTANCE) {
          addConnections(map, reference, other, distance);
        }
      }
    }
  }

  private static void connectAtLeastOneSettlement(Graph<AbstractLocation> map) {
    var vertices = map.getVertices();
    for (var reference : vertices) {
      var refLocation = reference.getLocation();
      if (refLocation.getNeighbours().isEmpty()) {
        var closestNeighbour = findClosestNeighbour(reference, vertices);
        if (closestNeighbour != null) {
          var distance = refLocation.distanceTo(closestNeighbour.getLocation());
          addConnections(map, reference, closestNeighbour, distance);
        }
      }
    }
  }

  private static void connectDisconnectedGroups(Graph<AbstractLocation> map) {
    var connectivityResult = findDisconnectedVertices(map);
    var unvisitedVertices = connectivityResult.unvisitedVertices();
    if (unvisitedVertices.isEmpty()) {
      return;
    }
    for (var unvisitedVertex : unvisitedVertices) {
      var refLocation = unvisitedVertex.getLocation();
      var visitedVertices = connectivityResult.visitedVertices().stream().toList();
      var closestNeighbour = findClosestNeighbour(unvisitedVertex, visitedVertices);
      if (closestNeighbour != null) {
        var distance = refLocation.distanceTo(closestNeighbour.getLocation());
        addConnections(map, unvisitedVertex, closestNeighbour, distance);
      }
    }
  }

  private static void addConnections(
      Graph<AbstractLocation> map,
      Vertex<AbstractLocation> vertex1,
      Vertex<AbstractLocation> vertex2,
      int distance) {
    map.addEdge(vertex1, vertex2, distance);
    var v1Location = vertex1.getLocation();
    var v2Location = vertex2.getLocation();
    v1Location.addNeighbour(v2Location);
    v2Location.addNeighbour(v1Location);
    log.info(
        "Added {} and {} (distance: {} km) as neighbours of each other",
        v2Location.getName(),
        v1Location.getName(),
        distance);
  }

  private static Vertex<AbstractLocation> findClosestNeighbour(
      Vertex<AbstractLocation> reference, List<Vertex<AbstractLocation>> vertices) {
    Vertex<AbstractLocation> closestNeighbor = null;
    var minDistance = Integer.MAX_VALUE;
    for (var other : vertices) {
      if (reference.equals(other)) {
        continue;
      }
      var distance = reference.getLocation().distanceTo(other.getLocation());
      if (distance < minDistance) {
        minDistance = distance;
        closestNeighbor = other;
      }
    }
    return closestNeighbor;
  }

  public static void logDisconnectedVertices(Graph<AbstractLocation> graph) {
    var result = findDisconnectedVertices(graph);
    log.info("Unvisited vertices: {}", result.unvisitedVertices().size());
    result.unvisitedVertices().forEach(v -> log.info("- " + v.getLocation().getBriefSummary()));
    log.info("Visited vertices: {}", result.visitedVertices().size());
  }

  private static ConnectivityResult findDisconnectedVertices(Graph<AbstractLocation> graph) {
    var visitedVertices = new LinkedHashSet<Vertex<AbstractLocation>>();
    var unvisitedVertices = new LinkedHashSet<>(graph.getVertices());
    if (!unvisitedVertices.isEmpty()) {
      var startVertex = unvisitedVertices.iterator().next();
      Graph.traverseGraphDepthFirst(startVertex, visitedVertices, unvisitedVertices);
    }
    return new ConnectivityResult(visitedVertices, unvisitedVertices);
  }

  private static LogStats getStats(Graph<AbstractLocation> map) {
    return new LogStats(
        System.currentTimeMillis(),
        map.getVertices().size(),
        map.getVertices().stream().map(Vertex::getLocation).toList());
  }

  private record ConnectivityResult(
      Set<Vertex<AbstractLocation>> visitedVertices,
      Set<Vertex<AbstractLocation>> unvisitedVertices) {}

  private record LogStats(
      long startTime, int prevSettlementCount, List<AbstractLocation> prevSettlements) {}
}
