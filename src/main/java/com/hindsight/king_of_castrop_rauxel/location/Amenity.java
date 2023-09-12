package com.hindsight.king_of_castrop_rauxel.location;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Amenity extends AbstractAmenity {

  public Amenity(AmenityType type, AbstractSettlement settlement) {
    super(type, settlement);
    generate();
    logResult();
  }

  @Override
  public void generate() {
    this.name = settlement.stringGenerator.locationNameFrom(
      this,
      settlement.size,
      settlement.getName(),
      settlement.getInhabitants(),
      this.getClass());
  }

  @Override
  public void generate(String parentName) {
    generate();
  }

  @Override
  public void logResult() {
    log.info("Generated: {}", this);
  }
}
