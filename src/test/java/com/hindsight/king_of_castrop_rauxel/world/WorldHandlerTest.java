package com.hindsight.king_of_castrop_rauxel.world;

import static com.hindsight.king_of_castrop_rauxel.location.LocationBuilder.randomArea;
import static com.hindsight.king_of_castrop_rauxel.location.LocationBuilder.randomSize;
import static com.hindsight.king_of_castrop_rauxel.world.Coordinates.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

import com.hindsight.king_of_castrop_rauxel.graphs.Vertex;
import com.hindsight.king_of_castrop_rauxel.location.AbstractLocation;
import com.hindsight.king_of_castrop_rauxel.location.LocationBuilder;
import com.hindsight.king_of_castrop_rauxel.location.Settlement;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;

@SpringBootTest
class WorldHandlerTest extends BaseWorldTest {

  protected static final Pair<Integer, Integer> C_1_W_COORDS = Pair.of(0, 0);

  @Override
  @BeforeEach
  void setUp() {
    super.setUp();
    chunk = new Chunk(C_1_W_COORDS, worldHandler, WorldHandler.Strategy.NONE);
    world.place(chunk, C_1_W_COORDS);
  }

  @Test
  void givenNoConnections_whenEvaluatingConnectivity_findOnlyOneVertex() {
    try (var mocked = mockStatic(LocationBuilder.class)) {
      // Given
      locationComponentIsInitialised(mocked);
      chunkWithSettlementsExists();

      // When
      var result = worldHandler.evaluateConnectivity(map);

      // Then
      assertThat(result.unvisitedVertices()).hasSize(3);
      assertThat(result.visitedVertices()).hasSize(1);
    }
  }

