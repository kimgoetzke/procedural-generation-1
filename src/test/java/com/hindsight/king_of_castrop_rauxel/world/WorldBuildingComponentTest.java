package com.hindsight.king_of_castrop_rauxel.world;

import static com.hindsight.king_of_castrop_rauxel.world.Coordinates.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

import com.hindsight.king_of_castrop_rauxel.action.DebugActionFactory;
import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.graphs.Vertex;
import com.hindsight.king_of_castrop_rauxel.location.AbstractLocation;
import com.hindsight.king_of_castrop_rauxel.location.LocationComponent;
import com.hindsight.king_of_castrop_rauxel.location.Settlement;
import com.hindsight.king_of_castrop_rauxel.utils.StringGenerator;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;

@SpringBootTest
class WorldBuildingComponentTest extends WorldBuildingComponent {

  public static final Pair<Integer, Integer> C_1_W_COORDS = Pair.of(0, 0);

  @Autowired private StringGenerator stringGenerator;
  @Autowired private AppProperties appProperties;

  private Chunk chunk;
  private World world;
  private Graph<AbstractLocation> map;
  private DebugActionFactory debug;

  @BeforeEach
  void setUp() {
    SeedComponent.changeSeed(123L);
    world = new World(appProperties);
    chunk = new Chunk(C_1_W_COORDS);
    map = new Graph<>(true);
    debug = new DebugActionFactory(map, world);
    world.place(chunk, C_1_W_COORDS);
  }

  @Test
  void givenNoConnections_whenEvaluatingConnectivity_findOnlyOneVertex() {
    try (var mocked = mockStatic(LocationComponent.class)) {
      // Given
      locationComponentIsInitialised(mocked);
      chunkWithSettlementsExists();

      // When
      var result = evaluateConnectivity(map);

      // Then
      assertThat(result.unvisitedVertices()).hasSize(3);
      assertThat(result.visitedVertices()).hasSize(1);
    }
  }

  @Test
  void givenSomeConnections_whenEvaluatingConnectivity_findThemAsExpected() {
    try (var mocked = mockStatic(LocationComponent.class)) {
      // Given
      locationComponentIsInitialised(mocked);
      var vertices = chunkWithSettlementsExists();
      var v1 = vertices.get(0);
      var v2 = vertices.get(1);
      var v3 = vertices.get(2);

      // When
      addConnections(map, v1, v2, v1.getLocation().distanceTo(v2.getLocation()));
      addConnections(map, v2, v3, v2.getLocation().distanceTo(v3.getLocation()));
      var result = evaluateConnectivity(map);

      // Then
      assertThat(result.unvisitedVertices()).hasSize(1);
      assertThat(result.unvisitedVertices()).contains(vertices.get(3));
      assertThat(result.visitedVertices()).hasSize(3);
      assertThat(v1.getLocation().getNeighbours()).hasSize(1);
      assertThat(v2.getLocation().getNeighbours()).hasSize(2);
      assertThat(v3.getLocation().getNeighbours()).hasSize(1);
    }
  }

  @Test
  void whenGeneratingSettlements_createThemPredictablyAndDoNotConnectThem() {
    try (var mocked = mockStatic(LocationComponent.class)) {
      // Given
      locationComponentIsInitialised(mocked);
      var expected1 = Pair.of(29, 293);
      var expected2 = Pair.of(125, 81);
      var expected3 = Pair.of(212, 276);
      var vertices = map.getVertices();

      // When
      generateSettlements(map, chunk, stringGenerator);

      // Then
      assertEquals(chunk.getDensity(), vertices.size());
      assertEquals(vertices.size() - 1, evaluateConnectivity(map).unvisitedVertices().size());
      assertThat(map.getVertexByValue(expected1, CoordType.GLOBAL).getLocation()).isNotNull();
      assertThat(map.getVertexByValue(expected2, CoordType.GLOBAL).getLocation()).isNotNull();
      assertThat(map.getVertexByValue(expected3, CoordType.GLOBAL).getLocation()).isNotNull();
    }
  }

