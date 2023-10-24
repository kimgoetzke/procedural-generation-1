package com.hindsight.king_of_castrop_rauxel.world;

import static com.hindsight.king_of_castrop_rauxel.configuration.AppConstants.RETENTION_ZONE;
import static com.hindsight.king_of_castrop_rauxel.world.WorldHandler.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

import com.hindsight.king_of_castrop_rauxel.action.debug.DebugActionFactory;
import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.graphs.Vertex;
import com.hindsight.king_of_castrop_rauxel.location.LocationBuilder;

import java.util.Random;

import com.hindsight.king_of_castrop_rauxel.location.Size;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;

@SpringBootTest
class AutoUnloadingTest extends BaseWorldTest {

  @BeforeEach
  void setUp() {
    SeedBuilder.changeSeed(123L);
    map = new Graph<>(true);
    worldHandler = new WorldHandler(map, generators);
    world = new World(appProperties, worldHandler);
    daf = new DebugActionFactory(map, world, worldHandler);
  }

  @Test
  void whenChangingCurrentChunk_unloadChunksOutsideRetentionZone() {
    try (var mocked = mockStatic(LocationBuilder.class)) {
      // Given
      locationComponentIsInitialised(mocked);
      var initialCoords = world.getCentreCoords();
      var removedNorthCoords = Pair.of(initialCoords.getFirst(), initialCoords.getSecond() + 1);
      var removedSouthCoords = Pair.of(initialCoords.getFirst(), initialCoords.getSecond() - 1);

      // When
      world.generateChunk(initialCoords, map);
      world.setCurrentChunk(initialCoords);
      for (var i = 0; i < RETENTION_ZONE + 1; i++) {
        world.generateChunk(CardinalDirection.NORTH, map);
        world.generateChunk(CardinalDirection.EAST, map);
        world.generateChunk(CardinalDirection.SOUTH, map);
        world.setCurrentChunk(world.getChunk(CardinalDirection.EAST).getCoordinates().getWorld());
      }
      debug(map.getVertices(), map);

      // Then
      var currentWorldCoords = world.getCurrentChunk().getCoordinates();
      var nCoords = Pair.of(currentWorldCoords.wX() - RETENTION_ZONE, currentWorldCoords.wY() + 1);
      var eCoords = Pair.of(currentWorldCoords.wX() - RETENTION_ZONE, currentWorldCoords.wY());
      var sCoords = Pair.of(currentWorldCoords.wX() - RETENTION_ZONE, currentWorldCoords.wY() - 1);
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
  }

  @Test
  void whenReturningToPrevChunk_generateTheSameChunkAgain() {
    try (var mocked = mockStatic(LocationBuilder.class)) {
      // Given
      locationComponentIsInitialised(mocked);
      var initialCoords = world.getCentreCoords();

      // When
      world.generateChunk(initialCoords, map);
      world.setCurrentChunk(initialCoords);
      var expected = map.getVertices().stream().map(Vertex::getLocation).toList();
      for (var i = 0; i < RETENTION_ZONE + 1; i++) {
        world.generateChunk(CardinalDirection.EAST, map);
        world.setCurrentChunk(world.getChunk(CardinalDirection.EAST).getCoordinates().getWorld());
      }
      world.setCurrentChunk(initialCoords);
      var result = map.getVertices().stream().map(Vertex::getLocation).toList();
      debug(map.getVertices(), map);

      // Then
      assertThat(result).containsAll(expected);
    }
  }

  @Override
  protected void locationComponentIsInitialised(MockedStatic<LocationBuilder> mocked) {
    mocked.when(() -> LocationBuilder.randomSize(any(Random.class))).thenReturn(Size.M);
    mocked
        .when(() -> LocationBuilder.getSettlementConfig(Size.M))
        .thenReturn(fakeConfig.get(Size.M));
  }
}
