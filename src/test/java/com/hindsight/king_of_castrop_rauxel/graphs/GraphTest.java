package com.hindsight.king_of_castrop_rauxel.graphs;

import static com.hindsight.king_of_castrop_rauxel.configuration.AppProperties.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import com.hindsight.king_of_castrop_rauxel.location.Settlement;
import com.hindsight.king_of_castrop_rauxel.utils.*;
import com.hindsight.king_of_castrop_rauxel.world.Bounds;
import java.util.Map;
import java.util.Random;
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

  private final Random random = new Random();

  @BeforeEach
  void setUp() {
    when(appProperties.getWorldProperties()).then(i -> new WorldProperties(50, 25, 2));
    when(appProperties.getChunkProperties()).then(returnMockChunkProperties());
    when(appProperties.getSettlementProperties()).then(returnMockSettlementProperties());
    when(appProperties.getGameProperties()).then(i -> new GameProperties(100, 0.1F, 10));
    when(generators.nameGenerator()).thenReturn(nameGenerator);
    when(nameGenerator.locationNameFrom(any())).thenReturn("Settlement " + random.nextInt(1000));
    when(generators.terrainGenerator()).thenReturn(terrainGenerator);
    when(terrainGenerator.getTargetLevel(any())).thenReturn(1);
  }

  @Test
  void whenRetrievingExistingVertexByValue_returnVertex() {
    // Given
    var location = createSettlement(Pair.of(25, 25), Pair.of(250, 250));
    var map = new Graph(true);
    map.addVertex(location);

    // When
    var vertex = map.getVertexByValue(location);

    // Then
    assertThat(vertex.getDto().id()).isEqualTo(location.getId());
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