  @Test
  void givenSomeConnections_whenEvaluatingConnectivity_findThemAsExpected() {
    try (var mocked = mockStatic(LocationBuilder.class)) {
      // Given
      locationComponentIsInitialised(mocked);
      var vertices = chunkWithSettlementsExists();
      var v1 = vertices.get(0);
      var v2 = vertices.get(1);
      var v3 = vertices.get(2);

      // When
      worldHandler.addConnections(map, v1, v2, v1.getLocation().distanceTo(v2.getLocation()));
      worldHandler.addConnections(map, v2, v3, v2.getLocation().distanceTo(v3.getLocation()));
      var result = worldHandler.evaluateConnectivity(map);

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
    try (var mocked = mockStatic(LocationBuilder.class)) {
      // Given
      locationComponentIsInitialised(mocked);
      var expected1 = Pair.of(317, 45);
      var expected2 = Pair.of(51, 338);
      var expected3 = Pair.of(243, 330);

      // When
      worldHandler.generateSettlements(map, chunk);
      var vertices = map.getVertices();
      var connectivity = worldHandler.evaluateConnectivity(map);

      // Then
      assertEquals(chunk.getDensity(), vertices.size());
      assertEquals(vertices.size() - 1, connectivity.unvisitedVertices().size());
      assertThat(map.getVertexByValue(expected1, CoordType.GLOBAL).getLocation()).isNotNull();
      assertThat(map.getVertexByValue(expected2, CoordType.GLOBAL).getLocation()).isNotNull();
      assertThat(map.getVertexByValue(expected3, CoordType.GLOBAL).getLocation()).isNotNull();
      vertices.forEach(v -> assertThat(v.getLocation().getNeighbours()).isEmpty());
    }
  }

  @Test
  void whenConnectingAnyWithinNeighbourDistance_connectCloseOnesAsExpected() {
    try (var mocked = mockStatic(LocationBuilder.class)) {
      // Given
      locationComponentIsInitialised(mocked);

      // When
      worldHandler.generateSettlements(map, chunk);
      worldHandler.connectAnyWithinNeighbourDistance(map);
      var BAE = map.getVertexByValue(Pair.of(317, 45), CoordType.GLOBAL).getLocation();
      var VAL = map.getVertexByValue(Pair.of(51, 338), CoordType.GLOBAL).getLocation();
      var AEL = map.getVertexByValue(Pair.of(308, 101), CoordType.GLOBAL).getLocation();
      var AST = map.getVertexByValue(Pair.of(356, 238), CoordType.GLOBAL).getLocation();
      var WYR = map.getVertexByValue(Pair.of(292, 40), CoordType.GLOBAL).getLocation();
      var SOL = map.getVertexByValue(Pair.of(231, 332), CoordType.GLOBAL).getLocation();
      var ITH = map.getVertexByValue(Pair.of(243, 330), CoordType.GLOBAL).getLocation();

      // Then
      assertThat(map.getVertices()).hasSize(7);
      assertThat(BAE.getNeighbours()).containsOnly(AEL, WYR);
      assertThat(VAL.getNeighbours()).isEmpty();
      assertThat(AEL.getNeighbours()).containsOnly(BAE, WYR);
      assertThat(AST.getNeighbours()).isEmpty();
      assertThat(WYR.getNeighbours()).containsOnly(BAE, AEL);
      assertThat(SOL.getNeighbours()).containsOnly(ITH);
      assertThat(ITH.getNeighbours()).containsOnly(SOL);
    }
  }

  @Test
  void whenConnectingNeighbourless_addNeighboursButDoNotConnectAll() {
    try (var mocked = mockStatic(LocationBuilder.class)) {
      // Given
      locationComponentIsInitialised(mocked);

      // When
      worldHandler.generateSettlements(map, chunk);
      worldHandler.connectNeighbourlessToClosest(map);
      var result = worldHandler.evaluateConnectivity(map);
      var BAE = map.getVertexByValue(Pair.of(317, 45), CoordType.GLOBAL).getLocation();
      var VAL = map.getVertexByValue(Pair.of(51, 338), CoordType.GLOBAL).getLocation();
      var AEL = map.getVertexByValue(Pair.of(308, 101), CoordType.GLOBAL).getLocation();
      var AST = map.getVertexByValue(Pair.of(356, 238), CoordType.GLOBAL).getLocation();
      var WYR = map.getVertexByValue(Pair.of(292, 40), CoordType.GLOBAL).getLocation();
      var SOL = map.getVertexByValue(Pair.of(231, 332), CoordType.GLOBAL).getLocation();
      var ITH = map.getVertexByValue(Pair.of(243, 330), CoordType.GLOBAL).getLocation();

      // Then
      map.getVertices().forEach(v -> assertThat(v.getLocation().getNeighbours()).isNotEmpty());
      assertThat(BAE.getNeighbours()).containsOnly(WYR, AEL);
      assertThat(AEL.getNeighbours()).containsOnly(BAE, AST);
      assertThat(SOL.getNeighbours()).containsOnly(VAL, ITH);
      assertThat(result.unvisitedVertices()).hasSize(4);
      assertThat(result.visitedVertices()).hasSize(3);
    }
  }

  @Test
  void whenConnectingDisconnected_connectAllAsExpected() {
    try (var mocked = mockStatic(LocationBuilder.class)) {
      // Given
      locationComponentIsInitialised(mocked);

      // When
      worldHandler.generateSettlements(map, chunk);
      worldHandler.connectDisconnectedToClosestConnected(map);
      var connectivity = worldHandler.evaluateConnectivity(map);
      var BAE = map.getVertexByValue(Pair.of(317, 45), CoordType.GLOBAL).getLocation();
      var VAL = map.getVertexByValue(Pair.of(51, 338), CoordType.GLOBAL).getLocation();
      var AEL = map.getVertexByValue(Pair.of(308, 101), CoordType.GLOBAL).getLocation();
      var AST = map.getVertexByValue(Pair.of(356, 238), CoordType.GLOBAL).getLocation();
      var WYR = map.getVertexByValue(Pair.of(292, 40), CoordType.GLOBAL).getLocation();
      var SOL = map.getVertexByValue(Pair.of(231, 332), CoordType.GLOBAL).getLocation();
      var ITH = map.getVertexByValue(Pair.of(243, 330), CoordType.GLOBAL).getLocation();
      debug(map.getVertices(), map);

      // Then
      assertThat(map.getVertices()).hasSize(7);
      assertThat(BAE.getNeighbours()).containsOnly(WYR, SOL, AEL);
      assertThat(VAL.getNeighbours()).containsOnly(SOL);
      assertThat(AEL.getNeighbours()).containsOnly(BAE, AST);
      assertThat(AST.getNeighbours()).containsOnly(AEL);
      assertThat(WYR.getNeighbours()).containsOnly(BAE);
      assertThat(SOL.getNeighbours()).containsOnly(VAL, BAE, ITH);
      assertThat(ITH.getNeighbours()).containsOnly(SOL);
      assertThat(connectivity.visitedVertices()).hasSize(7);
    }
  }

  @Override
  protected void locationComponentIsInitialised(MockedStatic<LocationBuilder> mocked) {
    mocked.when(() -> randomSize(any(Random.class))).thenReturn(AbstractLocation.Size.XS);
    mocked.when(() -> randomArea(any(Random.class), any())).thenReturn(1);
  }

  private List<Vertex<AbstractLocation>> chunkWithSettlementsExists() {
    assertEquals(AbstractLocation.Size.XS, LocationBuilder.randomSize(new Random()));
    assertEquals(1, LocationBuilder.randomArea(new Random(), AbstractLocation.Size.XS));

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
}
