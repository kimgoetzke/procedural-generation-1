package com.hindsight.king_of_castrop_rauxel.graphs;

import com.hindsight.king_of_castrop_rauxel.location.Location;
import com.hindsight.king_of_castrop_rauxel.world.Coordinates;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

@Slf4j
@Getter
public class Graph {

  private final List<Vertex> vertices = new ArrayList<>();
  private final boolean isWeighted;

  public Graph(boolean isWeighted) {
    this.isWeighted = isWeighted;
  }

  public Vertex addVertex(Location location) {
    var newVertex = new Vertex(location);
    this.vertices.add(newVertex);
    return newVertex;
  }

  public void addEdge(Vertex vertex1, Vertex vertex2, Integer weight) {
    if (!this.isWeighted) {
      weight = null;
    }
    vertex2.addEdge(vertex1, weight);
    vertex1.addEdge(vertex2, weight);
  }

  public Vertex getVertex(Location location) {
    for (var vertex : this.vertices) {
      if (vertex.getDto().id().equals(location.getId())) {
        log.debug("{} matches {} for:", vertex.getDto().id(), location.getId());
        log.debug(" - {}", vertex.getDto());
        log.debug(" - {}", location);
        return vertex;
      }
    }
    return null;
  }

  // TODO: Only allow retrieving with global coordinates
  public Vertex getVertex(Pair<Integer, Integer> anyCoords, Coordinates.CoordType type) {
    var rX = (int) anyCoords.getFirst();
    var rY = (int) anyCoords.getSecond();
    for (var vertex : this.vertices) {
      var vCoords =
          switch (type) {
            case WORLD -> vertex.getDto().coordinates().getWorld();
            case GLOBAL -> vertex.getDto().coordinates().getGlobal();
            case CHUNK -> vertex.getDto().coordinates().getChunk();
          };
      var vX = (int) vCoords.getFirst();
      var vY = (int) vCoords.getSecond();
      if (vX == rX && vY == rY) {
        return vertex;
      }
    }
    return null;
  }

  public void log() {
    log.info("Graph: ");
    for (var vertex : this.vertices) {
      vertex.log(isWeighted);
    }
  }

  public static void traverseGraphDepthFirst(
      Vertex currentVertex, Set<Vertex> visitedVertices, Set<Vertex> unvisitedVertices) {
    if (visitedVertices.contains(currentVertex)) {
      return;
    }
    visitedVertices.add(currentVertex);
    unvisitedVertices.remove(currentVertex);
    for (var edge : currentVertex.getEdges()) {
      traverseGraphDepthFirst(edge.end(), visitedVertices, unvisitedVertices);
    }
  }
}
