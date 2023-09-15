package com.hindsight.king_of_castrop_rauxel.components;

import java.util.Random;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ChunkComponent {
  public static final int CHUNK_SIZE = 500;
  public static final int MIN_PLACEMENT_DISTANCE = 5;
  public static final int MAX_NEIGHBOUR_DISTANCE = 100;
  private static final Bounds chunkDensity = new Bounds(5, 10);

  public static Chunk generateChunk(Random random) {
    return new Chunk(random, randomDensity(random));
  }

  private static int randomDensity(Random random) {
    return random.nextInt(chunkDensity.getUpper() - chunkDensity.getLower() + 1)
        + chunkDensity.getLower();
  }
}
