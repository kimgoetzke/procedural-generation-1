package com.hindsight.king_of_castrop_rauxel.world;

import static com.hindsight.king_of_castrop_rauxel.configuration.AppConstants.*;
import static com.hindsight.king_of_castrop_rauxel.world.WorldHandler.*;

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
public class ChunkBuilder {

  public static int randomDensity(Random random) {
    return random.nextInt(DENSITY.getUpper() - DENSITY.getLower() + 1) + DENSITY.getLower();
  }

  public static boolean isInsideTriggerZone(Pair<Integer, Integer> chunkCoords) {
    return chunkCoords.getFirst() > CHUNK_SIZE - GENERATION_TRIGGER_ZONE
        || chunkCoords.getFirst() < GENERATION_TRIGGER_ZONE
        || chunkCoords.getSecond() > CHUNK_SIZE - GENERATION_TRIGGER_ZONE
        || chunkCoords.getSecond() < GENERATION_TRIGGER_ZONE;
  }

  // TODO: Expand to include all 8 directions
  public static CardinalDirection nextChunkPosition(Pair<Integer, Integer> chunkCoords) {
    if (chunkCoords.getFirst() > CHUNK_SIZE - GENERATION_TRIGGER_ZONE) {
      return CardinalDirection.EAST;
    } else if (chunkCoords.getFirst() < GENERATION_TRIGGER_ZONE) {
      return CardinalDirection.WEST;
    } else if (chunkCoords.getSecond() > CHUNK_SIZE - GENERATION_TRIGGER_ZONE) {
      return CardinalDirection.NORTH;
    } else if (chunkCoords.getSecond() < GENERATION_TRIGGER_ZONE) {
      return CardinalDirection.SOUTH;
    } else {
      return CardinalDirection.THIS;
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
