package com.hindsight.king_of_castrop_rauxel.world;

import static com.hindsight.king_of_castrop_rauxel.configuration.AppConstants.*;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

@Slf4j
@Getter
public class Coordinates {

  private Pair<Integer, Integer> global;
  private Pair<Integer, Integer> world;
  private Pair<Integer, Integer> chunk;

  public enum CoordType {
    GLOBAL,
    WORLD,
    CHUNK
  }

  public Coordinates(Pair<Integer, Integer> worldCoords, Pair<Integer, Integer> chunkCoords) {
    verify(worldCoords, CoordType.WORLD);
    verify(chunkCoords, CoordType.CHUNK);
    this.chunk = chunkCoords;
    this.world = worldCoords;
    this.global = toGlobalCoords(worldCoords, chunkCoords);
  }

  public Coordinates(Pair<Integer, Integer> chunkCoords, Chunk chunk) {
    verify(chunkCoords, CoordType.CHUNK);
    this.chunk = chunkCoords;
    this.world = chunk.getCoordinates().getWorld();
    this.global = toGlobalCoords(world, chunkCoords);
  }

  public Coordinates(Pair<Integer, Integer> coords, CoordType type) {
    verify(coords, type);
    switch (type) {
      case GLOBAL -> {
        this.global = coords;
        this.world = toWorldCoords(coords);
        this.chunk = toChunkCoords(coords);
      }
      case WORLD -> {
        this.chunk = Pair.of(0, 0);
        this.world = coords;
        this.global = toGlobalCoords(coords, chunk);
      }
      case CHUNK -> {
        this.chunk = coords;
        this.global = toGlobalCoords(Pair.of(0, 0), coords);
        this.world = Pair.of(0, 0);
        log.error("Do not create 'Coordinates' from chunk coordinates only - refactor your code");
      }
    }
  }

  public void setTo(Pair<Integer, Integer> globalCoords) {
    verify(globalCoords, CoordType.GLOBAL);
    this.global = globalCoords;
    this.world = toWorldCoords(globalCoords);
    this.chunk = toChunkCoords(globalCoords);
  }

  /**
   * Returns true if the coordinates are within the relevant bounds i.e. CHUNK_SIZE for chunkCoords,
   * WORLD_SIZE for worldCoords and CHUNK_SIZE * WORLD_SIZE for globalCoords.
   */
  private void verify(Pair<Integer, Integer> anyCoords, CoordType type) {
    var max =
        switch (type) {
          case GLOBAL -> CHUNK_SIZE * WORLD_SIZE;
          case WORLD -> WORLD_SIZE;
          case CHUNK -> CHUNK_SIZE;
        };
    var x = (int) anyCoords.getFirst();
    var y = (int) anyCoords.getSecond();
    if (x >= 0 && x <= max && y >= 0 && y <= max) {
      return;
    }
    throw new IllegalArgumentException(
        "%s coordinates %s are out of bounds (%s)".formatted(type, anyCoords, max));
  }

  public boolean equalTo(Pair<Integer, Integer> anyCoords, CoordType type) {
    var thisCoords =
        switch (type) {
          case GLOBAL -> global;
          case WORLD -> world;
          case CHUNK -> chunk;
        };
    return (int) thisCoords.getFirst() == anyCoords.getFirst()
        && (int) thisCoords.getSecond() == anyCoords.getSecond();
  }

  public int distanceTo(Coordinates other) {
    return distanceTo(other.getGlobal());
  }

  public int distanceTo(Pair<Integer, Integer> globalCoords) {
    var deltaX = Math.abs(global.getFirst() - globalCoords.getFirst());
    var deltaY = Math.abs(global.getSecond() - globalCoords.getSecond());
    var distance = Math.sqrt((double) deltaX * deltaX + deltaY * deltaY);
    return (int) Math.round(distance);
  }

  private Pair<Integer, Integer> toGlobalCoords(
      Pair<Integer, Integer> worldCoords, Pair<Integer, Integer> chunkCoords) {
    var x = chunkCoords.getFirst() + (worldCoords.getFirst() * CHUNK_SIZE);
    var y = chunkCoords.getSecond() + (worldCoords.getSecond() * CHUNK_SIZE);
    return Pair.of(x, y);
  }

  private Pair<Integer, Integer> toWorldCoords(Pair<Integer, Integer> globalCoords) {
    var x = globalCoords.getFirst() / CHUNK_SIZE;
    var y = globalCoords.getSecond() / CHUNK_SIZE;
    return Pair.of(x, y);
  }

  private Pair<Integer, Integer> toChunkCoords(Pair<Integer, Integer> globalCoords) {
    var x = globalCoords.getFirst() % CHUNK_SIZE;
    var y = globalCoords.getSecond() % CHUNK_SIZE;
    return Pair.of(x, y);
  }

  @Override
  public String toString() {
    return "Coords("
        + "g=("
        + global.getFirst()
        + ", "
        + global.getSecond()
        + "), w=("
        + world.getFirst()
        + ", "
        + world.getSecond()
        + "), c=("
        + chunk.getFirst()
        + ", "
        + chunk.getSecond()
        + "))";
  }

  public String globalToString() {
    return "g(" + global.getFirst() + ", " + global.getSecond() + ")";
  }

  public String worldToString() {
    return "w(" + world.getFirst() + ", " + world.getSecond() + ")";
  }

  public String chunkToString() {
    return "c(" + chunk.getFirst() + ", " + chunk.getSecond() + ")";
  }
}
