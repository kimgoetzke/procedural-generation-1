package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.LocationAction;
import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.components.SeedComponent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Getter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class AbstractAmenity implements PointOfInterest, Generatable {

  @EqualsAndHashCode.Include @Getter protected final String id;
  @EqualsAndHashCode.Include @Getter protected final long seed;
  @ToString.Include @Getter @Setter protected String name;
  @Getter @Setter protected String description;
  @ToString.Include protected final PoiType type;
  protected final AbstractSettlement settlement;
  protected final Npc npc;
  @Getter protected final List<LocationAction> availableActions = new ArrayList<>();
  protected final Random random;

  protected AbstractAmenity(PoiType type, Npc npc, AbstractSettlement settlement) {
    this.id = UUID.randomUUID().toString();
    this.seed = SeedComponent.seedFrom(settlement.getCoordinates());
    this.random = new Random(seed);
    this.type = type;
    this.settlement = settlement;
    this.npc = npc;
    if (npc != null) {
      npc.setHome(this);
    }
  }

  @Override
  public String getSummary() {
    return "%s [ Type: %s | Located in %s ]".formatted(name, type, settlement.getName());
  }

  public enum PoiType {
    ENTRANCE,
    MAIN_SQUARE,
    SHOP,
    QUEST_LOCATION,
  }
}
