package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.utils.NameGenerator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DungeonHandler {

  private final NameGenerator generators;
  private final PointOfInterest parentPoi;
  private final Location parentLocation;

  public DungeonHandler(PointOfInterest poi) {
    this.parentPoi = poi;
    this.parentLocation = poi.getParent();
    this.generators = parentLocation.getNameGenerator();
  }

  public DungeonDetails build() {
    log.info("Building dungeon...");
    // TODO:
    //  - Generate target level (from coordinates?)
    //  - Generate level range based on target level
    //  - Generate type (based on what though?)
    //  - Generate boss fight (by chance)
    //  - Generate encounters numbers
    return null;
  }
}
