package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true)
public class Amenity extends AbstractAmenity {

  public Amenity(AmenityType type, Npc npc, AbstractSettlement settlement) {
    super(type, npc, settlement);
    generate();
    logResult();
  }

  @Override
  public void generate() {
    setCoordinatesWithin(settlement);
    this.name =
        settlement.stringGenerator.locationNameFrom(
            this, settlement.size, settlement.getName(), npc, this.getClass());
  }

  // TODO: Replace with random coordinates inside the settlement
  private void setCoordinatesWithin(AbstractSettlement settlement) {
    setCoordinates(settlement.getCoordinates());
  }

  @Override
  public void logResult() {
    log.info("Generated: {}", this);
  }
}
