package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.utils.BasicStringGenerator;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import static com.hindsight.king_of_castrop_rauxel.settings.LocationComponent.*;

@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Amenity extends AbstractAmenity {

  public Amenity(String parentName) {
    generate(parentName);
    logResult();
  }

  public Amenity(AmenityType type, String parentName) {
    this.type = type;
    generate(type, parentName);
    logResult();
  }

  @Override
  public void generate() {
    generate(null);
  }

  @Override
  public void generate(String parentName) {
    this.name =
      parentName == null
        ? BasicStringGenerator.generate(this.getClass())
        : BasicStringGenerator.generate(parentName, this.getClass());
  }

  public void generate(AmenityType type, String parentName) {
    this.name =
      parentName == null
        ? BasicStringGenerator.generate(type, this.getClass())
        : BasicStringGenerator.generate(type, parentName, this.getClass());
  }

  @Override
  public void logResult() {
    log.info("Generated: {}", this);
  }
}
