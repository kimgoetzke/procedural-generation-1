package com.hindsight.king_of_castrop_rauxel.world;

import java.util.Random;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ChunkComponent {

  public static final int CHUNK_SIZE = 500;
  public static final int MIN_PLACEMENT_DISTANCE = 5;
  public static final int MAX_NEIGHBOUR_DISTANCE = 100;
  public static final int GENERATION_TRIGGER_ZONE = 100;
  private static final Bounds chunkDensity = new Bounds(5, 10);

  public static Chunk generateChunk(Random random) {
    return new Chunk(random, randomDensity(random));
  }

  private static int randomDensity(Random random) {
    return random.nextInt(chunkDensity.getUpper() - chunkDensity.getLower() + 1)
        + chunkDensity.getLower();
  }

  public static boolean isInsideTriggerZone(Pair<Integer, Integer> coordinates) {
    return coordinates.getFirst() > CHUNK_SIZE - GENERATION_TRIGGER_ZONE
        || coordinates.getSecond() > CHUNK_SIZE - GENERATION_TRIGGER_ZONE;
  }

  public static int getClosestEdgeDistance(Pair<Integer, Integer> coordinates) {
    return Math.min(
        Math.min(coordinates.getFirst(), CHUNK_SIZE - coordinates.getFirst()),
        Math.min(coordinates.getSecond(), CHUNK_SIZE - coordinates.getSecond()));
  }
}
