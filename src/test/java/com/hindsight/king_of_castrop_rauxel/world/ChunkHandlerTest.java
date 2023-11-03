package com.hindsight.king_of_castrop_rauxel.world;

import static com.hindsight.king_of_castrop_rauxel.world.Coordinates.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hindsight.king_of_castrop_rauxel.action.debug.DebugActionFactory;
import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.graphs.Vertex;
import com.hindsight.king_of_castrop_rauxel.location.AbstractLocation;
import java.util.List;

import com.hindsight.king_of_castrop_rauxel.location.Settlement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;

@SpringBootTest
class ChunkHandlerTest extends BaseWorldTest {

  protected static final Pair<Integer, Integer> C_1_W_COORDS = Pair.of(0, 0);

  @BeforeEach
  void setUp() {
    SeedBuilder.changeSeed(123L);
    world = new World(appProperties, chunkHandler);
    map = new Graph<>(true);
    daf = new DebugActionFactory(map, world, chunkHandler, appProperties);
    chunk = new Chunk(C_1_W_COORDS, chunkHandler, Chunk.Strategy.NONE);
    world.place(chunk, C_1_W_COORDS);
  }

  @Test
  void givenNoConnections_whenEvaluatingConnectivity_findOnlyOneVertex() {
    // Given
    chunkWithSettlementsExists();

    // When
    var result = chunkHandler.evaluateConnectivity(map);

    // Then
    assertThat(result.unvisitedVertices()).hasSize(3);
    assertThat(result.visitedVertices()).hasSize(1);
  }

  @Test
  void givenSomeConnections_whenEvaluatingConnectivity_findThemAsExpected() {
    // Given
    var vertices = chunkWithSettlementsExists();
    var v1 = vertices.get(0);
    var v2 = vertices.get(1);
    var v3 = vertices.get(2);

    // When
    chunkHandler.addConnections(map, v1, v2, v1.getLocation().distanceTo(v2.getLocation()));
    chunkHandler.addConnections(map, v2, v3, v2.getLocation().distanceTo(v3.getLocation()));
    var result = chunkHandler.evaluateConnectivity(map);

    // Then
    assertThat(result.unvisitedVertices()).hasSize(1);
    assertThat(result.unvisitedVertices()).contains(vertices.get(3));
    assertThat(result.visitedVertices()).hasSize(3);
    assertThat(v1.getLocation().getNeighbours()).hasSize(1);
    assertThat(v2.getLocation().getNeighbours()).hasSize(2);
    assertThat(v3.getLocation().getNeighbours()).hasSize(1);
  }

  @Test
  void whenGeneratingSettlements_createThemPredictablyAndDoNotConnectThem() {
    // Given
    var expected1 = Pair.of(317, 45);
    var expected2 = Pair.of(51, 338);
    var expected3 = Pair.of(356, 238);

    // When
    chunkHandler.generateSettlements(map, chunk);
    var vertices = map.getVertices();
    var connectivity = chunkHandler.evaluateConnectivity(map);

    // Then
    assertEquals(chunk.getDensity(), vertices.size());
    assertEquals(vertices.size() - 1, connectivity.unvisitedVertices().size());
    assertThat(map.getVertexByValue(expected1, CoordType.GLOBAL).getLocation()).isNotNull();
    assertThat(map.getVertexByValue(expected2, CoordType.GLOBAL).getLocation()).isNotNull();
    assertThat(map.getVertexByValue(expected3, CoordType.GLOBAL).getLocation()).isNotNull();
    vertices.forEach(v -> assertThat(v.getLocation().getNeighbours()).isEmpty());
  }

  @Test
  void whenConnectingAnyWithinNeighbourDistance_connectCloseOnesAsExpected() {
    // When
    chunkHandler.generateSettlements(map, chunk);
    chunkHandler.connectAnyWithinNeighbourDistance(map);
    var BAE = map.getVertexByValue(Pair.of(317, 45), CoordType.GLOBAL).getLocation();
    var VAL = map.getVertexByValue(Pair.of(51, 338), CoordType.GLOBAL).getLocation();
    var AEL = map.getVertexByValue(Pair.of(308, 101), CoordType.GLOBAL).getLocation();
    var AST = map.getVertexByValue(Pair.of(356, 238), CoordType.GLOBAL).getLocation();
    var THE = map.getVertexByValue(Pair.of(220, 61), CoordType.GLOBAL).getLocation();
    var MYS = map.getVertexByValue(Pair.of(191, 399), CoordType.GLOBAL).getLocation();
    var EBR = map.getVertexByValue(Pair.of(84, 468), CoordType.GLOBAL).getLocation();

    // Then
    assertThat(map.getVertices()).hasSize(7);
    assertThat(VAL.getNeighbours()).isEmpty();
    assertThat(AST.getNeighbours()).isEmpty();
    assertThat(MYS.getNeighbours()).containsOnly(EBR);
    assertThat(EBR.getNeighbours()).containsOnly(MYS);
    assertThat(BAE.getNeighbours()).containsOnly(AEL, THE);
    assertThat(AEL.getNeighbours()).containsOnly(BAE, THE);
    assertThat(THE.getNeighbours()).containsOnly(BAE, AEL);
  }

