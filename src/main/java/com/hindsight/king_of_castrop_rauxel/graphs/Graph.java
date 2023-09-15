package com.hindsight.king_of_castrop_rauxel.graphs;

import com.hindsight.king_of_castrop_rauxel.location.AbstractLocation;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class Graph<T extends AbstractLocation> {

  private final List<Vertex<T>> vertices = new ArrayList<>();
  private final boolean isWeighted;

  public Graph(boolean isWeighted) {
    this.isWeighted = isWeighted;
  }

  public Vertex<T> addVertex(T location) {
    Vertex<T> newVertex = new Vertex<>(location);
    this.vertices.add(newVertex);
    return newVertex;
  }

  public void addEdge(Vertex<T> vertex1, Vertex<T> vertex2, Integer weight) {
    if (!this.isWeighted) {
      weight = null;
    }
    vertex2.addEdge(vertex1, weight);
    vertex1.addEdge(vertex2, weight);
  }

  public void removeEdge(Vertex<T> vertex1, Vertex<T> vertex2) {
    vertex1.removeEdge(vertex2);
    vertex2.removeEdge(vertex1);
  }

  public void removeVertex(Vertex<T> vertex) {
    this.vertices.remove(vertex);
  }

  public Vertex<T> getVertexByValue(T location) {
    for (Vertex<T> vertex : this.vertices) {
      if (vertex.getLocation().equals(location)) {
        return vertex;
      }
    }
    return null;
  }

  public void log() {
    for (Vertex<T> vertex : this.vertices) {
      vertex.log(isWeighted);
    }
  }
}
