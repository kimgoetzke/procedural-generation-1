package com.hindsight.king_of_castrop_rauxel.world;

import static com.hindsight.king_of_castrop_rauxel.world.Chunk.*;

import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.graphs.Vertex;
import com.hindsight.king_of_castrop_rauxel.location.Location;
import com.hindsight.king_of_castrop_rauxel.location.Settlement;
import com.hindsight.king_of_castrop_rauxel.utils.DataServices;
import com.hindsight.king_of_castrop_rauxel.utils.Generators;
import java.util.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

@Slf4j
public class ChunkHandler {

  @Getter private final AppProperties appProperties;
  private final AppProperties.ChunkProperties chunkProperties;
  private final Graph map;
  private final Generators generators;
  private final DataServices dataServices;

  public ChunkHandler(
      Graph map, AppProperties appProperties, Generators generators, DataServices dataServices) {
    this.map = map;
    this.appProperties = appProperties;
    this.chunkProperties = appProperties.getChunkProperties();
    this.generators = generators; // May require initialisation before use
    this.dataServices = dataServices;
  }

  public ChunkHandler initialise(Random random) {
    generators.initialiseAll(random);
    return this;
  }

  public int getTargetLevel(Coordinates coordinates) {
    return generators.terrainGenerator().getTargetLevel(coordinates);
  }

  public void populate(Chunk chunk, Strategy strategy) {
    generateSettlements(map, chunk);
    if (Strategy.DEFAULT == strategy) {
      connectAnyWithinNeighbourDistance(map);
      connectNeighbourlessToClosest(map);
      connectDisconnectedToClosestConnected(map);
    }
  }

  protected void generateSettlements(Graph map, Chunk chunk) {
    var settlementsCount = chunk.getDensity();
    for (int i = 0; i < settlementsCount; i++) {
      var chunkCoords = chunk.getRandomCoords();
      placeSettlement(map, chunk, chunkCoords);
    }
  }

  private void placeSettlement(Graph map, Chunk chunk, Pair<Integer, Integer> chunkCoords) {
    var worldCoords = chunk.getCoordinates().getWorld();
    var s = new Settlement(worldCoords, chunkCoords, generators, dataServices, appProperties);
    map.addVertex(s);
    chunk.place(chunkCoords, LocationType.SETTLEMENT);
  }

