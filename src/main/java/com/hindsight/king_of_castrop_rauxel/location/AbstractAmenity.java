package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.action.EventAction;
import com.hindsight.king_of_castrop_rauxel.character.Npc;
import com.hindsight.king_of_castrop_rauxel.cli.CliComponent;
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
@ToString(includeFieldNames = false, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class AbstractAmenity implements PointOfInterest, Generatable {

  public static final String ABOUT = " about ";

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
    if (type == Type.QUEST_LOCATION || type == Type.SHOP || type == Type.MAIN_SQUARE) {
      addEventAction(event);
    }
  }

  protected void addEventAction(Event event) {
    var action =
        EventAction.builder()
            .name("Speak with %s".formatted(npc.getName()))
            .index(availableActions.size() + 1)
            .event(event)
            .npc(npc)
            .build();
    availableActions.add(action);
  }

  @Override
  public List<Action> getAvailableActions() {
    var processedActions = new ArrayList<Action>();
    for (var action : availableActions) {
      if (!(action instanceof EventAction eventAction)) {
        processedActions.add(action);
        continue;
      }
      if (eventAction.getEvent().isDisplayable(npc)) {
        var processedAction = EventAction.from(eventAction);
        processEventActionName(processedAction);
        processedActions.add(processedAction);
      }
    }
    return processedActions;
  }

  private void processEventActionName(EventAction action) {
    var details = action.getEvent().getEventDetails();
    if (details.getEventType() == Event.Type.DIALOGUE) {
      action.setName(action.getName() + CliComponent.label(CliComponent.Type.DIALOGUE));
    } else {
      var isEventGiver = details.getEventGiver().equals(npc);
      var aboutText = isEventGiver ? details.getAboutGiver() : details.getAboutTarget();
      action.setName(getProcessedName(action, aboutText));
    }
  }

  private static String getProcessedName(EventAction action, String text) {
    return action.getName() + ABOUT + text + CliComponent.label(CliComponent.Type.QUEST);
  }
}
