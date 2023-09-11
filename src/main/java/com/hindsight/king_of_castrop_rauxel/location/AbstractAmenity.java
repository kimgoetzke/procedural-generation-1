package com.hindsight.king_of_castrop_rauxel.location;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString(callSuper = true, exclude = {"settlement"})
@Getter
public abstract class AbstractAmenity extends AbstractLocation {

  protected final AmenityType type;
  protected final AbstractSettlement settlement;

  protected AbstractAmenity(AmenityType type, AbstractSettlement settlement) {
    super();
    this.type = type;
    this.settlement = settlement;
  }

  @Override
  public String getSummary() {
    return "%s [ Type: %s ]".formatted(name, type);
  }

  public enum AmenityType {
    ENTRANCE,
    MAIN_SQUARE,
    SHOP,
    QUEST_LOCATION,
  }
}
