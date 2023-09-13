package com.hindsight.king_of_castrop_rauxel.graphs;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
public class Vertex {

  private final String name;
  private final List<Edge> edges;

  public Vertex(String name) {
    this.name = name;
    this.edges = new ArrayList<>();
  }

  public void addEdge(Vertex endVertex, Integer weight) {
    this.edges.add(new Edge(this, endVertex, weight));
  }

  public void removeEdge(Vertex endVertex) {
    this.edges.removeIf(edge -> edge.end().equals(endVertex));
  }

  public void log(boolean showWeight) {
    StringBuilder message = new StringBuilder();
    if (this.edges.isEmpty()) {
      log.info(this.name + " -->");
      return;
    }
    for (int i = 0; i < this.edges.size(); i++) {
      if (i == 0) {
        message.append(this.edges.get(i).start().name).append(" -->  ");
      }
      message.append(this.edges.get(i).end().name);
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
