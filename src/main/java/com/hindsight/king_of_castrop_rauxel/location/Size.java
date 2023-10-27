package com.hindsight.king_of_castrop_rauxel.location;

import lombok.Getter;

@Getter
public enum Size {
  XS("Very small", 0),
  S("Small", 1),
  M("Medium", 2),
  L("Large", 3),
  XL("Very large", 4);

  private final String name;
  private final int ordinal;

  Size(String s, int i) {
    this.name = s;
    this.ordinal = i;
  }
}
