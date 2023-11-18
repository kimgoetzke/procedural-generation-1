package com.hindsight.king_of_castrop_rauxel.world;

import lombok.Getter;

@Getter
public enum CardinalDirection {
  THIS("This", 0),
  NORTH("North", 1),
  NORTH_EAST("North-east", 2),
  EAST("East", 3),
  SOUTH_EAST("South-east", 4),
  SOUTH("South", 5),
  SOUTH_WEST("South-west", 6),
  WEST("West", 7),
  NORTH_WEST("North-west", 8);

  private final String name;
  private final int ordinal;

  CardinalDirection(String name, int ordinal) {
    this.name = name;
    this.ordinal = ordinal;
  }
}
