package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.utils.BasicStringGenerator;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import static com.hindsight.king_of_castrop_rauxel.settings.LocationComponent.*;

@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = {"settlementSize", "settlementName"})
public class Amenity extends AbstractAmenity {

  private final Size settlementSize;
  private final String settlementName;

  public Amenity(AmenityType type, Size settlementSize, String settlementName) {
    this.type = type;
    this.settlementSize = settlementSize;
    this.settlementName = settlementName;
    generate();
    logResult();
  }

  @Override
  public void generate() {
    this.name =
      settlementName == null
        ? BasicStringGenerator.generate(type, this.getClass())
        : BasicStringGenerator.generate(type, settlementSize, settlementName, this.getClass());
  }

  @Override
  public void generate(String parentName) {
    this.name =
      parentName == null
        ? BasicStringGenerator.generate(this.getClass())
        : BasicStringGenerator.generate(parentName, this.getClass());
  }

  @Override
  public void logResult() {
    log.info("Generated: {}", this);
  }
}
