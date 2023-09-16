package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true)
public class Amenity extends AbstractAmenity {

  public Amenity(PoiType type, Npc npc, Location parent) {
    super(type, npc, parent);
    generate();
    logResult();
  }

  @Override
  public void generate() {
    this.name =
        parent
            .getStringGenerator()
            .locationNameFrom(this, parent.getSize(), parent.getName(), npc, this.getClass());
  }

  @Override
  public void logResult() {
    log.info("Generated: {}", this);
  }
}
