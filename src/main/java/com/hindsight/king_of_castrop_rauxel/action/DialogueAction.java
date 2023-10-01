package com.hindsight.king_of_castrop_rauxel.action;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.event.Event;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/** This action is used in dialogue and allows branching logic. */
@Getter
@Builder
public class DialogueAction implements Action {

  @Setter private int index;
  private String name;
  private Event.EventChoice choice;
  private int nextInteraction;

  @Override
  public void execute(Player player) {
    switch (choice) {
      case PENDING -> {}
      case ACCEPT -> player.getCurrentEvent().setState(Event.EventState.ACTIVE);
      case DECLINE -> player.getCurrentEvent().setState(Event.EventState.DECLINED);
    }
  }

  @Override
  public Player.PlayerState getNextState() {
    return Player.PlayerState.EVENT;
  }
}