  @Test
  void whenConnectingAnyWithinNeighbourDistance_connectThemAsExpected() {
    try (var mocked = mockStatic(LocationComponent.class)) {
      // Given
      locationComponentIsInitialised(mocked);

      // When
      generateSettlements(map, chunk, stringGenerator);
      connectAnyWithinNeighbourDistance(map);
      var VER = map.getVertexByValue(Pair.of(212, 276), CoordType.GLOBAL).getLocation();
      var SYR = map.getVertexByValue(Pair.of(169, 311), CoordType.GLOBAL).getLocation();
      var KYN = map.getVertexByValue(Pair.of(125, 81), CoordType.GLOBAL).getLocation();
      var TYR = map.getVertexByValue(Pair.of(232, 275), CoordType.GLOBAL).getLocation();
      var DYN = map.getVertexByValue(Pair.of(216, 206), CoordType.GLOBAL).getLocation();
      var MYS = map.getVertexByValue(Pair.of(245, 373), CoordType.GLOBAL).getLocation();
      var VAL = map.getVertexByValue(Pair.of(29, 293), CoordType.GLOBAL).getLocation();

      // Then
      assertThat(map.getVertices()).hasSize(7);
      assertThat(KYN.getNeighbours()).isEmpty();
      assertThat(VER.getNeighbours()).containsOnly(SYR, TYR, DYN);
      assertThat(SYR.getNeighbours()).containsOnly(VER, TYR, MYS);
      assertThat(TYR.getNeighbours()).containsOnly(SYR, VER, DYN, MYS);
      assertThat(DYN.getNeighbours()).containsOnly(VER, TYR);
      assertThat(MYS.getNeighbours()).containsOnly(TYR, SYR);
      assertThat(VAL.getNeighbours()).isEmpty();
    }
  }

  @Test
  void whenConnectingDisconnected_connectThemAsExpected() {
    try (var mocked = mockStatic(LocationComponent.class)) {
      // Given
      locationComponentIsInitialised(mocked);

      // When
      generateSettlements(map, chunk, stringGenerator);
      connectDisconnectedToClosestConnected(map);
      var VER = map.getVertexByValue(Pair.of(212, 276), CoordType.GLOBAL).getLocation();
      var SYR = map.getVertexByValue(Pair.of(169, 311), CoordType.GLOBAL).getLocation();
      var KYN = map.getVertexByValue(Pair.of(125, 81), CoordType.GLOBAL).getLocation();
      var TYR = map.getVertexByValue(Pair.of(232, 275), CoordType.GLOBAL).getLocation();
      var DYN = map.getVertexByValue(Pair.of(216, 206), CoordType.GLOBAL).getLocation();
      var MYS = map.getVertexByValue(Pair.of(245, 373), CoordType.GLOBAL).getLocation();
      var VAL = map.getVertexByValue(Pair.of(29, 293), CoordType.GLOBAL).getLocation();

      // Then
      assertThat(map.getVertices()).hasSize(7);
      assertThat(KYN.getNeighbours()).containsOnly(VAL);
      assertThat(VER.getNeighbours()).containsOnly(VAL, DYN, TYR, SYR);
      assertThat(SYR.getNeighbours()).containsOnly(VER, MYS);
      assertThat(TYR.getNeighbours()).containsOnly(VER);
      assertThat(DYN.getNeighbours()).containsOnly(VER);
      assertThat(MYS.getNeighbours()).containsOnly(SYR);
      assertThat(VAL.getNeighbours()).containsOnly(KYN, VER);
    }
  }

  private void debug(List<Vertex<AbstractLocation>> vertices) {
    logDisconnectedVertices(map);
    var connectivityResult = evaluateConnectivity(map);
    System.out.println("Unvisited vertices: " + connectivityResult.unvisitedVertices().size());
    connectivityResult
        .unvisitedVertices()
        .forEach(
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
    System.out.println("Visited vertices: " + connectivityResult.visitedVertices().size());
    connectivityResult
        .visitedVertices()
        .forEach(
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

    debug.printPlane(world, map);
    debug.printConnectivity();
  }

  private List<Vertex<AbstractLocation>> chunkWithSettlementsExists() {
    assertEquals(AbstractLocation.Size.XS, LocationComponent.randomSize(new Random()));
    assertEquals(1, LocationComponent.randomArea(new Random(), AbstractLocation.Size.XS));

    var v1 = map.addVertex(new Settlement(C_1_W_COORDS, Pair.of(0, 0), stringGenerator));
    var v2 = map.addVertex(new Settlement(C_1_W_COORDS, Pair.of(20, 20), stringGenerator));
    var v3 = map.addVertex(new Settlement(C_1_W_COORDS, Pair.of(100, 100), stringGenerator));
    var v4 = map.addVertex(new Settlement(C_1_W_COORDS, Pair.of(500, 500), stringGenerator));

    chunk.place(v1.getLocation().getCoordinates().getChunk(), Chunk.LocationType.SETTLEMENT);
    chunk.place(v2.getLocation().getCoordinates().getChunk(), Chunk.LocationType.SETTLEMENT);
    chunk.place(v3.getLocation().getCoordinates().getChunk(), Chunk.LocationType.SETTLEMENT);
    chunk.place(v4.getLocation().getCoordinates().getChunk(), Chunk.LocationType.SETTLEMENT);

    return List.of(v1, v2, v3, v4);
  }

  private static void locationComponentIsInitialised(MockedStatic<LocationComponent> mocked) {
    mocked
        .when(() -> LocationComponent.randomSize(any(Random.class)))
        .thenReturn(AbstractLocation.Size.XS);
    mocked.when(() -> LocationComponent.randomArea(any(Random.class), any())).thenReturn(1);
  }
}
