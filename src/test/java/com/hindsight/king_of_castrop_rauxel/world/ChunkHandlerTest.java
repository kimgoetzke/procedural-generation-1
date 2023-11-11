package com.hindsight.king_of_castrop_rauxel.world;

import static com.hindsight.king_of_castrop_rauxel.world.Coordinates.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.hindsight.king_of_castrop_rauxel.action.debug.DebugActionFactory;
import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.graphs.Vertex;
import com.hindsight.king_of_castrop_rauxel.location.Settlement;
import com.hindsight.king_of_castrop_rauxel.location.Shop;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;

@SpringBootTest
class ChunkHandlerTest extends BaseWorldTest {

  protected static final Pair<Integer, Integer> C_1_W_COORDS = Pair.of(0, 0);

  private Chunk chunk;

  @BeforeEach
  void setUp() {
    SeedBuilder.changeSeed(123L);
    map = new Graph(true);
    daf = new DebugActionFactory(map, world, chunkHandler, appProperties);
    chunk = ctx.getBean(Chunk.class, C_1_W_COORDS, chunkHandler, Chunk.Strategy.NONE);
    world.place(chunk, C_1_W_COORDS);
    chunk.load();
  }

  @ParameterizedTest
  @MethodSource("worldCoordsToTargetLevel")
  void whenGeneratingChunks_initialiseGeneratorAndSetTargetLevel(
      Pair<Integer, Integer> coords, int expectedTargetLevel) {
    System.out.println("map.getVertices().size() = " + map.getVertices().size());
    // When
    world.generateChunk(coords, map);

    // Then
    assertThat(world.getChunk(coords).getTargetLevel()).isEqualTo(expectedTargetLevel);
  }

  private static Stream<Arguments> worldCoordsToTargetLevel() {
    return Stream.of(
        arguments(Pair.of(25, 25), 1),
        arguments(Pair.of(25, 27), 2),
        arguments(Pair.of(28, 25), 3),
        arguments(Pair.of(29, 25), 4),
        arguments(Pair.of(30, 30), 10));
  }

  @Test
  void whenGeneratingChunk_initialiseShopsWithItemsOfCorrectTier() {
    // Given
    var vertices = chunkWithSettlementsExists();
    var shops = new ArrayList<Shop>();

    // When
    for (var vert : vertices) {
      var coords = vert.getDto().coordinates();
      var location = chunk.getLoadedLocation(coords);
      location.getPointsOfInterest().stream()
          .filter(p -> p instanceof Shop)
          .forEach(p -> shops.add((Shop) p));
    }
    var tier6Items = shops.get(0).getItems().stream().filter(i -> i.getTier() == 6).toList();
    var prohibitedItems = shops.get(0).getItems().stream().filter(i -> i.getTier() != 6).toList();

    // Then
    assertThat(shops).isNotNull();
    assertThat(tier6Items).isNotEmpty();
    assertThat(prohibitedItems).isEmpty();
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
    chunkHandler.addConnections(v1, v2, v1.getDto().distanceTo(v2.getDto()));
    chunkHandler.addConnections(v2, v3, v2.getDto().distanceTo(v3.getDto()));
    var result = chunkHandler.evaluateConnectivity(map);

    // Then
    assertThat(result.unvisitedVertices()).hasSize(1);
    assertThat(result.unvisitedVertices()).contains(vertices.get(3));
    assertThat(result.visitedVertices()).hasSize(3);
    assertThat(v1.getNeighbours()).hasSize(1);
    assertThat(v2.getNeighbours()).hasSize(2);
    assertThat(v3.getNeighbours()).hasSize(1);
  }

  @Test
  void whenGeneratingSettlements_createThemPredictablyAndDoNotConnectThem() {
    // Given
    var expected1 = Pair.of(317, 45);
    var expected2 = Pair.of(51, 338);
    var expected3 = Pair.of(356, 238);

    // When
    chunkHandler.generateSettlements(chunk);
    var vertices = map.getVertices();
    var connectivity = chunkHandler.evaluateConnectivity(map);

    // Then
    assertEquals(chunk.getDensity(), vertices.size());
    assertEquals(vertices.size() - 1, connectivity.unvisitedVertices().size());
    assertThat(map.getVertex(expected1, CoordType.GLOBAL).getDto()).isNotNull();
    assertThat(map.getVertex(expected2, CoordType.GLOBAL).getDto()).isNotNull();
    assertThat(map.getVertex(expected3, CoordType.GLOBAL).getDto()).isNotNull();
    vertices.forEach(v -> assertThat(v.getNeighbours()).isEmpty());
  }

