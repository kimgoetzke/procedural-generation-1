package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.settings.LocationComponent;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString(callSuper = true)
@Getter
public abstract class AbstractAmenity extends AbstractLocation {

  protected LocationComponent.AmenityType type;

  protected AbstractAmenity() {
    super();
  }

  @Override
  public String getSummary() {
    return "%s [ Type: %s ]".formatted(name, type);
  }
}
