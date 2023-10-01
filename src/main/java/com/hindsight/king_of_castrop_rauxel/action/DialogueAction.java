package com.hindsight.king_of_castrop_rauxel.action;

import static com.hindsight.king_of_castrop_rauxel.characters.Player.*;
import static com.hindsight.king_of_castrop_rauxel.event.Event.*;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.event.Event;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * This action initiates an event by changing the state to EVENT and setting the current event on
 * the player.
 */
@Getter
@Builder
public class DialogueAction implements Action {

  @Setter private int index;
  private String name;
  private Event event;

  @Override
  public void execute(Player player) {
    if (!player.getEvents().contains(event)
        || (event.isRepeatable() && event.getState() == EventState.AVAILABLE)) {
      player.addEvent(event);
      player.setCurrentEvent(event);
      player.setState(PlayerState.EVENT);
      event.setState(EventState.ACTIVE);
      return;
    }
    event.progress();
    if (!event.hasNext()) {
      player.setCurrentEvent(null);
      event.setState(EventState.COMPLETED);
      nextState(player);
    }
  }

  @Override
  public PlayerState getNextState() {
    return PlayerState.AT_SPECIFIC_POI;
  }
}
