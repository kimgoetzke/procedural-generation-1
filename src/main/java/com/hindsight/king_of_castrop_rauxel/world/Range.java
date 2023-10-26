package com.hindsight.king_of_castrop_rauxel.world;

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

  /** Inclusive upper bounds. */
  private float maxMod;

  @Override
  public String toString() {
    return multiplier + " x " + minMod + "-" + maxMod;
  }
}
