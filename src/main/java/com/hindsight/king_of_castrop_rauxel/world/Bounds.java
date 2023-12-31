package com.hindsight.king_of_castrop_rauxel.world;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Bounds {

  /** Inclusive lower bounds. */
  private int lower;

  /** Inclusive upper bounds. */
  private int upper;

  @Override
  public String toString() {
    return lower + "-" + upper;
  }

  public static Bounds of(int lower, int upper) {
    return new Bounds(lower, upper);
  }
}
