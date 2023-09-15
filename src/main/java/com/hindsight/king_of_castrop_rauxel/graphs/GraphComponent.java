package com.hindsight.king_of_castrop_rauxel.graphs;

import com.hindsight.king_of_castrop_rauxel.location.AbstractLocation;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GraphComponent {

  public static ConnectivityResult getDisconnectedVertices(Graph<AbstractLocation> graph) {
    Set<Vertex<AbstractLocation>> visitedVertices = new HashSet<>();
    Set<Vertex<AbstractLocation>> unvisitedVertices = new HashSet<>(graph.getVertices());

    if (!unvisitedVertices.isEmpty()) {
      Vertex<AbstractLocation> startVertex = unvisitedVertices.iterator().next();
      traverseDepthFirst(startVertex, visitedVertices, unvisitedVertices);
    }
    return new ConnectivityResult(visitedVertices, unvisitedVertices);
  }

  public static void traverseDepthFirst(
      Vertex<AbstractLocation> currentVertex,
      Set<Vertex<AbstractLocation>> visitedVertices,
      Set<Vertex<AbstractLocation>> unvisitedVertices) {
    visitedVertices.add(currentVertex);
    unvisitedVertices.remove(currentVertex);

    for (Edge<AbstractLocation> edge : currentVertex.getEdges()) {
      var end = edge.end();
      if (!visitedVertices.contains(end)) {
        traverseDepthFirst(end, visitedVertices, unvisitedVertices);
      }
    }
  }

  public record ConnectivityResult(
      @Getter Set<Vertex<AbstractLocation>> visitedVertices,
      @Getter Set<Vertex<AbstractLocation>> unvisitedVertices) {}
}
