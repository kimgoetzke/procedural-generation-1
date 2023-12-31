package com.hindsight.king_of_castrop_rauxel.action;

import static com.hindsight.king_of_castrop_rauxel.configuration.EnvironmentResolver.*;

import com.hindsight.king_of_castrop_rauxel.character.Npc;
import com.hindsight.king_of_castrop_rauxel.character.Player;
import com.hindsight.king_of_castrop_rauxel.event.Event;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Initiates a dialogue by changing the state to IN_DIALOGUE and setting the current event on the
 * player.
 */
@Getter
@Builder
public class EventAction implements Action {

  @Setter private Environment environment;
  @Setter private int index;
  @Setter private String name;
  private Event event;
  private Npc npc;

  public static EventAction from(EventAction action) {
    return EventAction.builder()
        .environment(action.getEnvironment())
        .index(action.getIndex())
        .name(action.getName())
        .event(action.getEvent())
        .npc(action.getNpc())
        .build();
  }

  @Override
  public void execute(Player player) {
    if (!player.getEvents().contains(event)
        || (event.isRepeatable() && event.getEventState() == Event.State.AVAILABLE)) {
      player.addEvent(event);
    }
    event.setActive(npc);
    player.setCurrentEvent(event);
    nextState(player);
  }

  @Override
  public Player.State getNextState() {
    return Player.State.IN_DIALOGUE;
  }
}
