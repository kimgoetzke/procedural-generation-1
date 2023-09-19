package com.hindsight.king_of_castrop_rauxel.world;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import static com.hindsight.king_of_castrop_rauxel.world.WorldBuildingComponent.*;

@Slf4j
@Component
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ChunkComponent {

  public static final int CHUNK_SIZE = 500;
  public static final int MIN_PLACEMENT_DISTANCE = 5;
  public static final int MAX_NEIGHBOUR_DISTANCE = 100;
  public static final int GENERATION_TRIGGER_ZONE = 100;
  private static final Bounds chunkDensity = new Bounds(5, 10);

  public static Chunk generateChunk(Random random, Pair<Integer, Integer> coordinates) {
    return new Chunk(randomDensity(random), coordinates);
  }

  private static int randomDensity(Random random) {
    return random.nextInt(chunkDensity.getUpper() - chunkDensity.getLower() + 1)
        + chunkDensity.getLower();
  }

  public static boolean isInsideTriggerZone(Pair<Integer, Integer> coordinates) {
    return coordinates.getFirst() > CHUNK_SIZE - GENERATION_TRIGGER_ZONE
        || coordinates.getFirst() < GENERATION_TRIGGER_ZONE
        || coordinates.getSecond() > CHUNK_SIZE - GENERATION_TRIGGER_ZONE
        || coordinates.getSecond() < GENERATION_TRIGGER_ZONE;
  }

  public static CardinalDirection nextChunkPosition(Pair<Integer, Integer> coordinates) {
    if (coordinates.getFirst() > CHUNK_SIZE - GENERATION_TRIGGER_ZONE) {
      return CardinalDirection.EAST;
    } else if (coordinates.getFirst() < GENERATION_TRIGGER_ZONE) {
      return CardinalDirection.WEST;
    } else if (coordinates.getSecond() > CHUNK_SIZE - GENERATION_TRIGGER_ZONE) {
      return CardinalDirection.NORTH;
    } else if (coordinates.getSecond() < GENERATION_TRIGGER_ZONE) {
      return CardinalDirection.SOUTH;
    } else {
      throw new IllegalStateException(
          "Cannot return RelativePosition for coordinates " + coordinates);
    }
  }

  public static void log(Chunk chunk, CardinalDirection position) {
    if (chunk == null) {
      log.info("{} chunk does not exist yet", position);
      return;
    }
    var settlements = new AtomicInteger();
    Arrays.stream(chunk.getPlane())
        .forEach(
            row -> {
              for (var cell : row) {
                if (cell > 0) {
                  settlements.getAndIncrement();
                }
              }
            });
    log.info(
        "{} chunk at ({}, {}) has a density of {} and {} settlements",
        position,
        chunk.getWorldCoords().getFirst(),
        chunk.getWorldCoords().getSecond(),
        chunk.getDensity(),
        settlements);
  }
}
