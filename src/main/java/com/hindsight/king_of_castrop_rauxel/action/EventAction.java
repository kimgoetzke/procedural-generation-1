package com.hindsight.king_of_castrop_rauxel.action;

import static com.hindsight.king_of_castrop_rauxel.characters.Player.*;
import static com.hindsight.king_of_castrop_rauxel.event.Event.*;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.event.Event;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * This action initiates a dialogue by changing the state to DIALOGUE and setting the current event
 * on the player.
 */
@Getter
@Builder
public class EventAction implements Action {

  @Setter private int index;
  private String name;
  private Event event;

  @Override
  public void execute(Player player) {
    if (!player.getEvents().contains(event)
        || (event.isRepeatable() && event.getState() == EventState.AVAILABLE)) {
      player.addEvent(event);
      player.setCurrentEvent(event);
      nextState(player);
    }
  }

  @Override
  public PlayerState getNextState() {
    return PlayerState.DIALOGUE;
  }
}
