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
    chunkHandler = new ChunkHandler(world, graph, appProperties, generators, dataServices);
    daf = new DebugActionFactory(graph, world, chunkHandler, appProperties);
    retentionZone = appProperties.getWorldProperties().retentionZone();
  }

  @Test
  void whenChangingCurrentChunk_unloadChunksOutsideRetentionZone() {
    // Given
    var initialCoords = world.getCentreCoords();
    var removedNorthCoords = Pair.of(initialCoords.getFirst(), initialCoords.getSecond() + 1);
    var removedSouthCoords = Pair.of(initialCoords.getFirst(), initialCoords.getSecond() - 1);

    // When
    world.setCurrentChunk(initialCoords);
    for (var i = 0; i < retentionZone + 1; i++) {
      world.setCurrentChunk(world.getChunk(CardinalDirection.EAST).getCoordinates().getWorld());
    }

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
    world.setCurrentChunk(initialCoords);
    var initialChunk = world.getChunk(CardinalDirection.THIS);
    var initialLocations = initialChunk.getLocations();
    var initialDtos = graph.getVertices().stream().map(Vertex::getDto).toList();
    var initialVerts = graph.getVertices(initialChunk);
    for (var i = 0; i < retentionZone + 1; i++) {
      world.setCurrentChunk(world.getChunk(CardinalDirection.EAST).getCoordinates().getWorld());
    }
    var intermediate = graph.getVertices().stream().map(Vertex::getDto).toList();

    // Then initial chunk is unloaded
    assertThat(world.hasLoadedChunk(initialCoords)).isFalse();

    // When moving back to initial chunk
    world.setCurrentChunk(initialCoords);
    var finalDtos = graph.getVertices().stream().map(Vertex::getDto).toList();
    var finalVerts = graph.getVertices(world.getChunk(CardinalDirection.THIS));
    var finalLocations = world.getChunk(CardinalDirection.THIS).getLocations();

    // Then initial chunk is loaded again
    assertThat(finalDtos).containsAll(initialDtos).hasSizeGreaterThan(1);
    assertThat(finalVerts).containsExactlyInAnyOrderElementsOf(initialVerts);
    assertThat(finalLocations).isEqualTo(initialLocations);
    assertThat(intermediate).hasSizeBetween(initialDtos.size(), finalDtos.size());
  }
}
