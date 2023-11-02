package com.hindsight.king_of_castrop_rauxel.world;

import static com.hindsight.king_of_castrop_rauxel.configuration.AppConstants.LEVEL_TO_TIER_DIVIDER;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Random;

@Getter
@Setter
@AllArgsConstructor
public class Range {

  /** Base multiplier of the current level upon which the modifier will be applied. */
  private float multiplier;

  /** Inclusive lower bounds. */
  private float minMod;

  /** Exclusive upper bounds. */
  private float maxMod;

  public Bounds toBounds(int level) {
    var upper = level * multiplier * maxMod;
    var lower = level * multiplier * minMod;
    return new Bounds((int) lower, (int) upper);
  }

  public int toRandomActual(Random random, int targetLevel) {
    var upper = targetLevel * multiplier * maxMod;
    var lower = targetLevel * multiplier * minMod;
    var randomOffset = random.nextFloat(upper - lower);
    return toActual(targetLevel, upper, lower, randomOffset);
  }

  private int toActual(int targetLevel, float upper, float lower, float offset) {
    var modulus = targetLevel % LEVEL_TO_TIER_DIVIDER;
    var modifier = 1 - (1 / modulus);
    return (int) ((upper - lower) * modifier) + (int) (lower + offset);
  }

  @Override
  public String toString() {
    return multiplier + " x " + minMod + "-" + maxMod;
  }
}
