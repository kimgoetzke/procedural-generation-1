package com.hindsight.king_of_castrop_rauxel.world;

import static org.assertj.core.api.Assertions.assertThat;

import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.utils.BasicTerrainGenerator;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;

@SpringBootTest
class BasicTerrainGeneratorTest {

  @Autowired private AppProperties appProperties;
  private CoordinateFactory cf;
  private BasicTerrainGenerator terrainGenerator;
  private int worldCentre;

  @BeforeEach
  void setUp() {
    cf = new CoordinateFactory(appProperties);
    terrainGenerator = new BasicTerrainGenerator(appProperties);
    terrainGenerator.initialise(new Random());
    worldCentre = appProperties.getWorldProperties().centre();
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7})
  void givenDifferentXWorldLocations_returnDifficultyAsExpected(int offset) {
    var worldCoords = Pair.of(worldCentre + offset, worldCentre);
    var chunkCoords = Pair.of(250, 250);
    var result = terrainGenerator.getTargetLevel(cf.create(worldCoords, chunkCoords));
    var expected = Math.max(offset, 1);
    assertThat(result).isEqualTo(expected);
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7})
  void givenDifferentYWorldLocations_returnDifficultyAsExpected(int offset) {
    var worldCoords = Pair.of(worldCentre, worldCentre + offset);
    var chunkCoords = Pair.of(250, 250);
    var result = terrainGenerator.getTargetLevel(cf.create(worldCoords, chunkCoords));
    var expected = Math.max(offset, 1);
    assertThat(result).isEqualTo(expected);
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7})
  void givenDifferentWorldLocations_returnDifficultyAsExpected(int offset) {
    var worldCoords = Pair.of(worldCentre + offset, worldCentre + offset);
    var chunkCoords = Pair.of(250, 250);
    var result = terrainGenerator.getTargetLevel(cf.create(worldCoords, chunkCoords));
    var expected = Math.max(offset * 2, 1);
    assertThat(result).isEqualTo(expected);
  }
}
