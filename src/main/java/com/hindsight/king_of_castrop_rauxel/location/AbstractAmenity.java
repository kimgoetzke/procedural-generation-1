package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@ToString(callSuper = true, exclude = {"settlement", "npc"})
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public abstract class AbstractAmenity extends AbstractLocation {

  protected final AmenityType type;
  protected final AbstractSettlement settlement;
  protected final Npc npc;

  protected AbstractAmenity(AmenityType type, Npc npc, AbstractSettlement settlement) {
    super();
    this.type = type;
    this.settlement = settlement;
    this.npc = npc;
    if (npc != null) {
      npc.setHome(this);
    }
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
