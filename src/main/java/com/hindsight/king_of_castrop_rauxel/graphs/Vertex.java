package com.hindsight.king_of_castrop_rauxel.graphs;

import com.hindsight.king_of_castrop_rauxel.location.Location;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Vertex<T extends Location> {

  @EqualsAndHashCode.Include private final String id;
  private final Set<Edge<T>> edges;
  private final T location;

  public Vertex(T location) {
    this.id =
        "VER~"
            + location.getName().substring(0, 3).toUpperCase()
            + location.getCoordinates().getGlobal().getFirst()
            + location.getCoordinates().getGlobal().getSecond();
    this.location = location;
    this.edges = new LinkedHashSet<>();
  }

  public void addEdge(Vertex<T> endVertex, Integer weight) {
    this.edges.add(new Edge<>(this, endVertex, weight));
  }

  public void removeEdge(Vertex<T> endVertex) {
    this.edges.removeIf(edge -> edge.end().equals(endVertex));
  }

  public void log(boolean showWeight) {
    if (edges.isEmpty()) {
      log.info(
          "- " + location.getName() + " " + location.getCoordinates().globalToString() + " -->");
      return;
    }
    StringBuilder message = new StringBuilder();
    boolean first = true;
    for (Edge<T> edge : edges) {
      if (first) {
        message
            .append("- ")
            .append(edge.start().location.getName())
            .append(" ")
            .append(edge.start().location.getCoordinates().globalToString())
            .append(" --> ");
        first = false;
      }
      message.append(edge.end().location.getName());
      if (showWeight) {
        message.append(" (").append(edge.weight()).append(")");
      }
      message.append(", ");
    }
    message.setLength(message.length() - 2);
    log.info(message.toString());
  }

  @Override
  public String toString() {
    return "Vertex(id="
        + id
        + ", edges="
        + edges.stream().map(e -> e.end().location.getName()).toList()
        + ", location="
        + location
        + ')';
  }
}
