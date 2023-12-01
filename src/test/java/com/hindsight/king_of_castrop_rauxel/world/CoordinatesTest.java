package com.hindsight.king_of_castrop_rauxel.world;

import static com.hindsight.king_of_castrop_rauxel.world.Coordinates.*;
import static org.junit.jupiter.api.Assertions.*;

import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.graph.Graph;
import com.hindsight.king_of_castrop_rauxel.utils.DataServices;
import com.hindsight.king_of_castrop_rauxel.utils.Generators;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;

@SpringBootTest
class CoordinatesTest {

  @Autowired ChunkHandler chunkHandler;
  @Autowired Generators generators;
  @Autowired DataServices dataServices;
  @Autowired AppProperties appProperties;
  @Autowired CoordinateFactory cf;
  @Autowired World world;
  @Mock Graph graph;
  private int chunkSize;

  @BeforeEach
  void setUp() {
    chunkHandler = new ChunkHandler(world, graph, appProperties, generators, dataServices);
    chunkSize = appProperties.getChunkProperties().size();
  }

  @Test
  void givenWorldAndChunkCoords_createCoords() {
    var worldCoords = Pair.of(2, 2);
    var chunkCoords = Pair.of(250, 250);
    var coordinates = cf.create(worldCoords, chunkCoords);
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
    var chunk = new Chunk(worldCoords, chunkHandler);
    var coordinates = cf.create(chunkCoords, chunk);

    assertEquals(expectedGlobal, coordinates.getGlobal());
    assertEquals(worldCoords, coordinates.getWorld());
    assertEquals(chunkCoords, coordinates.getChunk());
  }

  @Test
  void givenValidWorldCoords_createCoords() {
    var worldCoords = Pair.of(7, 8);
    var expectedGlobal = expectedGlobalFrom(worldCoords, Pair.of(0, 0)); // here (3500, 4000)
    var coordinates = cf.create(worldCoords, CoordType.WORLD);

    assertEquals(expectedGlobal, coordinates.getGlobal());
    assertEquals(worldCoords, coordinates.getWorld());
    assertEquals(Pair.of(0, 0), coordinates.getChunk());
  }

  @Test
  void givenOutOfBoundsWorldCoords_failToCreateCoords() {
    var worldCoords = Pair.of(-1, 8);

    assertThrows(IllegalArgumentException.class, () -> cf.create(worldCoords, CoordType.WORLD));
  }

  @Test
  void givenValidGlobalCoords_createCoords() {
    var globalCoords = Pair.of(3205, 675);
    var coordinates = cf.create(globalCoords, CoordType.GLOBAL);
    var expectedWorld = expectedWorldFrom(globalCoords); // here (6, 1)
    var expectedChunk = expectedChunkFrom(globalCoords); // here (205, 175)

    assertEquals(globalCoords, coordinates.getGlobal());
    assertEquals(expectedWorld, coordinates.getWorld());
    assertEquals(expectedChunk, coordinates.getChunk());
  }

  @Test
  void givenValidGlobalCoords_updateCoords() {
    // Creates Coords(g=(1250, 1250), w=(2, 2), c=(250, 250))
    var worldCoords = Pair.of(2, 2);
    var chunkCoords = Pair.of(250, 250);
    var coordinates = cf.create(worldCoords, chunkCoords);

    // Updates to Coords(g=(1230, 1505), w=(2, 3), c=(230, 5))
    coordinates.setTo(Pair.of(1230, 1505));

    var expected = cf.create(Pair.of(1230, 1505), CoordType.GLOBAL);
    var x = expected.gX();
    var y = expected.gY();

    assertEquals(Pair.of(x, y), coordinates.getGlobal()); // here (1230, 1505)
    assertEquals(Pair.of(x / chunkSize, y / chunkSize), coordinates.getWorld()); // here (2, 2)
    assertEquals(Pair.of(x % chunkSize, y % chunkSize), coordinates.getChunk()); // here (230, 5)
  }

  @Test
  void givenOutOfBoundsGlobalCoords_failToUpdateCoords() {
    var coordinates = cf.create(Pair.of(2, 3), CoordType.WORLD);
    var invalidCoordinates = Pair.of(-1, 14);

    assertThrows(IllegalArgumentException.class, () -> coordinates.setTo(invalidCoordinates));
  }

  @Test
  void givenSameWorldCoords_calculateZeroDistance() {
    var reference = cf.create(Pair.of(2, 3), CoordType.WORLD);
    var other = cf.create(Pair.of(2, 3), CoordType.WORLD);
    var expected = 0;

    assertEquals(expected, reference.distanceTo(other));
    assertEquals(expected, reference.distanceTo(other.getGlobal()));
  }

  @Test
  void givenDifferentGlobalCoords_calculateDistance1() {
    var reference = cf.create(Pair.of(1250, 1250), CoordType.GLOBAL);
    var other = cf.create(Pair.of(1250, 1000), CoordType.GLOBAL);
    var expected = 250;

    assertEquals(expected, reference.distanceTo(other));
    assertEquals(expected, reference.distanceTo(other.getGlobal()));
  }

  @Test
  void givenDifferentGlobalCoords_calculateDistance2() {
    var reference = cf.create(Pair.of(500, 500), CoordType.GLOBAL);
    var other = cf.create(Pair.of(1000, 1000), CoordType.GLOBAL);
    var expected = 707;

    assertEquals(expected, reference.distanceTo(other));
    assertEquals(expected, reference.distanceTo(other.getGlobal()));
  }

  private Pair<Integer, Integer> expectedGlobalFrom(
      Pair<Integer, Integer> worldCoords, Pair<Integer, Integer> chunkCoords) {
    var x = (worldCoords.getFirst() * chunkSize) + chunkCoords.getFirst();
    var y = (worldCoords.getSecond() * chunkSize) + chunkCoords.getSecond();
    return Pair.of(x, y);
  }

  private Pair<Integer, Integer> expectedWorldFrom(Pair<Integer, Integer> globalCoords) {
    var x = globalCoords.getFirst() / chunkSize;
    var y = globalCoords.getSecond() / chunkSize;
    return Pair.of(x, y);
  }

  private Pair<Integer, Integer> expectedChunkFrom(Pair<Integer, Integer> globalCoords) {
    var x = globalCoords.getFirst() % chunkSize;
    var y = globalCoords.getSecond() % chunkSize;
    return Pair.of(x, y);
  }
}
