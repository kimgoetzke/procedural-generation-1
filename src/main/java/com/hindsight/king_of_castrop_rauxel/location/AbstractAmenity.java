package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.world.SeedComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class AbstractAmenity implements PointOfInterest, Generatable {

  @EqualsAndHashCode.Include @Getter protected final String id;
  @EqualsAndHashCode.Include @Getter protected final long seed;
  @ToString.Include @Getter @Setter protected String name;
  @ToString.Include protected final PoiType type;
  @Getter protected final List<Action> availableActions = new ArrayList<>();
  @Getter protected final Location parent;
  protected final Npc npc;
  @Getter @Setter protected String description;
  protected Random random;
  @Getter @Setter private boolean isLoaded;

  protected AbstractAmenity(PoiType type, Npc npc, Location parent) {
    this.id = "POI~" + UUID.randomUUID();
    this.seed = SeedComponent.seedFrom(parent.getCoordinates().getGlobal());
    this.random = new Random(seed);
    this.type = type;
    this.parent = parent;
    this.npc = npc;
    if (npc != null) {
      npc.setHome(this);
    }
  }

  @Override
  public String getSummary() {
    return "%s [ Type: %s | Located in %s ]".formatted(name, type, parent.getName());
  }

  public enum PoiType {
    ENTRANCE,
    MAIN_SQUARE,
    SHOP,
    QUEST_LOCATION,
  }
}
