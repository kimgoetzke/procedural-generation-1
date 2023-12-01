package com.hindsight.king_of_castrop_rauxel.graphs;

import static com.hindsight.king_of_castrop_rauxel.configuration.AppProperties.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.graph.Graph;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import com.hindsight.king_of_castrop_rauxel.location.Settlement;
import com.hindsight.king_of_castrop_rauxel.utils.*;
import com.hindsight.king_of_castrop_rauxel.world.Bounds;
import com.hindsight.king_of_castrop_rauxel.world.Coordinates;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.util.Pair;

@ExtendWith(MockitoExtension.class)
class GraphTest {

  @Mock private DataServices dataServices;
  @Mock private AppProperties appProperties;
  @Mock private Generators generators;
  @Mock private NameGenerator nameGenerator;
  @Mock private TerrainGenerator terrainGenerator;

  private Graph underTest;

  @BeforeEach
  void setUp() {
    when(appProperties.getWorldProperties()).then(i -> new WorldProperties(50, 25, 2));
    when(appProperties.getChunkProperties()).then(returnMockChunkProperties());
    when(appProperties.getSettlementProperties()).then(returnMockSettlementProperties());
    when(appProperties.getGameProperties()).then(i -> new GameProperties(100, 0.1F, 10));
    when(generators.nameGenerator()).thenReturn(nameGenerator);
    when(nameGenerator.locationNameFrom(any())).thenReturn("Fake Settlement");
    when(generators.terrainGenerator()).thenReturn(terrainGenerator);
    when(terrainGenerator.getTargetLevel(any())).thenReturn(1);
    underTest = new Graph();
  }

  @Test
  void whenRetrievingExistingVertexByLocation_returnVertex() {
    // Given
    var l1 = createSettlement(Pair.of(25, 25), Pair.of(250, 250));
    var l2 = createSettlement(Pair.of(25, 25), Pair.of(250, 450));
    var l3 = createSettlement(Pair.of(25, 26), Pair.of(250, 50));
    underTest.addVertex(l1);
    underTest.addVertex(l2);
    underTest.addVertex(l3);

    // When
    var v1 = underTest.getVertex(l1);
    var v2 = underTest.getVertex(l2);
    var v3 = underTest.getVertex(l3);

    // Then
    assertThat(v1.getDto().id()).isEqualTo(l1.getId());
    assertThat(v2.getDto().id()).isEqualTo(l2.getId());
    assertThat(v3.getDto().id()).isEqualTo(l3.getId());
  }

  @Test
  void whenRetrievingExistingVertexByCoords_returnVertex() {
    // Given
    var l1 = createSettlement(Pair.of(25, 25), Pair.of(250, 250));
    var l2 = createSettlement(Pair.of(25, 25), Pair.of(250, 450));
    var l3 = createSettlement(Pair.of(25, 26), Pair.of(250, 50));
    var l4 = createSettlement(Pair.of(25, 27), Pair.of(0, 0));
    underTest.addVertex(l1);
    underTest.addVertex(l2);
    underTest.addVertex(l3);
    underTest.addVertex(l4);

    // When
    var v1 = underTest.getVertex(l1.getCoordinates().getChunk(), Coordinates.CoordType.CHUNK);
    var v2 = underTest.getVertex(l2.getCoordinates().getGlobal(), Coordinates.CoordType.GLOBAL);
    var v3 = underTest.getVertex(l3.getCoordinates().getGlobal(), Coordinates.CoordType.GLOBAL);
    var v4 = underTest.getVertex(l4.getCoordinates().getWorld(), Coordinates.CoordType.WORLD);

    // Then
    assertThat(v1.getDto().id()).isEqualTo(l1.getId());
    assertThat(v2.getDto().id()).isEqualTo(l2.getId());
    assertThat(v3.getDto().id()).isEqualTo(l3.getId());
    assertThat(v4.getDto().id()).isEqualTo(l4.getId());
  }

  /**
   * Note that the Graph is sorted by vertex ID so that ID, meaning that the most south-western
   * vertex will be picked up first. In this case, w(25,24) is the most south-western chunk.
   */
  @Test
  void givenLocationsWithSameChunkCoords_whenRetrievingByChunkCords_cannotDistinguish() {
    // Given
    var l1 = createSettlement(Pair.of(25, 25), Pair.of(250, 250));
    var l2 = createSettlement(Pair.of(25, 26), Pair.of(250, 250));
    var l3 = createSettlement(Pair.of(25, 24), Pair.of(250, 250));
    underTest.addVertex(l1);
    underTest.addVertex(l2);
    underTest.addVertex(l3);

    // When
    var v1 = underTest.getVertex(l1.getCoordinates().getChunk(), Coordinates.CoordType.CHUNK);
    var v2 = underTest.getVertex(l2.getCoordinates().getChunk(), Coordinates.CoordType.CHUNK);
    var v3 = underTest.getVertex(l3.getCoordinates().getChunk(), Coordinates.CoordType.CHUNK);

    // Then
    assertThat(v1.getDto().id()).isEqualTo(l3.getId());
    assertThat(v2.getDto().id()).isEqualTo(l3.getId());
    assertThat(v3.getDto().id()).isEqualTo(l3.getId());
  }

  /**
   * Note that the Graph is sorted by vertex ID so that ID, meaning that the most south-western
   * vertex will be picked up first. In this case, c(0,0) is the most south-western settlement in
   * the chunk at w(24,25).
   */
  @Test
  void givenLocationsWithSameWorldCoords_whenRetrievingByWorldCords_cannotDistinguish() {
    // Given
    var l1 = createSettlement(Pair.of(25, 25), Pair.of(0, 0));
    var l2 = createSettlement(Pair.of(24, 25), Pair.of(5, 5));
    var l3 = createSettlement(Pair.of(24, 25), Pair.of(0, 0));
    var l4 = createSettlement(Pair.of(24, 25), Pair.of(100, 100));
    underTest.addVertex(l1);
    underTest.addVertex(l2);
    underTest.addVertex(l3);
    underTest.addVertex(l4);

    // When
    var v1 = underTest.getVertex(l1.getCoordinates().getWorld(), Coordinates.CoordType.WORLD);
    var v3 = underTest.getVertex(l3.getCoordinates().getWorld(), Coordinates.CoordType.WORLD);
    var v4 = underTest.getVertex(l4.getCoordinates().getWorld(), Coordinates.CoordType.WORLD);

    // Then
    assertThat(v1.getDto().id()).isEqualTo(l1.getId());
    assertThat(v3.getDto().id()).isEqualTo(l3.getId());
    assertThat(v4.getDto().id()).isEqualTo(l3.getId());
  }

  private Settlement createSettlement(
      Pair<Integer, Integer> worldCoords, Pair<Integer, Integer> chunkCoords) {
    return new Settlement(worldCoords, chunkCoords, generators, dataServices, appProperties);
  }

  private static Answer<Object> returnMockChunkProperties() {
    return invocation -> new ChunkProperties(500, 30, 130, 100, Bounds.of(5, 10));
  }

  private static Answer<Object> returnMockSettlementProperties() {
    return invocation -> {
      var inhabitants = Bounds.of(10, 100);
      var area = Bounds.of(2, 3);
      var amenities = Map.of(PointOfInterest.Type.MAIN_SQUARE, Bounds.of(1, 1));
      var config = new SettlementConfig();
      config.setAmenities(amenities);
      config.setArea(area);
      config.setInhabitants(inhabitants);
      return new SettlementProperties(config, config, config, config, config);
    };
  }
}
