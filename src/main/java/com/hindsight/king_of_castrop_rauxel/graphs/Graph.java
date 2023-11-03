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
    Vertex newVertex = new Vertex(location);
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

  public void removeEdge(Vertex vertex1, Vertex vertex2) {
    vertex1.removeEdge(vertex2);
    vertex2.removeEdge(vertex1);
  }

  public void removeVertex(Vertex vertex) {
    this.vertices.remove(vertex);
  }

  public Vertex getVertexByValue(Location location) {
    for (var vertex : this.vertices) {
      if (vertex.getLocation().equals(location)) {
        return vertex;
      }
    }
    return null;
  }

  public Vertex getVertexByValue(Pair<Integer, Integer> anyCoords, Coordinates.CoordType type) {
    var rX = (int) anyCoords.getFirst();
    var rY = (int) anyCoords.getSecond();
    for (var vertex : this.vertices) {
      var vCoords =
          switch (type) {
            case WORLD -> vertex.getLocation().getCoordinates().getWorld();
            case GLOBAL -> vertex.getLocation().getCoordinates().getGlobal();
            case CHUNK -> vertex.getLocation().getCoordinates().getChunk();
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
    for (Vertex vertex : this.vertices) {
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
