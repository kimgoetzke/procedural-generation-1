package com.hindsight.king_of_castrop_rauxel.settings;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SeedComponent {

  private static long seed = 1234L;
  private static Random random = new Random(seed);

  public static void changeSeed(long seed) {
    SeedComponent.seed = seed;
    random = new Random(seed);
  }

  public static long seedFrom(Pair<Integer, Integer> coordinates) {
    return seed + coordinates.getFirst() + coordinates.getSecond();
  }

  /**
   * This instance is used for the generation of nodes and edges. It is not used for the generation
   * of settlements, their amenities and names, which is done through a new instance of Random.
   */
  public static Random getInstance() {
    return random;
  }
}
