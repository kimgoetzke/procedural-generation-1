package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.action.EventAction;
import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.event.Event;
import com.hindsight.king_of_castrop_rauxel.world.Generatable;
import com.hindsight.king_of_castrop_rauxel.world.IdBuilder;
import com.hindsight.king_of_castrop_rauxel.world.SeedBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class AbstractAmenity implements PointOfInterest, Generatable {

  @Getter(AccessLevel.NONE)
  protected final List<Action> availableActions = new ArrayList<>();

  @EqualsAndHashCode.Include protected final String id;
  @EqualsAndHashCode.Include protected final long seed;
  @ToString.Include protected final Type type;
  protected final Location parent;
  protected final Npc npc;
  @ToString.Include @Setter protected String name;
  @Setter protected String description;
  @Setter private boolean isLoaded;
  protected Random random;

  protected AbstractAmenity(Type type, Npc npc, Location parent) {
    this.id = IdBuilder.idFrom(this.getClass());
    this.seed = SeedBuilder.seedFrom(parent.getCoordinates().getGlobal());
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

  @Override
  public void addAvailableAction(Event event) {
    var isPrimaryEvent = event.equals(npc.getPrimaryEvent());
    if (!isPrimaryEvent) {
      var about = event.getEventDetails().getAbout();
      var appendAbout = about == null ? "" : " about " + about;
      addEventAction(event, appendAbout);
      return;
    }
    if (type == Type.QUEST_LOCATION || type == Type.SHOP) {
      addEventAction(event, "");
    }
  }

  protected void addEventAction(Event event, String append) {
    var action =
        EventAction.builder()
            .name("Speak with %s%s".formatted(npc.getName(), append))
            .index(availableActions.size() + 1)
            .event(event)
            .npc(npc)
            .build();
    if (availableActions.stream().anyMatch(a -> a.getName().equals(action.getName()))) {
      throw new IllegalStateException(
          "Duplicate action '%s' for event '%s'".formatted(action, event));
    }
    availableActions.add(action);
  }
}
