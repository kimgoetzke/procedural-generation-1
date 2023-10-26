package com.hindsight.king_of_castrop_rauxel.world;

import static com.hindsight.king_of_castrop_rauxel.configuration.AppConstants.DUNGEON_TIER_DIVIDER;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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

  public int toActual(int targetLevel) {
    var modulus = targetLevel % DUNGEON_TIER_DIVIDER;
    var modifier = 1 / modulus;
    var upper = targetLevel * multiplier * maxMod;
    var lower = targetLevel * multiplier * minMod;
    return (int) ((upper - lower) * modifier) + (int) lower;
  }

  @Override
  public String toString() {
    return multiplier + " x " + minMod + "-" + maxMod;
  }
}
