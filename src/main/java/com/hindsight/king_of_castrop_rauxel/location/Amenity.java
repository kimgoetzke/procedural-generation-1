package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.EventAction;
import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.event.Event;
import com.hindsight.king_of_castrop_rauxel.event.EventDetails;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true)
public class Amenity extends AbstractAmenity {

  public Amenity(PoiType type, Npc npc, Location parent) {
    super(type, npc, parent);
    load();
    logResult();
  }

  @Override
  public void load() {
    this.name =
        parent
            .getNameGenerator()
            .locationNameFrom(this, parent.getSize(), parent.getName(), npc, this.getClass());
    setLoaded(true);
  }

  /**
   * Generates the available actions for this amenity based on its type. This method must be called
   * after the amenity has been generated as the event, to which actions can point, is generated
   * after all amenities have been generated. This is because events can, e.g., point to other POIs.
   */
  @Override
  public void addAvailableAction(Event event) {
    var isPrimaryEvent = event.equals(npc.getPrimaryEvent());
    if (!isPrimaryEvent) {
      addSecondaryEvent(event.getEventDetails(), event);
      return;
    }
    switch (type) {
      case SHOP:
        addPrimaryEvent(", the owner of this establishment", event);
        break;
      case QUEST_LOCATION:
        addPrimaryEvent(", who appears to want something", event);
        break;
      default:
        break;
    }
  }

  private void addPrimaryEvent(String append, Event event) {
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

  private void addSecondaryEvent(EventDetails details, Event event) {
    var about = details.getAbout() == null ? "" : "about " + details.getAbout();
    availableActions.add(
        EventAction.builder()
            .name("Speak with %s %s".formatted(npc.getName(), about))
            .index(availableActions.size() + 1)
            .event(event)
            .npc(npc)
            .build());
  }

  @Override
  public void unload() {
    LocationBuilder.throwIfRepeatedRequest(this, true);
    random = new Random(seed);
    availableActions.clear();
    setLoaded(false);
    log.info("Unloaded: {}", this);
  }

  @Override
  public void logResult() {
    log.info("Generated: {}", this);
  }
}
