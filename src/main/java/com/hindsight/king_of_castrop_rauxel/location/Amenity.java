package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.utils.BasicStringGenerator;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import static com.hindsight.king_of_castrop_rauxel.settings.LocationComponent.*;

@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString(
    callSuper = true,
    exclude = {"settlement"})
public class Amenity extends AbstractAmenity {

  private final AbstractSettlement settlement;

  public Amenity(AmenityType type, AbstractSettlement settlement) {
    this.type = type;
    this.settlement = settlement;
    generate();
    logResult();
  }

  @Override
  public void generate() {
    this.name =
        settlement == null
            ? BasicStringGenerator.locationNameFrom(type, this.getClass())
            : BasicStringGenerator.locationNameFrom(
                type,
                settlement.size,
                settlement.getName(),
                settlement.getInhabitants(),
                this.getClass());
  }

  @Override
  public void generate(String parentName) {
    this.name =
        parentName == null
            ? BasicStringGenerator.locationNameFrom(this.getClass())
            : BasicStringGenerator.locationNameFrom(parentName, this.getClass());
  }

  @Override
  public void logResult() {
    log.info("Generated: {}", this);
  }
}
