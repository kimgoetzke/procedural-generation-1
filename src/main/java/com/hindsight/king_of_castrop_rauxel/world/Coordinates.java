package com.hindsight.king_of_castrop_rauxel.world;

import static com.google.common.base.Preconditions.checkArgument;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

@Slf4j
@Getter
public class Coordinates {

  private final int worldSize;
  private final int chunkSize;
  private Pair<Integer, Integer> global;
  private Pair<Integer, Integer> world;
  private Pair<Integer, Integer> chunk;

  public enum CoordType {
    GLOBAL,
    WORLD,
    CHUNK
  }

  public Coordinates(
      Pair<Integer, Integer> worldCoords,
      Pair<Integer, Integer> chunkCoords,
      int worldSize,
      int chunkSize) {
    this.worldSize = worldSize;
    this.chunkSize = chunkSize;
    verify(worldCoords, CoordType.WORLD);
    verify(chunkCoords, CoordType.CHUNK);
    this.chunk = chunkCoords;
    this.world = worldCoords;
    this.global = toGlobalCoords(worldCoords, chunkCoords);
  }

  public Coordinates(
      Pair<Integer, Integer> chunkCoords, Chunk chunk, int worldSize, int chunkSize) {
    this.worldSize = worldSize;
    this.chunkSize = chunkSize;
    verify(chunkCoords, CoordType.CHUNK);
    this.chunk = chunkCoords;
    this.world = chunk.getCoordinates().getWorld();
    this.global = toGlobalCoords(world, chunkCoords);
  }

  public Coordinates(Pair<Integer, Integer> coords, CoordType type, int worldSize, int chunkSize) {
    this.worldSize = worldSize;
    this.chunkSize = chunkSize;
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

  public static Coordinates of(Coordinates coordinates) {
    return new Coordinates(
        coordinates.getGlobal(), CoordType.GLOBAL, coordinates.worldSize, coordinates.chunkSize);
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
          case GLOBAL -> chunkSize * worldSize;
          case WORLD -> worldSize;
          case CHUNK -> chunkSize;
        };
    var x = (int) anyCoords.getFirst();
    var y = (int) anyCoords.getSecond();
    var isWithinBounds = x >= 0 && x <= max && y >= 0 && y <= max;
    checkArgument(isWithinBounds, "%s coordinates %s are out of bounds", type, anyCoords);
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

  public int gX() {
    return global.getFirst();
  }

  public int gY() {
    return global.getSecond();
  }

  public int wX() {
    return world.getFirst();
  }

  public int wY() {
    return world.getSecond();
  }

  public int cX() {
    return chunk.getFirst();
  }

  public int cY() {
    return chunk.getSecond();
  }

  private Pair<Integer, Integer> toGlobalCoords(
      Pair<Integer, Integer> worldCoords, Pair<Integer, Integer> chunkCoords) {
    var x = chunkCoords.getFirst() + (worldCoords.getFirst() * chunkSize);
    var y = chunkCoords.getSecond() + (worldCoords.getSecond() * chunkSize);
    return Pair.of(x, y);
  }

  private Pair<Integer, Integer> toWorldCoords(Pair<Integer, Integer> globalCoords) {
    var x = globalCoords.getFirst() / chunkSize;
    var y = globalCoords.getSecond() / chunkSize;
    return Pair.of(x, y);
  }

  private Pair<Integer, Integer> toChunkCoords(Pair<Integer, Integer> globalCoords) {
    var x = globalCoords.getFirst() % chunkSize;
    var y = globalCoords.getSecond() % chunkSize;
    return Pair.of(x, y);
  }

  @Override
  public String toString() {
    return "Coords(" + "g=(" + gX() + ", " + gY() + "), w=(" + wX() + ", " + wY() + "), c=(" + cX()
        + ", " + cY() + "))";
  }

  public String globalToString() {
    return "g(" + gX() + ", " + gY() + ")";
  }

  public String worldToString() {
    return "w(" + wX() + ", " + wY() + ")";
  }

  public String chunkToString() {
    return "c(" + cX() + ", " + cY() + ")";
  }
}
