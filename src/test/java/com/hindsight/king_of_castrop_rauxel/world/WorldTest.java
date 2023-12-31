package com.hindsight.king_of_castrop_rauxel.world;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;

@SpringBootTest
class WorldTest {

  @Autowired private World underTest;

  @BeforeEach
  void setUp() {}

  @Test
  void whenRequestingChunks_returnChunkAsExpected() {
    // When
    underTest.setCurrentChunk(Pair.of(25, 25));
    var requestedChunk = underTest.getChunk(Pair.of(25, 25));
    var currentChunk = underTest.getCurrentChunk();
    var thisChunk = underTest.getChunk(CardinalDirection.THIS);

    // Then
    assertThat(requestedChunk).isNotNull().isEqualTo(currentChunk).isEqualTo(thisChunk);
    assertThat(requestedChunk.isLoaded()).isTrue();
  }

  @Test
  void givenChunkIsPlaced_whenGetByCoords_returnChunk() {
    // When
    underTest.setCurrentChunk(Pair.of(0, 0));
    var currentChunk = underTest.getCurrentChunk();
    var validChunk1 = underTest.getChunk(CardinalDirection.NORTH);
    var validChunk2 = underTest.getChunk(CardinalDirection.EAST);
    var invalidChunk1 = underTest.getChunk(CardinalDirection.WEST);
    var invalidChunk2 = underTest.getChunk(CardinalDirection.SOUTH);
    var invalidChunk3 = underTest.getChunk(Pair.of(999, 999));

    // Then
    assertThat(currentChunk).isNotNull().hasFieldOrPropertyWithValue("loaded", true);
    assertThat(validChunk1).isNotNull().hasFieldOrPropertyWithValue("loaded", true);
    assertThat(validChunk2).isNotNull().hasFieldOrPropertyWithValue("loaded", true);
    assertThat(invalidChunk1).isNull();
    assertThat(invalidChunk2).isNull();
    assertThat(invalidChunk3).isNull();
  }
}
