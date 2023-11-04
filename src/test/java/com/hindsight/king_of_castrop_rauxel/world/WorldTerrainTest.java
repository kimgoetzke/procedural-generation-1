package com.hindsight.king_of_castrop_rauxel.world;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class WorldTerrainTest extends BaseWorldTest {

  @Test
  void whenReturningToPrevChunk_generateTheSameChunkAgain() {
    // Given
    var initialCoords = world.getCentreCoords();

    // When moving outside the retention zone
    world.generateChunk(initialCoords, map);
    world.setCurrentChunk(initialCoords);
    assertThat(world.getCurrentChunk().getDifficulty()).isEqualTo(1);
    for (var i = 0; i < 5; i++) {
      world.generateChunk(CardinalDirection.EAST, map);
      world.setCurrentChunk(world.getChunk(CardinalDirection.EAST).getCoordinates().getWorld());
    }
  }
}
