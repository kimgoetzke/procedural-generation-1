package com.hindsight.king_of_castrop_rauxel.components;

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
}
