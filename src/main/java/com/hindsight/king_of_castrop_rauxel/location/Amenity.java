package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.action.EventAction;
import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.event.Event;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true)
public class Amenity extends AbstractAmenity {

  public Amenity(Type type, Npc npc, Location parent) {
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

  @Override
  public List<Action> getAvailableActions() {
    var processedActions = new ArrayList<Action>();
    for (var action : availableActions) {
      if (action instanceof EventAction eventAction && !eventAction.getEvent().isDisplayable(npc)) {
        continue;
      }
      processedActions.add(action);
    }
    return processedActions;
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
    switch (type) {
      case SHOP:
        addEventAction(event, ", the owner of this establishment");
        break;
      case QUEST_LOCATION:
        addEventAction(event, ", who appears to want something");
        break;
      default:
        break;
    }
  }

  private void addEventAction(Event event, String append) {
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
