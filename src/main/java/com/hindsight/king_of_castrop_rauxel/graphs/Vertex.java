package com.hindsight.king_of_castrop_rauxel.graphs;

import com.hindsight.king_of_castrop_rauxel.location.Location;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
public class Vertex<T extends Location> {

  private final T location;
  private final List<Edge> edges;

  public Vertex(T location) {
    this.location = location;
    this.edges = new ArrayList<>();
  }

  public void addEdge(Vertex<T> endVertex, Integer weight) {
    this.edges.add(new Edge(this, endVertex, weight));
  }

  public void removeEdge(Vertex<T> endVertex) {
    this.edges.removeIf(edge -> edge.end().equals(endVertex));
  }

  public void log(boolean showWeight) {
    StringBuilder message = new StringBuilder();
    if (this.edges.isEmpty()) {
      log.info(this.location.getName() + " -->");
      return;
    }
    for (int i = 0; i < this.edges.size(); i++) {
      if (i == 0) {
        message.append(this.edges.get(i).start().location.getName()).append(" -->  ");
      }
      message.append(this.edges.get(i).end().location.getName());
      if (showWeight) {
        message.append(" (").append(this.edges.get(i).weight()).append(")");
      }
      if (i != this.edges.size() - 1) {
        message.append(", ");
      }
    }
    log.info(message.toString());
  }
}
