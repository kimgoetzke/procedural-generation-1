package com.hindsight.king_of_castrop_rauxel.world;

import static com.hindsight.king_of_castrop_rauxel.cli.CliComponent.*;
import static com.hindsight.king_of_castrop_rauxel.location.LocationHandler.*;

import com.hindsight.king_of_castrop_rauxel.action.debug.DebugActionFactory;
import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.graphs.Vertex;
import com.hindsight.king_of_castrop_rauxel.location.*;
import com.hindsight.king_of_castrop_rauxel.utils.DataServices;
import com.hindsight.king_of_castrop_rauxel.utils.Generators;
import java.util.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public abstract class BaseWorldTest {

  protected static final Map<Size, SettlementConfig> fakeConfig = new EnumMap<>(Size.class);

  @Autowired protected Generators generators;
  @Autowired protected AppProperties appProperties;
  @Autowired protected LocationFactory locationFactory;
  @Autowired protected WorldHandler worldHandler;
  @Autowired protected DataServices dataServices;

  protected Chunk chunk;
  protected World world;
  protected Graph<AbstractLocation> map;
  protected DebugActionFactory daf;

  @BeforeEach
  void setUp() {
    fakeConfig.put(Size.XS, appProperties.getSettlementProperties().xs());
    fakeConfig.put(Size.S, appProperties.getSettlementProperties().s());
    fakeConfig.put(Size.M, appProperties.getSettlementProperties().m());
    fakeConfig.put(Size.L, appProperties.getSettlementProperties().l());
    fakeConfig.put(Size.XL, appProperties.getSettlementProperties().xl());
  }

  @AfterEach
  void tearDown() {
    map = null;
    worldHandler = null;
    world = null;
    daf = null;
  }

  protected abstract void locationComponentIsInitialised(MockedStatic<LocationHandler> mocked);

  protected <T extends AbstractLocation> void debug(
      List<Vertex<T>> vertices, Graph<AbstractLocation> map) {
    worldHandler.logDisconnectedVertices(map);
    var connectivityResult = worldHandler.evaluateConnectivity(map);
    System.out.println("Unvisited vertices: " + connectivityResult.unvisitedVertices().size());
    debugSet(vertices, connectivityResult.unvisitedVertices());
    System.out.println("Visited vertices: " + connectivityResult.visitedVertices().size());
    debugSet(vertices, connectivityResult.visitedVertices());
    try {
      daf.printPlane(world, map);
    } catch (Exception e) {
      System.out.printf(
          FMT.RED_BRIGHT
              + "Error: Could not print plane - this happens usually because 1) the setUp()/tearDown() does not reset all fields correctly or 2) you never call setCurrentChunk().%n"
              + FMT.RESET);
    }
    daf.printConnectivity();
  }

  protected <T extends AbstractLocation> void debugSet(
      List<Vertex<T>> vertices, Set<Vertex<AbstractLocation>> vertexSet) {
    vertexSet.forEach(
        v -> {
          System.out.println(v.getLocation().getBriefSummary());
          v.getLocation()
              .getNeighbours()
              .forEach(n -> System.out.println("- neighbour of: " + n.getName()));
          vertices.forEach(
              vOther ->
                  System.out.printf(
                      "- distance to %s: %s%n",
                      vOther.getLocation().getName(),
                      vOther.getLocation().distanceTo(v.getLocation())));
        });
  }
}
