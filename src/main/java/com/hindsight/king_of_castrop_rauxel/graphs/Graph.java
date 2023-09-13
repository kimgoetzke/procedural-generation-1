package com.hindsight.king_of_castrop_rauxel.graphs;

import java.util.ArrayList;
import java.util.List;

import com.hindsight.king_of_castrop_rauxel.location.Location;
import lombok.Getter;

@Getter
public class Graph {

  private final List<Vertex> vertices = new ArrayList<>();
  private final boolean isWeighted;
  private final boolean isDirected;

  public Graph(boolean isWeighted, boolean isDirected) {
    this.isWeighted = isWeighted;
    this.isDirected = isDirected;
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
    if (!this.isDirected) {
      vertex2.addEdge(vertex1, weight);
    }
    vertex1.addEdge(vertex2, weight);
  }

  public void removeEdge(Vertex vertex1, Vertex vertex2) {
    vertex1.removeEdge(vertex2);
    if (!this.isDirected) {
      vertex2.removeEdge(vertex1);
    }
  }

  public void removeVertex(Vertex vertex) {
    this.vertices.remove(vertex);
  }

  public Vertex getVertexByValue(Location location) {
    for (Vertex vertex : this.vertices) {
      if (vertex.getLocation().equals(location)) {
        return vertex;
      }
    }
    return null;
  }

  public void log() {
    for (Vertex vertex : this.vertices) {
      vertex.log(isWeighted);
    }
  }
}
