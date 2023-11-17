package com.hindsight.king_of_castrop_rauxel.world;

import static com.google.common.base.Preconditions.checkArgument;
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
  private final World world;
  private final Graph graph;
  private final Generators generators;
  private final DataServices dataServices;

  public ChunkHandler(
      World world,
      Graph graph,
      AppProperties appProperties,
      Generators generators,
      DataServices dataServices) {
    this.world = world;
    this.graph = graph;
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
    checkArgument(!chunk.isLoaded(), "Chunk must not be loaded");
    if (Strategy.DO_NOTHING == strategy) {
      return;
    }
    generateSettlements(chunk);
    if (Strategy.PLACE_ONLY == strategy) {
      return;
    }
    connectAnyWithinNeighbourDistance();
    // TODO: Do I need the below strategies?
    //    connectNeighbourlessToClosest();
    //    connectDisconnectedToClosestConnected();
  }

  protected void generateSettlements(Chunk chunk) {
    var settlementsCount = chunk.getDensity();
    for (int i = 0; i < settlementsCount; i++) {
      var chunkCoords = chunk.getRandomCoords();
      placeSettlement(chunk, chunkCoords);
    }
  }

  /**
   * Only temporarily permitted to facilitate development and testing but should not be used in
   * production because it allows generating locations in already loaded chunks, therefore
   * increasing the density set this chunk (which does not have any side effects now but may do in
   * the future).
   */
  public Settlement generateSettlement(Chunk chunk, Pair<Integer, Integer> chunkCoords) {
    log.warn("Generating settlement at {} manually - not permitted in production", chunkCoords);
    var worldCoords = chunk.getCoordinates().getWorld();
    return new Settlement(worldCoords, chunkCoords, generators, dataServices, appProperties);
  }

  private void placeSettlement(Chunk chunk, Pair<Integer, Integer> chunkCoords) {
    var worldCoords = chunk.getCoordinates().getWorld();
    var s = new Settlement(worldCoords, chunkCoords, generators, dataServices, appProperties);
    graph.addVertex(s);
    chunk.place(s);
  }

  /** Connects any vertices that are within a certain distance of each other. */
  protected void connectAnyWithinNeighbourDistance() {
    var vertices = graph.getVertices();
    for (var reference : vertices) {
      for (var other : vertices) {
        if (reference.equals(other)) {
          continue;
        }
        var distance = reference.getDto().distanceTo(other.getDto());
        if (distance < appProperties.getChunkProperties().maxGuaranteedNeighbourDistance()) {
          addConnections(reference, other, distance);
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
  protected void connectNeighbourlessToClosest() {
    var vertices = graph.getVertices();
    for (var reference : vertices) {
      var correctChunk = world.getChunk(reference.getDto().coordinates().getWorld());
      var location = correctChunk.getLocation(reference.getDto().coordinates());
      connectIfNoNeighbours(reference, vertices, location);
    }
  }

  private void connectIfNoNeighbours(Vertex vert, Set<Vertex> vertices, Location refLocation) {
    if (vert.getNeighbours().isEmpty()) {
      var closestNeighbour = closestNeighbourTo(vert, vertices);
      if (closestNeighbour != null) {
        var distance = refLocation.distanceTo(closestNeighbour.getDto());
        addConnections(vert, closestNeighbour, distance);
      }
    }
  }

  /**
   * Connects any vertices that are not connected to the closest vertex that is connected. This
   * method guarantees that all vertices will be connected to the graph. However, it will ignore
   * close vertices if they have not been connected to the graph yet. Executing this method prior to
   * any other connection algorithm will provide odd results.
   */
  protected void connectDisconnectedToClosestConnected() {
    var connectivity = evaluateConnectivity(graph);
    var visitedVertices = new HashSet<>(connectivity.visitedVertices());
    var unvisitedVertices = connectivity.unvisitedVertices();
    if (unvisitedVertices.isEmpty()) {
      return;
    }
    for (var unvisitedVertex : unvisitedVertices) {
      var refLocation = unvisitedVertex.getDto();
      var closestNeighbour = closestNeighbourTo(unvisitedVertex, visitedVertices);
      if (closestNeighbour != null) {
        var distance = refLocation.distanceTo(closestNeighbour.getDto());
        addConnections(unvisitedVertex, closestNeighbour, distance);
        visitedVertices.add(unvisitedVertex);
      }
    }
  }

  protected void addConnections(Vertex vertex1, Vertex vertex2, int distance) {
    graph.addEdge(vertex1, vertex2, distance);
    var v1Chunk = world.getChunk(vertex1.getDto().coordinates().getWorld());
    var v2Chunk = world.getChunk(vertex2.getDto().coordinates().getWorld());
    var v1Location = v1Chunk.getLocation(vertex1.getDto().coordinates());
    var v2Location = v2Chunk.getLocation(vertex2.getDto().coordinates());
    addNeighbourIfNotNull(v1Location, v2Location);
    addNeighbourIfNotNull(v2Location, v1Location);
    log.info(
        "Connected {} and {} (distance: {} km)",
        vertex1.getDto().name(),
        vertex2.getDto().name(),
        distance);
  }

  private static void addNeighbourIfNotNull(Location l1, Location l2) {
    if (l1 == null || l2 == null) {
      return;
    }
    l1.addNeighbour(l2);
  }

  private Vertex closestNeighbourTo(Vertex reference, Set<Vertex> vertices) {
    Vertex closestNeighbor = null;
    var minDistance = Integer.MAX_VALUE;
    for (var other : vertices) {
      if (reference.equals(other)) {
        continue;
      }
      var distance = reference.getDto().distanceTo(other.getDto());
      if (distance < minDistance) {
        minDistance = distance;
        closestNeighbor = other;
      }
    }
    log.debug(
        "Closest neighbour of {} is {} (distance: {} km)",
        reference.getDto().name(),
        closestNeighbor != null && closestNeighbor.getDto() != null
            ? closestNeighbor.getDto().name()
            : "'null'",
        minDistance);
    return closestNeighbor;
  }

  public Vertex closestLocationTo(Pair<Integer, Integer> globalCoords, Set<Vertex> vertices) {
    Vertex closestNeighbor = null;
    var minDistance = Integer.MAX_VALUE;
    for (var vertex : vertices) {
      var distance = vertex.getDto().coordinates().distanceTo(globalCoords);
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

  // TODO: Consider expanding to include all 8 directions
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
    log.info("Not traversed vertices: {}", result.unvisitedVertices().size());
    result.unvisitedVertices().forEach(v -> log.info("- " + v.getDto().getSummary()));
    log.info("Traversed vertices: {}", result.visitedVertices().size());
    result.visitedVertices().forEach(v -> log.info("- " + v.getDto().getSummary()));
  }

  protected record ConnectivityResult(Set<Vertex> visitedVertices, Set<Vertex> unvisitedVertices) {}
}
