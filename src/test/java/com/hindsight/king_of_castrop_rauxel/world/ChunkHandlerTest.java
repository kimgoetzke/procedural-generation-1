package com.hindsight.king_of_castrop_rauxel.world;

import static com.hindsight.king_of_castrop_rauxel.world.Coordinates.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.hindsight.king_of_castrop_rauxel.action.debug.DebugActionFactory;
import com.hindsight.king_of_castrop_rauxel.graph.Graph;
import com.hindsight.king_of_castrop_rauxel.graph.Vertex;
import com.hindsight.king_of_castrop_rauxel.location.Settlement;
import com.hindsight.king_of_castrop_rauxel.location.Shop;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;

@SpringBootTest
class ChunkHandlerTest extends BaseWorldTest {

  private static final Pair<Integer, Integer> C_1_W_COORDS = Pair.of(0, 0);

  private Chunk chunk;

  @BeforeEach
  void setUp() {
    graph = ctx.getBean(Graph.class);
    daf = new DebugActionFactory(graph, world, chunkHandler, appProperties, environmentResolver);
    chunk = ctx.getBean(Chunk.class, C_1_W_COORDS, chunkHandler, Chunk.Strategy.DO_NOTHING);
    world.placeChunk(chunk, C_1_W_COORDS);
    chunk.load();
  }

  @AfterEach
  void tearDown() {
    super.tearDown();
    chunk.unload();
    chunk = null;
  }

  @ParameterizedTest
  @MethodSource("worldCoordsToTargetLevel")
  void whenGeneratingChunks_initialiseGeneratorAndSetTargetLevel(
      Pair<Integer, Integer> coords, int expectedTargetLevel) {
    // When
    world.setCurrentChunk(coords);

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

  @ParameterizedTest
  @MethodSource("worldCoords")
  void whenSettingCurrentChunk_ensureAllLocationsAreConnected(Pair<Integer, Integer> coords) {
    // When
    world.setCurrentChunk(coords);
    var locationsWithoutNeighbours =
        world.getChunk(coords).getLocations().stream()
            .filter(l -> l.getNeighbours().isEmpty())
            .toList();

    // Then
    assertThat(locationsWithoutNeighbours).isEmpty();
  }

  private static Stream<Pair<Integer, Integer>> worldCoords() {
    return Stream.of(Pair.of(25, 25), Pair.of(1, 1), Pair.of(49, 49));
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
    var result = chunkHandler.evaluateConnectivity(graph);

    // Then
    assertThat(result.unvisitedVertices()).hasSize(3);
    assertThat(result.visitedVertices()).hasSize(1);
  }

  @Test
  void givenSomeConnections_whenEvaluatingConnectivity_findThemAsExpected() {
    // Given
    var vertices = chunkWithSettlementsExists();
    var v1 = vertices.get(1);
    var v2 = vertices.get(2);
    var v3 = vertices.get(3);

    // When
    chunkHandler.addConnections(v1, v2, v1.getDto().distanceTo(v2.getDto()));
    chunkHandler.addConnections(v2, v3, v2.getDto().distanceTo(v3.getDto()));
    var result = chunkHandler.evaluateConnectivity(graph);

    // Then
    assertThat(result.unvisitedVertices()).hasSize(1);
    assertThat(result.unvisitedVertices()).contains(vertices.get(0));
    assertThat(result.visitedVertices()).hasSize(3);
    assertThat(v1.getNeighbours()).hasSize(1);
    assertThat(v2.getNeighbours()).hasSize(2);
    assertThat(v3.getNeighbours()).hasSize(1);
  }

  @Test
  void whenGeneratingSettlements_createThemPredictablyAndDoNotConnectThem() {
    // Given
    var expected1 = Pair.of(144, 64);
    var expected2 = Pair.of(496, 413);
    var expected3 = Pair.of(293, 341);

    // When
    chunkHandler.generateSettlements(chunk);
    var vertices = graph.getVertices();
    var connectivity = chunkHandler.evaluateConnectivity(graph);

    // Then
    assertEquals(chunk.getDensity(), vertices.size());
    assertEquals(vertices.size() - 1, connectivity.unvisitedVertices().size());
    assertThat(graph.getVertex(expected1, CoordType.GLOBAL).getDto()).isNotNull();
    assertThat(graph.getVertex(expected2, CoordType.GLOBAL).getDto()).isNotNull();
    assertThat(graph.getVertex(expected3, CoordType.GLOBAL).getDto()).isNotNull();
    vertices.forEach(v -> assertThat(v.getNeighbours()).isEmpty());
  }

  @Test
  void whenConnectingAnyWithinNeighbourDistance_connectCloseOnesAsExpected() {
    // When
    chunkHandler.generateSettlements(chunk);
    chunkHandler.connectAnyWithinNeighbourDistance();
    var HEL = graph.getVertex(Pair.of(144, 64), CoordType.GLOBAL);
    var THY = graph.getVertex(Pair.of(496, 413), CoordType.GLOBAL);
    var BRY = graph.getVertex(Pair.of(293, 341), CoordType.GLOBAL);
    var PYR = graph.getVertex(Pair.of(101, 225), CoordType.GLOBAL);
    var TYR = graph.getVertex(Pair.of(405, 389), CoordType.GLOBAL);

    // Then
    assertThat(graph.getVertices()).hasSize(5);
    assertThat(HEL.getNeighbours()).isEmpty();
    assertThat(THY.getNeighbours()).containsOnly(TYR.getDto());
    assertThat(BRY.getNeighbours()).containsOnly(TYR.getDto());
    assertThat(PYR.getNeighbours()).isEmpty();
    assertThat(TYR.getNeighbours()).containsOnly(THY.getDto(), BRY.getDto());
  }

  @Test
  void whenConnectingNeighbourless_addNeighboursButDoNotConnectAll() {
    // When
    chunkHandler.generateSettlements(chunk);
    chunkHandler.connectNeighbourlessInChunkToClosest();
    var result = chunkHandler.evaluateConnectivity(graph);

    // Then
    graph.getVertices().forEach(v -> assertThat(v.getNeighbours()).isNotEmpty());
    assertThat(result.unvisitedVertices()).hasSize(2);
    assertThat(result.visitedVertices()).hasSize(3);
  }

  private List<Vertex> chunkWithSettlementsExists() {
    var gCoordsList = List.of(Pair.of(0, 0), Pair.of(20, 20), Pair.of(100, 100), Pair.of(499, 499));
    var vertices = new ArrayList<Vertex>();
    for (var gCoords : gCoordsList) {
      graph.addVertex(createSettlement(gCoords));
      var vertex = graph.getVertex(gCoords, CoordType.GLOBAL);
      chunk.place(vertex.getDto());
      vertices.add(vertex);
    }
    return vertices;
  }

  private Settlement createSettlement(Pair<Integer, Integer> chunkCoords) {
    return new Settlement(C_1_W_COORDS, chunkCoords, generators, dataServices, appProperties);
  }
}
