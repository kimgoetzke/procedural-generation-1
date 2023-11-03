package com.hindsight.king_of_castrop_rauxel.world;

import static com.hindsight.king_of_castrop_rauxel.world.Chunk.*;

import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.graphs.Vertex;
import com.hindsight.king_of_castrop_rauxel.location.AbstractLocation;

import java.util.*;

import com.hindsight.king_of_castrop_rauxel.location.Settlement;
import com.hindsight.king_of_castrop_rauxel.utils.DataServices;
import com.hindsight.king_of_castrop_rauxel.utils.Generators;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

@Slf4j
public class ChunkHandler {

  @Getter private final AppProperties appProperties;
  private final AppProperties.ChunkProperties chunkProperties;
  private final Graph<AbstractLocation> map;
  private final Generators generators;
  private final DataServices dataServices;

  public ChunkHandler(
      Graph<AbstractLocation> map,
      AppProperties appProperties,
      Generators generators,
      DataServices dataServices) {
    this.map = map;
    this.appProperties = appProperties;
    this.chunkProperties = appProperties.getChunkProperties();
    this.generators = generators;
    this.dataServices = dataServices;
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
    var settlement =
        new Settlement(worldCoords, chunkCoords, generators, dataServices, appProperties);
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
        if (distance < appProperties.getChunkProperties().maxGuaranteedNeighbourDistance()) {
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

  public boolean isInsideTriggerZone(Pair<Integer, Integer> chunkCoords) {
    var chunkSize = chunkProperties.size();
    var generationTriggerZone = chunkProperties.generationTriggerZone();
    return chunkCoords.getFirst() > chunkSize - generationTriggerZone
        || chunkCoords.getFirst() < generationTriggerZone
        || chunkCoords.getSecond() > chunkSize - generationTriggerZone
        || chunkCoords.getSecond() < generationTriggerZone;
  }

  // TODO: Expand to include all 8 directions
  public CardinalDirection nextChunkPosition(Pair<Integer, Integer> chunkCoords) {
    var chunkSize = chunkProperties.size();
    var generationTriggerZone = chunkProperties.generationTriggerZone();
    if (chunkCoords.getFirst() > chunkSize - generationTriggerZone) {
      return CardinalDirection.EAST;
    } else if (chunkCoords.getFirst() < generationTriggerZone) {
      return CardinalDirection.WEST;
    } else if (chunkCoords.getSecond() > chunkSize - generationTriggerZone) {
      return CardinalDirection.NORTH;
    } else if (chunkCoords.getSecond() < generationTriggerZone) {
      return CardinalDirection.SOUTH;
    } else {
      return CardinalDirection.THIS;
    }
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

  public <T extends AbstractLocation> void logDisconnectedVertices(Graph<T> graph) {
    var result = evaluateConnectivity(graph);
    log.info("Unvisited vertices: {}", result.unvisitedVertices().size());
    result.unvisitedVertices().forEach(v -> log.info("- " + v.getLocation().getBriefSummary()));
    log.info("Visited vertices: {}", result.visitedVertices().size());
    result.visitedVertices().forEach(v -> log.info("- " + v.getLocation().getBriefSummary()));
  }

  protected record ConnectivityResult<T extends AbstractLocation>(
      Set<Vertex<T>> visitedVertices, Set<Vertex<T>> unvisitedVertices) {}
}
