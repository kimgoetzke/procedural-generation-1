package com.hindsight.king_of_castrop_rauxel.configuration;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class AppConstants {

  public static final int LEVEL_TO_TIER_DIVIDER = 10;

  /**
   * The delay in milliseconds between each step. Currently used when displaying each action in an
   * encounter.
   */
  public static final long DELAY_IN_MS = 175;

  /**
   * The speed modifier for the progress bar. The higher the value, the slower the progress.
   * Examples: A modifier of 1 means that it takes 10 seconds to travel a distance of 100 km. A
   * modifier of 0.5 means that it takes 5 seconds to travel a distance of 100 km.
   */
  public static final float SPEED_MODIFIER = 0.1F;

  // WORLD PROPERTIES
  public static final int WORLD_SIZE = 50;

  // CHUNK PROPERTIES
  public static final int CHUNK_SIZE = 500;
}