  @Test
  void whenConnectingNeighbourless_addNeighboursButDoNotConnectAll() {
    // When
    chunkHandler.generateSettlements(map, chunk);
    chunkHandler.connectNeighbourlessToClosest(map);
    var result = chunkHandler.evaluateConnectivity(map);
    var BAE = map.getVertexByValue(Pair.of(317, 45), CoordType.GLOBAL).getLocation();
    var VAL = map.getVertexByValue(Pair.of(51, 338), CoordType.GLOBAL).getLocation();
    var AEL = map.getVertexByValue(Pair.of(308, 101), CoordType.GLOBAL).getLocation();
    var AST = map.getVertexByValue(Pair.of(356, 238), CoordType.GLOBAL).getLocation();
    var THE = map.getVertexByValue(Pair.of(220, 61), CoordType.GLOBAL).getLocation();
    var MYS = map.getVertexByValue(Pair.of(191, 399), CoordType.GLOBAL).getLocation();
    var EBR = map.getVertexByValue(Pair.of(84, 468), CoordType.GLOBAL).getLocation();

    // Then
    map.getVertices().forEach(v -> assertThat(v.getLocation().getNeighbours()).isNotEmpty());
    assertThat(VAL.getNeighbours()).containsOnly(EBR);
    assertThat(MYS.getNeighbours()).containsOnly(EBR);
    assertThat(EBR.getNeighbours()).containsOnly(VAL, MYS);
    assertThat(BAE.getNeighbours()).containsOnly(AEL);
    assertThat(AEL.getNeighbours()).containsOnly(THE, BAE, AST);
    assertThat(THE.getNeighbours()).containsOnly(AEL);
    assertThat(result.unvisitedVertices()).hasSize(3);
    assertThat(result.visitedVertices()).hasSize(4);
  }

  @Test
  void whenConnectingDisconnected_connectAllAsExpected() {

    // When
    chunkHandler.generateSettlements(map, chunk);
    chunkHandler.connectDisconnectedToClosestConnected(map);
    var connectivity = chunkHandler.evaluateConnectivity(map);
    var BAE = map.getVertexByValue(Pair.of(317, 45), CoordType.GLOBAL).getLocation();
    var VAL = map.getVertexByValue(Pair.of(51, 338), CoordType.GLOBAL).getLocation();
    var AEL = map.getVertexByValue(Pair.of(308, 101), CoordType.GLOBAL).getLocation();
    var AST = map.getVertexByValue(Pair.of(356, 238), CoordType.GLOBAL).getLocation();
    var THE = map.getVertexByValue(Pair.of(220, 61), CoordType.GLOBAL).getLocation();
    var MYS = map.getVertexByValue(Pair.of(191, 399), CoordType.GLOBAL).getLocation();
    var EBR = map.getVertexByValue(Pair.of(84, 468), CoordType.GLOBAL).getLocation();

    // Then
    assertThat(map.getVertices()).hasSize(7);
    assertThat(BAE.getNeighbours()).containsOnly(VAL, AEL);
    assertThat(VAL.getNeighbours()).containsOnly(MYS, BAE);
    assertThat(MYS.getNeighbours()).containsOnly(VAL, EBR);
    assertThat(EBR.getNeighbours()).containsOnly(MYS);
    assertThat(AEL.getNeighbours()).containsOnly(THE, BAE, AST);
    assertThat(AST.getNeighbours()).containsOnly(AEL);
    assertThat(THE.getNeighbours()).containsOnly(AEL);
    assertThat(connectivity.visitedVertices()).hasSize(7);
  }

  private List<Vertex<AbstractLocation>> chunkWithSettlementsExists() {
    var v1 = map.addVertex(createSettlement(Pair.of(0, 0)));
    var v2 = map.addVertex(createSettlement(Pair.of(20, 20)));
    var v3 = map.addVertex(createSettlement(Pair.of(100, 100)));
    var v4 = map.addVertex(createSettlement(Pair.of(500, 500)));

    chunk.place(v1.getLocation().getCoordinates().getChunk(), Chunk.LocationType.SETTLEMENT);
    chunk.place(v2.getLocation().getCoordinates().getChunk(), Chunk.LocationType.SETTLEMENT);
    chunk.place(v3.getLocation().getCoordinates().getChunk(), Chunk.LocationType.SETTLEMENT);
    chunk.place(v4.getLocation().getCoordinates().getChunk(), Chunk.LocationType.SETTLEMENT);

    return List.of(v1, v2, v3, v4);
  }

  private Settlement createSettlement(Pair<Integer, Integer> chunkCoords) {
    return new Settlement(
        ChunkHandlerTest.C_1_W_COORDS, chunkCoords, generators, dataServices, appProperties);
  }
}
