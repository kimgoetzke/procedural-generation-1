package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.action.EventAction;
import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.cli.CliComponent;
import com.hindsight.king_of_castrop_rauxel.event.Event;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, includeFieldNames = false)
public class Amenity extends AbstractAmenity {

  public static final String ABOUT = " about ";

  public Amenity(Type type, Npc npc, Location parent) {
    super(type, npc, parent);
    load();
    logResult();
  }

  @Override
  public void load() {
    LocationBuilder.throwIfRepeatedRequest(this, true);
    this.name =
        parent
            .getGenerators()
            .nameGenerator()
            .locationNameFrom(this.getClass(), this, parent.getSize(), parent.getName(), npc);
    setLoaded(true);
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

  @Override
  public void logResult() {
    log.info("Generated: {}", this);
  }
}