  @Test
  void whenConnectingAnyWithinNeighbourDistance_connectCloseOnesAsExpected() {
    // When
    chunkHandler.generateSettlements(chunk);
    chunkHandler.connectAnyWithinNeighbourDistance();
    var BAE = map.getVertex(Pair.of(317, 45), CoordType.GLOBAL);
    var VAL = map.getVertex(Pair.of(51, 338), CoordType.GLOBAL);
    var AEL = map.getVertex(Pair.of(308, 101), CoordType.GLOBAL);
    var AST = map.getVertex(Pair.of(356, 238), CoordType.GLOBAL);
    var THE = map.getVertex(Pair.of(220, 61), CoordType.GLOBAL);
    var MYS = map.getVertex(Pair.of(191, 399), CoordType.GLOBAL);
    var EBR = map.getVertex(Pair.of(84, 468), CoordType.GLOBAL);

    // Then
    assertThat(map.getVertices()).hasSize(7);
    assertThat(VAL.getNeighbours()).isEmpty();
    assertThat(AST.getNeighbours()).isEmpty();
    assertThat(MYS.getNeighbours()).containsOnly(EBR.getDto());
    assertThat(EBR.getNeighbours()).containsOnly(MYS.getDto());
    assertThat(BAE.getNeighbours()).containsOnly(AEL.getDto(), THE.getDto());
    assertThat(AEL.getNeighbours()).containsOnly(BAE.getDto(), THE.getDto());
    assertThat(THE.getNeighbours()).containsOnly(BAE.getDto(), AEL.getDto());
  }

  @Test
  void whenConnectingNeighbourless_addNeighboursButDoNotConnectAll() {
    // When
    chunkHandler.generateSettlements(chunk);
    chunkHandler.connectNeighbourlessToClosest();
    var result = chunkHandler.evaluateConnectivity(map);
    var BAE = map.getVertex(Pair.of(317, 45), CoordType.GLOBAL);
    var VAL = map.getVertex(Pair.of(51, 338), CoordType.GLOBAL);
    var AEL = map.getVertex(Pair.of(308, 101), CoordType.GLOBAL);
    var AST = map.getVertex(Pair.of(356, 238), CoordType.GLOBAL);
    var THE = map.getVertex(Pair.of(220, 61), CoordType.GLOBAL);
    var MYS = map.getVertex(Pair.of(191, 399), CoordType.GLOBAL);
    var EBR = map.getVertex(Pair.of(84, 468), CoordType.GLOBAL);
    debug(map.getVertices(), map);

    // Then
    map.getVertices().forEach(v -> assertThat(v.getNeighbours()).isNotEmpty());
    assertThat(VAL.getNeighbours()).containsOnly(EBR.getDto());
    assertThat(MYS.getNeighbours()).containsOnly(EBR.getDto());
    assertThat(EBR.getNeighbours()).containsOnly(VAL.getDto(), MYS.getDto());
    assertThat(BAE.getNeighbours()).containsOnly(AEL.getDto());
    assertThat(AEL.getNeighbours()).containsOnly(THE.getDto(), BAE.getDto(), AST.getDto());
    assertThat(THE.getNeighbours()).containsOnly(AEL.getDto());
    assertThat(result.unvisitedVertices()).hasSize(3);
    assertThat(result.visitedVertices()).hasSize(4);
  }

  @Test
  void whenConnectingDisconnected_connectAllAsExpected() {

    // When
    chunkHandler.generateSettlements(chunk);
    chunkHandler.connectDisconnectedToClosestConnected();
    var connectivity = chunkHandler.evaluateConnectivity(map);
    var BAE = map.getVertex(Pair.of(317, 45), CoordType.GLOBAL);
    var VAL = map.getVertex(Pair.of(51, 338), CoordType.GLOBAL);
    var AEL = map.getVertex(Pair.of(308, 101), CoordType.GLOBAL);
    var AST = map.getVertex(Pair.of(356, 238), CoordType.GLOBAL);
    var THE = map.getVertex(Pair.of(220, 61), CoordType.GLOBAL);
    var MYS = map.getVertex(Pair.of(191, 399), CoordType.GLOBAL);
    var EBR = map.getVertex(Pair.of(84, 468), CoordType.GLOBAL);

    // Then
    assertThat(map.getVertices()).hasSize(7);
    assertThat(BAE.getNeighbours()).containsOnly(VAL.getDto(), AEL.getDto());
    assertThat(VAL.getNeighbours()).containsOnly(MYS.getDto(), BAE.getDto());
    assertThat(MYS.getNeighbours()).containsOnly(VAL.getDto(), EBR.getDto());
    assertThat(EBR.getNeighbours()).containsOnly(MYS.getDto());
    assertThat(AEL.getNeighbours()).containsOnly(THE.getDto(), BAE.getDto(), AST.getDto());
    assertThat(AST.getNeighbours()).containsOnly(AEL.getDto());
    assertThat(THE.getNeighbours()).containsOnly(AEL.getDto());
    assertThat(connectivity.visitedVertices()).hasSize(7);
  }

  private List<Vertex> chunkWithSettlementsExists() {
    var v1 = map.addVertex(createSettlement(Pair.of(0, 0)));
    var v2 = map.addVertex(createSettlement(Pair.of(20, 20)));
    var v3 = map.addVertex(createSettlement(Pair.of(100, 100)));
    var v4 = map.addVertex(createSettlement(Pair.of(500, 500)));

    chunk.place(v1.getDto());
    chunk.place(v2.getDto());
    chunk.place(v3.getDto());
    chunk.place(v4.getDto());

    return List.of(v1, v2, v3, v4);
  }

  private Settlement createSettlement(Pair<Integer, Integer> chunkCoords) {
    return new Settlement(C_1_W_COORDS, chunkCoords, generators, dataServices, appProperties);
  }
}