  /** Connects any vertices that are within a certain distance of each other. */
  protected void connectAnyWithinNeighbourDistance(Graph map) {
    var vertices = map.getVertices();
    for (var reference : vertices) {
      for (var other : vertices) {
        if (reference.equals(other)) {
          continue;
        }
        var distance = reference.getLocDetails().distanceTo(other.getLocDetails());
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
  protected void connectNeighbourlessToClosest(Graph map) {
    var vertices = map.getVertices();
    for (var reference : vertices) {
      var refLocation = reference.getLocDetails();
      var hasNoNeighbours = refLocation.getNeighbours().isEmpty();
      throwIfMisconfigured(map, refLocation, hasNoNeighbours);
      connectIfNoNeighbours(map, reference, hasNoNeighbours, vertices, refLocation);
    }
  }

  private static void throwIfMisconfigured(
      Graph map, Location refLocation, boolean hasNoNeighbours) {
    var hasNoEdges = map.getVertexByValue(refLocation).getEdges().isEmpty();
    if ((hasNoEdges && !hasNoNeighbours) || (!hasNoEdges && hasNoNeighbours)) {
      throw new IllegalStateException(
          String.format(
              "Vertex '%s' has %d edges and %d neighbours but both must have the same value",
              refLocation.getName(),
              refLocation.getNeighbours().size(),
              map.getVertexByValue(refLocation).getEdges().size()));
    }
  }

  private void connectIfNoNeighbours(
      Graph map,
      Vertex vert,
      boolean hasNoNeighbours,
      List<Vertex> vertices,
      Location refLocation) {
    if (hasNoNeighbours) {
      var closestNeighbour = closestNeighbourTo(vert, vertices);
      if (closestNeighbour != null) {
        var distance = refLocation.distanceTo(closestNeighbour.getLocDetails());
        addConnections(map, vert, closestNeighbour, distance);
      }
    }
  }

  /**
   * Connects any vertices that are not connected to the closest vertex that is connected. This
   * method guarantees that all vertices will be connected to the graph. However, it will ignore
   * close vertices if they have not been connected to the graph yet. Executing this method prior to
   * any other connection algorithm will provide odd results.
   */
  protected void connectDisconnectedToClosestConnected(Graph map) {
    var connectivity = evaluateConnectivity(map);
    var visitedVertices = new ArrayList<>(connectivity.visitedVertices());
    var unvisitedVertices = connectivity.unvisitedVertices();
    if (unvisitedVertices.isEmpty()) {
      return;
    }
    for (var unvisitedVertex : unvisitedVertices) {
      var refLocation = unvisitedVertex.getLocDetails();
      var closestNeighbour = closestNeighbourTo(unvisitedVertex, visitedVertices);
      if (closestNeighbour != null) {
        var distance = refLocation.distanceTo(closestNeighbour.getLocDetails());
        addConnections(map, unvisitedVertex, closestNeighbour, distance);
        visitedVertices.add(unvisitedVertex);
      }
    }
  }

  protected void addConnections(Graph map, Vertex vertex1, Vertex vertex2, int distance) {
    map.addEdge(vertex1, vertex2, distance);
    var v1Location = vertex1.getLocDetails();
    var v2Location = vertex2.getLocDetails();
    v1Location.addNeighbour(v2Location);
    v2Location.addNeighbour(v1Location);
    log.debug(
        "Added {} and {} (distance: {} km) as neighbours of each other",
        v2Location.name(),
        v1Location.name(),
        distance);
  }

  private Vertex closestNeighbourTo(Vertex reference, List<Vertex> vertices) {
    Vertex closestNeighbor = null;
    var minDistance = Integer.MAX_VALUE;
    for (var other : vertices) {
      if (reference.equals(other)) {
        continue;
      }
      var distance = reference.getLocDetails().distanceTo(other.getLocDetails());
      if (distance < minDistance) {
        minDistance = distance;
        closestNeighbor = other;
      }
    }
    log.debug(
        "Closest neighbour of {} is {} (distance: {} km)",
        reference.getLocDetails().name(),
        closestNeighbor != null && closestNeighbor.getLocDetails() != null
            ? closestNeighbor.getLocDetails().name()
            : "'null'",
        minDistance);
    return closestNeighbor;
  }

  public Vertex closestLocationTo(Pair<Integer, Integer> globalCoords, List<Vertex> vertices) {
    Vertex closestNeighbor = null;
    var minDistance = Integer.MAX_VALUE;
    for (var vertex : vertices) {
      var distance = vertex.getLocDetails().coordinates().distanceTo(globalCoords);
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

  protected ConnectivityResult evaluateConnectivity(Graph graph) {
    var visitedVertices = new LinkedHashSet<Vertex>();
    var unvisitedVertices = new LinkedHashSet<>(graph.getVertices());
    if (!unvisitedVertices.isEmpty()) {
      var startVertex = unvisitedVertices.iterator().next();
      Graph.traverseGraphDepthFirst(startVertex, visitedVertices, unvisitedVertices);
    }
    return new ConnectivityResult(visitedVertices, unvisitedVertices);
  }

  public void logDisconnectedVertices(Graph graph) {
    var result = evaluateConnectivity(graph);
    log.info("Unvisited vertices: {}", result.unvisitedVertices().size());
    result.unvisitedVertices().forEach(v -> log.info("- " + v.getLocDetails().getSummary()));
    log.info("Visited vertices: {}", result.visitedVertices().size());
    result.visitedVertices().forEach(v -> log.info("- " + v.getLocDetails().getSummary()));
  }

  protected record ConnectivityResult(Set<Vertex> visitedVertices, Set<Vertex> unvisitedVertices) {}
}
