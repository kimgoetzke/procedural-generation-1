package com.hindsight.king_of_castrop_rauxel.encounter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Random;

@Getter
@Setter
@AllArgsConstructor
public class Damage {

  /** Inclusive lower bounds. */
  private int min;

  /** Inclusive upper bounds. */
  private int max;

  public static Damage of(int lower, int upper) {
    return new Damage(lower, upper);
  }

  public int actual(Random random) {
    return random.nextInt(max - min + 1) + min;
  }

  @Override
  public String toString() {
    return min + "-" + max;
  }
}
