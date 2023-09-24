package com.hindsight.king_of_castrop_rauxel.world;

import static com.hindsight.king_of_castrop_rauxel.configuration.AppProperties.*;
import static com.hindsight.king_of_castrop_rauxel.world.WorldBuildingComponent.*;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ChunkComponent {

  public static int randomDensity(Random random) {
    return random.nextInt(DENSITY.getUpper() - DENSITY.getLower() + 1) + DENSITY.getLower();
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
        "{} chunk at {} has a density of {} and {} settlements",
        position,
        chunk.getCoordinates(),
        chunk.getDensity(),
        settlements);
  }
}
