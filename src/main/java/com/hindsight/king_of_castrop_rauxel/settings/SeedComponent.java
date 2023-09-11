package com.hindsight.king_of_castrop_rauxel.settings;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SeedComponent {

  @Getter
  private static long seed = 1234L;
  private static Random random = new Random(seed);

  public static void setSeed(long seed) {
    SeedComponent.seed = seed;
    random = new Random(seed);
  }

  public static Random getSettlementInstance() {
    return random;
  }
}
