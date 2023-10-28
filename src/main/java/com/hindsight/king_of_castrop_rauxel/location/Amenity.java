package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.action.EventAction;
import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import java.util.ArrayList;
import java.util.List;

import com.hindsight.king_of_castrop_rauxel.cli.CliComponent;
import com.hindsight.king_of_castrop_rauxel.event.Event;
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
            .locationNameFrom(this, parent.getSize(), parent.getName(), npc, this.getClass());
    setLoaded(true);
  }

  @Override
  public List<Action> getAvailableActions() {
    var processedActions = new ArrayList<Action>();
    for (var action : availableActions) {
      if (action instanceof EventAction eventAction) {
        if (!eventAction.getEvent().isDisplayable(npc)) {
          continue;
        }
        var details = eventAction.getEvent().getEventDetails();
        if (details.getEventType() == Event.Type.DIALOGUE) {
          // Append something to the action name to indicate that it's a dialogue
          action.setName(action.getName() + CliComponent.label("Dialogue", CliComponent.FMT.BLUE));
        } else {
          if (details.getEventGiver().equals(npc)) {
            action.setName(
                action.getName()
                    + ABOUT
                    + details.getAboutGiver()
                    + CliComponent.label("Quest", CliComponent.FMT.BLUE));
          } else {
            action.setName(
                action.getName()
                    + ABOUT
                    + details.getAboutTarget()
                    + CliComponent.label("Quest", CliComponent.FMT.BLUE));
          }
        }
      }
      processedActions.add(action);
    }
    return processedActions;
  }

  @Override
  public void logResult() {
    log.info("Generated: {}", this);
  }
}
