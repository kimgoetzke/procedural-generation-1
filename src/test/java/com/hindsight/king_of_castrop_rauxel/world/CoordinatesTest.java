package com.hindsight.king_of_castrop_rauxel.world;

import static com.hindsight.king_of_castrop_rauxel.configuration.AppConstants.*;
import static com.hindsight.king_of_castrop_rauxel.world.Coordinates.*;
import static org.junit.jupiter.api.Assertions.*;

import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.location.AbstractLocation;
import com.hindsight.king_of_castrop_rauxel.utils.StringGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;

@SpringBootTest
class CoordinatesTest {

  @Autowired WorldHandler worldHandler;
  @Autowired StringGenerator stringGenerator;
  @Mock Graph<AbstractLocation> map;

  @BeforeEach
  void setUp() {
    worldHandler = new WorldHandler(map, stringGenerator);
  }

  @Test
  void givenWorldAndChunkCoords_createCoords() {
    var worldCoords = Pair.of(2, 2);
    var chunkCoords = Pair.of(250, 250);
    var coordinates = new Coordinates(worldCoords, chunkCoords);
    var expectedGlobal = expectedGlobalFrom(worldCoords, chunkCoords); // here (1250, 1250)

    assertEquals(expectedGlobal, coordinates.getGlobal());
    assertEquals(worldCoords, coordinates.getWorld());
    assertEquals(chunkCoords, coordinates.getChunk());
  }

  @Test
  void givenChunkCoordsAndChunkInstance_createCoords() {
    var worldCoords = Pair.of(8, 6);
    var chunkCoords = Pair.of(200, 200);
    var expectedGlobal = expectedGlobalFrom(worldCoords, chunkCoords); // here (2700, 3200)
    var chunk = new Chunk(worldCoords, worldHandler);
    var coordinates = new Coordinates(chunkCoords, chunk);

    assertEquals(expectedGlobal, coordinates.getGlobal());
    assertEquals(worldCoords, coordinates.getWorld());
    assertEquals(chunkCoords, coordinates.getChunk());
  }

  @Test
  void givenValidWorldCoords_createCoords() {
    var worldCoords = Pair.of(7, 8);
    var expectedGlobal = expectedGlobalFrom(worldCoords, Pair.of(0, 0)); // here (3500, 4000)
    var coordinates = new Coordinates(worldCoords, CoordType.WORLD);

    assertEquals(expectedGlobal, coordinates.getGlobal());
    assertEquals(worldCoords, coordinates.getWorld());
    assertEquals(Pair.of(0, 0), coordinates.getChunk());
  }

  @Test
  void givenOutOfBoundsWorldCoords_failToCreateCoords() {
    var worldCoords = Pair.of(-1, 8);

    assertThrows(
        IllegalArgumentException.class, () -> new Coordinates(worldCoords, CoordType.WORLD));
  }

  @Test
  void givenValidGlobalCoords_createCoords() {
    var globalCoords = Pair.of(3205, 675);
    var coordinates = new Coordinates(globalCoords, CoordType.GLOBAL);
    var expectedWorld = expectedWorldFrom(globalCoords); // here (6, 1)
    var expectedChunk = expectedChunkFrom(globalCoords); // here (205, 175)

    assertEquals(globalCoords, coordinates.getGlobal());
    assertEquals(expectedWorld, coordinates.getWorld());
    assertEquals(expectedChunk, coordinates.getChunk());
  }

  @Test
  void givenValidGlobalCoords_updateCoords() {
    var worldCoords = Pair.of(2, 2);
    var chunkCoords = Pair.of(250, 250);
    var coordinates = new Coordinates(worldCoords, chunkCoords);
    // Creates Coords(g=(1250, 1250), w=(2, 2), c=(250, 250))

    coordinates.setTo(Pair.of(1230, 1505));
    // Updates to Coords(g=(1230, 1505), w=(2, 3), c=(230, 5))

    var expected = new Coordinates(Pair.of(1230, 1505), CoordType.GLOBAL);
    var x = expected.getGlobal().getFirst();
    var y = expected.getGlobal().getSecond();

    assertEquals(Pair.of(x, y), coordinates.getGlobal()); // here (1230, 1505)
    assertEquals(Pair.of(x / CHUNK_SIZE, y / CHUNK_SIZE), coordinates.getWorld()); // here (2, 2)
    assertEquals(Pair.of(x % CHUNK_SIZE, y % CHUNK_SIZE), coordinates.getChunk()); // here (230, 5)
  }

  @Test
  void givenOutOfBoundsGlobalCoords_failToUpdateCoords() {
    var coordinates = new Coordinates(Pair.of(2, 3), CoordType.WORLD);
    var invalidCoordinates = Pair.of(-1, 14);

    assertThrows(IllegalArgumentException.class, () -> coordinates.setTo(invalidCoordinates));
  }

  @Test
  void givenSameWorldCoords_calculateZeroDistance() {
    var reference = new Coordinates(Pair.of(2, 3), CoordType.WORLD);
    var other = new Coordinates(Pair.of(2, 3), CoordType.WORLD);
    var expected = 0;

    assertEquals(expected, reference.distanceTo(other));
    assertEquals(expected, reference.distanceTo(other.getGlobal()));
  }

  @Test
  void givenDifferentGlobalCoords_calculateDistance1() {
    var reference = new Coordinates(Pair.of(1250, 1250), CoordType.GLOBAL);
    var other = new Coordinates(Pair.of(1250, 1000), CoordType.GLOBAL);
    var expected = 250;

    assertEquals(expected, reference.distanceTo(other));
    assertEquals(expected, reference.distanceTo(other.getGlobal()));
  }

  @Test
  void givenDifferentGlobalCoords_calculateDistance2() {
    var reference = new Coordinates(Pair.of(500, 500), CoordType.GLOBAL);
    var other = new Coordinates(Pair.of(1000, 1000), CoordType.GLOBAL);
    var expected = 707;

    assertEquals(expected, reference.distanceTo(other));
    assertEquals(expected, reference.distanceTo(other.getGlobal()));
  }

  private Pair<Integer, Integer> expectedGlobalFrom(
      Pair<Integer, Integer> worldCoords, Pair<Integer, Integer> chunkCoords) {
    var x = (worldCoords.getFirst() * CHUNK_SIZE) + chunkCoords.getFirst();
    var y = (worldCoords.getSecond() * CHUNK_SIZE) + chunkCoords.getSecond();
    return Pair.of(x, y);
  }

  private Pair<Integer, Integer> expectedWorldFrom(Pair<Integer, Integer> globalCoords) {
    var x = globalCoords.getFirst() / CHUNK_SIZE;
    var y = globalCoords.getSecond() / CHUNK_SIZE;
    return Pair.of(x, y);
  }

  private Pair<Integer, Integer> expectedChunkFrom(Pair<Integer, Integer> globalCoords) {
    var x = globalCoords.getFirst() % CHUNK_SIZE;
    var y = globalCoords.getSecond() % CHUNK_SIZE;
    return Pair.of(x, y);
  }
}
