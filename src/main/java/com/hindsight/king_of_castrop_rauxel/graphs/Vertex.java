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
public class Vertex {

  @EqualsAndHashCode.Include private final String id;
  private final Set<Edge> edges;
  private final LocationDto locDetails;

  public Vertex(Location location) {
    this.id =
        "VER~"
            + location.getName().substring(0, 3).toUpperCase()
            + location.getCoordinates().getGlobal().getFirst()
            + location.getCoordinates().getGlobal().getSecond();
    this.locDetails = LocationDto.from(location);
    this.edges = new LinkedHashSet<>();
  }

  public void addEdge(Vertex endVertex, Integer weight) {
    this.edges.add(new Edge(this, endVertex, weight));
  }

  public void removeEdge(Vertex endVertex) {
    this.edges.removeIf(edge -> edge.end().equals(endVertex));
  }

  public void log(boolean showWeight) {
    if (edges.isEmpty()) {
      log.info("- " + locDetails.name() + " " + locDetails.coordinates().globalToString() + " -->");
      return;
    }
    var message = new StringBuilder();
    var first = true;
    for (var edge : edges) {
      if (first) {
        message
            .append("- ")
            .append(edge.start().locDetails.name())
            .append(" ")
            .append(edge.start().locDetails.coordinates().globalToString())
            .append(" --> ");
        first = false;
      }
      message.append(edge.end().locDetails.name());
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
        + edges.stream().map(e -> e.end().locDetails.name()).toList()
        + ", location="
        + locDetails
        + ')';
  }
}
