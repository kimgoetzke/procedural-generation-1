package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, includeFieldNames = false)
public class Amenity extends AbstractAmenity {

  public Amenity(Type type, Npc npc, Location parent) {
    super(type, npc, parent);
    load();
    logResult();
  }

  @Override
  public void load() {
    if (isLoaded()) {
      log.info("Requested to load '{}' but it already is", getId());
      return;
    }
    this.name =
        parent
            .getGenerators()
            .nameGenerator()
            .locationNameFrom(this.getClass(), this, parent.getSize(), parent.getName(), npc);
    setLoaded(true);
  }

  @Override
  public void logResult() {
    log.info("Generated: {}", this);
  }
}
