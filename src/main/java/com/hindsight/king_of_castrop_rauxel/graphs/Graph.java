package com.hindsight.king_of_castrop_rauxel.graphs;

import com.hindsight.king_of_castrop_rauxel.location.Location;
import com.hindsight.king_of_castrop_rauxel.world.Chunk;
import com.hindsight.king_of_castrop_rauxel.world.Coordinates;
import java.util.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

@Slf4j
@Getter
@NoArgsConstructor
public class Graph {

  private final Set<Vertex> vertices = new TreeSet<>(Comparator.comparing(Vertex::getId));

  public void addVertex(Location location) {
    var vertex = new Vertex(location);
    vertices.add(vertex);
  }

  public void addEdge(Vertex vertex1, Vertex vertex2, Integer weight) {
    vertex2.addEdge(vertex1, weight);
    vertex1.addEdge(vertex2, weight);
  }

  public Vertex getVertex(Location location) {
    for (var vertex : vertices) {
      if (vertex.getDto().id().equals(location.getId())) {
        return vertex;
      }
    }
    return null;
  }

  /** Returns a list of vertices that are within the given chunk i.e. same world coords. */
  public Set<Vertex> getVertices(Chunk chunk) {
    var worldCoords = chunk.getCoordinates().getWorld();
    var chunkVertices = new TreeSet<>(Comparator.comparing(Vertex::getId));
    for (var vertex : vertices) {
      if (vertex.getDto().coordinates().equalTo(worldCoords, Coordinates.CoordType.WORLD)) {
        chunkVertices.add(vertex);
      }
    }
    return chunkVertices;
  }

  // TODO: Only allow retrieving with global coordinates
  public Vertex getVertex(Pair<Integer, Integer> anyCoords, Coordinates.CoordType type) {
    var rX = (int) anyCoords.getFirst();
    var rY = (int) anyCoords.getSecond();
    for (var vertex : vertices) {
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

  public void clear() {
    vertices.clear();
  }

  public void log() {
    log.info("Graph: ");
    for (var vertex : vertices) {
      vertex.log(true);
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
