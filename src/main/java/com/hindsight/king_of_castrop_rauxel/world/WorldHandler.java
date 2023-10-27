package com.hindsight.king_of_castrop_rauxel.world;

import static com.hindsight.king_of_castrop_rauxel.configuration.AppConstants.*;
import static com.hindsight.king_of_castrop_rauxel.world.Chunk.*;

import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.graphs.Vertex;
import com.hindsight.king_of_castrop_rauxel.location.AbstractLocation;
import com.hindsight.king_of_castrop_rauxel.location.Settlement;
import com.hindsight.king_of_castrop_rauxel.utils.Generators;
import java.util.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WorldHandler {

  private final Graph<AbstractLocation> map;
  private final Generators generators;

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

    CardinalDirection(String name, int ordinal) {
      this.name = name;
      this.ordinal = ordinal;
    }
  }

  public enum Strategy {
    DEFAULT,
    NONE,
  }

  public void populate(Chunk chunk, Strategy strategy) {
    generateSettlements(map, chunk);
    if (Strategy.DEFAULT == strategy) {
      connectAnyWithinNeighbourDistance(map);
      connectNeighbourlessToClosest(map);
      connectDisconnectedToClosestConnected(map);
    }
  }

  protected void generateSettlements(Graph<AbstractLocation> map, Chunk chunk) {
    var settlementsCount = chunk.getDensity();
    for (int i = 0; i < settlementsCount; i++) {
      var chunkCoords = chunk.getRandomCoords();
      placeSettlement(map, chunk, chunkCoords);
    }
  }

  private void placeSettlement(
      Graph<AbstractLocation> map, Chunk chunk, Pair<Integer, Integer> chunkCoords) {
    var worldCoords = chunk.getCoordinates().getWorld();
    var settlement = new Settlement(worldCoords, chunkCoords, generators);
    map.addVertex(settlement);
    chunk.place(chunkCoords, LocationType.SETTLEMENT);
  }

  /** Connects any vertices that are within a certain distance of each other. */
  protected <T extends AbstractLocation> void connectAnyWithinNeighbourDistance(Graph<T> map) {
    var vertices = map.getVertices();
    for (var reference : vertices) {
      for (var other : vertices) {
        if (reference.equals(other)) {
          continue;
        }
        var distance = reference.getLocation().distanceTo(other.getLocation());
        if (distance < MAX_GUARANTEED_NEIGHBOUR_DISTANCE) {
          addConnections(map, reference, other, distance);
        }
      }
    }
  }

  /**
   * Connects any vertices that have no neighbours to the closest vertex. Does NOT guarantee that
   * all vertices will be connected to the graph. This method will skip any vertex that has been
   * connected while running the algorithm even if this vertex has an even closer neighbour.
   * Example: A and B are already connected. C's closed neighbour, D, is 100km away. D to A is 10km.
   * C will be connected to D and D will be skipped because it now has a neighbour, despite D's
   * closest neighbour being A.
   */
  protected void connectNeighbourlessToClosest(Graph<AbstractLocation> map) {
    var vertices = map.getVertices();
    for (var reference : vertices) {
      var refLocation = reference.getLocation();
      var hasNoEdges = map.getVertexByValue(refLocation).getEdges().isEmpty();
      var hasNoNeighbours = refLocation.getNeighbours().isEmpty();
      if ((hasNoEdges && !hasNoNeighbours) || (!hasNoEdges && hasNoNeighbours)) {
        throw new IllegalStateException(
            String.format(
                "Vertex '%s' has %d edges and %d neighbours but both must have the same value",
                refLocation.getName(),
                refLocation.getNeighbours().size(),
                map.getVertexByValue(refLocation).getEdges().size()));
      }
      if (hasNoNeighbours) {
        var closestNeighbour = closestNeighbourTo(reference, vertices);
        if (closestNeighbour != null) {
          var distance = refLocation.distanceTo(closestNeighbour.getLocation());
          addConnections(map, reference, closestNeighbour, distance);
        }
      }
    }
  }

  /**
   * Connects any vertices that are not connected to the closest vertex that is connected. This
   * method guarantees that all vertices will be connected to the graph. However, it will ignore
   * close vertices if they have not been connected to the graph yet. Executing this method prior to
   * any other connection algorithm will provide odd results.
   */
  protected void connectDisconnectedToClosestConnected(Graph<AbstractLocation> map) {
    var connectivity = evaluateConnectivity(map);
    var visitedVertices = new ArrayList<>(connectivity.visitedVertices());
    var unvisitedVertices = connectivity.unvisitedVertices();
    if (unvisitedVertices.isEmpty()) {
      return;
    }
    for (var unvisitedVertex : unvisitedVertices) {
      var refLocation = unvisitedVertex.getLocation();
      var closestNeighbour = closestNeighbourTo(unvisitedVertex, visitedVertices);
      if (closestNeighbour != null) {
        var distance = refLocation.distanceTo(closestNeighbour.getLocation());
        addConnections(map, unvisitedVertex, closestNeighbour, distance);
        visitedVertices.add(unvisitedVertex);
      }
    }
  }

  protected <T extends AbstractLocation> void addConnections(
      Graph<T> map, Vertex<T> vertex1, Vertex<T> vertex2, int distance) {
    map.addEdge(vertex1, vertex2, distance);
    var v1Location = vertex1.getLocation();
    var v2Location = vertex2.getLocation();
    v1Location.addNeighbour(v2Location);
    v2Location.addNeighbour(v1Location);
    log.debug(
        "Added {} and {} (distance: {} km) as neighbours of each other",
        v2Location.getName(),
        v1Location.getName(),
        distance);
  }

  private <T extends AbstractLocation> Vertex<T> closestNeighbourTo(
      Vertex<T> reference, List<Vertex<T>> vertices) {
    Vertex<T> closestNeighbor = null;
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
    log.debug(
        "Closest neighbour of {} is {} (distance: {} km)",
        reference.getLocation().getName(),
        closestNeighbor != null && closestNeighbor.getLocation() != null
            ? closestNeighbor.getLocation().getName()
            : "'null'",
        minDistance);
    return closestNeighbor;
  }

  public <T extends AbstractLocation> Vertex<T> closestLocationTo(
      Pair<Integer, Integer> globalCoords, List<Vertex<T>> vertices) {
    Vertex<T> closestNeighbor = null;
    var minDistance = Integer.MAX_VALUE;
    for (var vertex : vertices) {
      var distance = vertex.getLocation().getCoordinates().distanceTo(globalCoords);
      if (distance < minDistance) {
        minDistance = distance;
        closestNeighbor = vertex;
      }
    }
    return closestNeighbor;
  }

  public <T extends AbstractLocation> void logDisconnectedVertices(Graph<T> graph) {
    var result = evaluateConnectivity(graph);
    log.info("Unvisited vertices: {}", result.unvisitedVertices().size());
    result.unvisitedVertices().forEach(v -> log.info("- " + v.getLocation().getBriefSummary()));
    log.info("Visited vertices: {}", result.visitedVertices().size());
    result.visitedVertices().forEach(v -> log.info("- " + v.getLocation().getBriefSummary()));
  }

  protected <T extends AbstractLocation> ConnectivityResult<T> evaluateConnectivity(
      Graph<T> graph) {
    var visitedVertices = new LinkedHashSet<Vertex<T>>();
    var unvisitedVertices = new LinkedHashSet<>(graph.getVertices());
    if (!unvisitedVertices.isEmpty()) {
      var startVertex = unvisitedVertices.iterator().next();
      Graph.traverseGraphDepthFirst(startVertex, visitedVertices, unvisitedVertices);
    }
    return new ConnectivityResult<>(visitedVertices, unvisitedVertices);
  }

  // TODO: Make this a wrapper within which code can be executed
  public <T> void logOutcome(LogStats stats, Graph<AbstractLocation> map, Class<T> clazz) {
    log.info("Generation took {} seconds", (System.currentTimeMillis() - stats.startT) / 1000.0);
    if (clazz.equals(World.class)) {
      log.info("Generated {} settlements", map.getVertices().size() - stats.prevSetCount);
    }
  }

  public LogStats getStats(Graph<AbstractLocation> map) {
    return new LogStats(
        System.currentTimeMillis(),
        map.getVertices().size(),
        map.getVertices().stream().map(Vertex::getLocation).toList());
  }

  public record LogStats(long startT, int prevSetCount, List<AbstractLocation> prevSet) {}

  protected record ConnectivityResult<T extends AbstractLocation>(
      Set<Vertex<T>> visitedVertices, Set<Vertex<T>> unvisitedVertices) {}
}
