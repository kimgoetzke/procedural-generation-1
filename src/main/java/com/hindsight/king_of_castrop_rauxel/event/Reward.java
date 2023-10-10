package com.hindsight.king_of_castrop_rauxel.event;

import lombok.Builder;

@Builder
public class Reward {

  private Type type;
  private int minValue;
  private int maxValue;
  private int value;

  public enum Type {
    GOLD,
    EXPERIENCE,
    ITEM
  }
}
