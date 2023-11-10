package com.hindsight.king_of_castrop_rauxel.world;

import static com.hindsight.king_of_castrop_rauxel.cli.CliComponent.*;

import com.hindsight.king_of_castrop_rauxel.action.debug.DebugActionFactory;
import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.graphs.Vertex;
import com.hindsight.king_of_castrop_rauxel.utils.DataServices;
import com.hindsight.king_of_castrop_rauxel.utils.Generators;
import java.util.*;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
public abstract class BaseWorldTest {

  public static final String NEIGHBOUR_OF = "- neighbour of: ";

  @Autowired protected Generators generators;
  @Autowired protected AppProperties appProperties;
  @Autowired protected ChunkHandler chunkHandler;
  @Autowired protected DataServices dataServices;
  @Autowired protected ApplicationContext ctx;
  @Autowired protected World world;
  @Autowired protected Graph map;
  protected DebugActionFactory daf;

  @AfterEach
  void tearDown() {
    map = null;
    chunkHandler = null;
    world = null;
    daf = null;
  }

  protected void debug(List<Vertex> vertices, Graph map) {
    chunkHandler.logDisconnectedVertices(map);
    var connectivityResult = chunkHandler.evaluateConnectivity(map);
    System.out.println("Unvisited vertices: " + connectivityResult.unvisitedVertices().size());
    debugSet(vertices, connectivityResult.unvisitedVertices());
    System.out.println("Visited vertices: " + connectivityResult.visitedVertices().size());
    debugSet(vertices, connectivityResult.visitedVertices());
    try {
      daf.logPlane(world, map);
    } catch (Exception e) {
      System.out.printf(
          FMT.RED_BRIGHT
              + "Error: Could not print plane - this usually happens because 1) the setUp()/tearDown() does not reset all fields correctly or 2) you never call setCurrentChunk().%n"
              + FMT.RESET);
    }
    daf.printConnectivity();
  }

  protected void debugSet(List<Vertex> vertices, Set<Vertex> vertexSet) {
    vertexSet.forEach(
        v -> {
          System.out.println(v.getDto().getSummary());
          v.getEdges().forEach(e -> System.out.println(NEIGHBOUR_OF + e.end().getDto().name()));
          vertices.forEach(
              vOther ->
                  System.out.printf(
                      "- distance to %s: %s%n",
                      vOther.getDto().name(), vOther.getDto().distanceTo(v.getDto())));
        });
  }
}
