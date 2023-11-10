package com.hindsight.king_of_castrop_rauxel.world;

import static org.assertj.core.api.Assertions.assertThat;

import com.hindsight.king_of_castrop_rauxel.action.debug.DebugActionFactory;
import com.hindsight.king_of_castrop_rauxel.graphs.Vertex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AutoUnloadingTest extends BaseWorldTest {

  private int retentionZone;

  @BeforeEach
  void setUp() {
    SeedBuilder.changeSeed(123L);
    chunkHandler = new ChunkHandler(world, map, appProperties, generators, dataServices);
    daf = new DebugActionFactory(map, world, chunkHandler, appProperties);
    retentionZone = appProperties.getWorldProperties().retentionZone();
  }

  @Test
  void whenChangingCurrentChunk_unloadChunksOutsideRetentionZone() {
    // Given
    var initialCoords = world.getCentreCoords();
    var removedNorthCoords = Pair.of(initialCoords.getFirst(), initialCoords.getSecond() + 1);
    var removedSouthCoords = Pair.of(initialCoords.getFirst(), initialCoords.getSecond() - 1);

    // When
    world.generateChunk(initialCoords, map);
    world.setCurrentChunk(initialCoords);
    for (var i = 0; i < retentionZone + 1; i++) {
      world.generateChunk(CardinalDirection.NORTH, map);
      world.generateChunk(CardinalDirection.EAST, map);
      world.generateChunk(CardinalDirection.SOUTH, map);
      world.setCurrentChunk(world.getChunk(CardinalDirection.EAST).getCoordinates().getWorld());
    }
    debug(map.getVertices(), map);

    // Then
    var currentWorldCoords = world.getCurrentChunk().getCoordinates();
    var nCoords = Pair.of(currentWorldCoords.wX() - retentionZone, currentWorldCoords.wY() + 1);
    var eCoords = Pair.of(currentWorldCoords.wX() - retentionZone, currentWorldCoords.wY());
    var sCoords = Pair.of(currentWorldCoords.wX() - retentionZone, currentWorldCoords.wY() - 1);
    assertThat(world.hasLoadedChunk(removedNorthCoords)).isFalse();
    assertThat(world.hasLoadedChunk(initialCoords)).isFalse();
    assertThat(world.hasLoadedChunk(removedSouthCoords)).isFalse();
    assertThat(world.hasLoadedChunk(nCoords)).isTrue();
    assertThat(world.hasLoadedChunk(eCoords)).isTrue();
    assertThat(world.hasLoadedChunk(sCoords)).isTrue();
    assertThat(world.hasLoadedChunk(CardinalDirection.NORTH_WEST)).isTrue();
    assertThat(world.hasLoadedChunk(CardinalDirection.WEST)).isTrue();
    assertThat(world.hasLoadedChunk(CardinalDirection.SOUTH_WEST)).isTrue();
  }

  @Test
  void whenReturningToPrevChunk_generateTheSameChunkAgain() {
    // Given
    var initialCoords = world.getCentreCoords();

    // When moving outside the retention zone
    world.generateChunk(initialCoords, map);
    world.setCurrentChunk(initialCoords);
    var initial = map.getVertices().stream().map(Vertex::getDto).toList();
    for (var i = 0; i < retentionZone + 1; i++) {
      world.generateChunk(CardinalDirection.EAST, map);
      world.setCurrentChunk(world.getChunk(CardinalDirection.EAST).getCoordinates().getWorld());
    }
    var intermediate = map.getVertices().stream().map(Vertex::getDto).toList();

    // Then initial chunk is unloaded
    assertThat(world.hasLoadedChunk(initialCoords)).isFalse();

    // When moving back to initial chunk
    world.setCurrentChunk(initialCoords);
    var result = map.getVertices().stream().map(Vertex::getDto).toList();

    // Then initial chunk is loaded again
    assertThat(result).containsAll(initial).hasSizeGreaterThan(1);
    assertThat(intermediate).hasSizeBetween(initial.size(), result.size());
  }
}
