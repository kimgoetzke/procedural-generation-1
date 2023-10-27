package com.hindsight.king_of_castrop_rauxel.world;

import static com.hindsight.king_of_castrop_rauxel.configuration.AppConstants.WORLD_CENTER;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Random;

import com.hindsight.king_of_castrop_rauxel.utils.BasicTerrainGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.util.Pair;

class BasicTerrainGeneratorTest {

  private BasicTerrainGenerator terrainGenerator;

  @BeforeEach
  void setUp() {
    terrainGenerator = new BasicTerrainGenerator();
    terrainGenerator.initialise(new Random());
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7})
  void givenDifferentXWorldLocations_returnDifficultyAsExpected(int offset) {
    var worldCoords = Pair.of(WORLD_CENTER + offset, WORLD_CENTER);
    var chunkCoords = Pair.of(250, 250);
    var result = terrainGenerator.getTargetLevel(new Coordinates(worldCoords, chunkCoords));
    var expected = Math.max(offset, 1);
    assertThat(result).isEqualTo(expected);
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7})
  void givenDifferentYWorldLocations_returnDifficultyAsExpected(int offset) {
    var worldCoords = Pair.of(WORLD_CENTER, WORLD_CENTER + offset);
    var chunkCoords = Pair.of(250, 250);
    var result = terrainGenerator.getTargetLevel(new Coordinates(worldCoords, chunkCoords));
    var expected = Math.max(offset, 1);
    assertThat(result).isEqualTo(expected);
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7})
  void givenDifferentWorldLocations_returnDifficultyAsExpected(int offset) {
    var worldCoords = Pair.of(WORLD_CENTER + offset, WORLD_CENTER + offset);
    var chunkCoords = Pair.of(250, 250);
    var result = terrainGenerator.getTargetLevel(new Coordinates(worldCoords, chunkCoords));
    var expected = Math.max(offset * 2, 1);
    assertThat(result).isEqualTo(expected);
  }
}
