package com.hindsight.king_of_castrop_rauxel.action;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.event.Event;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** This action is used in dialogue and allows branching logic. */
@Getter
@Builder
@ToString
public class DialogueAction implements Action {

  @Setter private int index;
  private String name;
  private Event.EventChoice choice;
  private int nextInteraction;

  @Override
  public void execute(Player player) {
    switch (choice) {
      case ACCEPT -> player.getCurrentEvent().setState(Event.EventState.ACTIVE);
      case DECLINE -> player.getCurrentEvent().setState(Event.EventState.DECLINED);
    }
    // Subtract 1 because the dialogue will progress after this action is executed.
    nextInteraction = nextInteraction - 1;
    player.getCurrentEvent().setCurrentInteraction(nextInteraction);
  }

  @Override
  public Player.PlayerState getNextState() {
    return Player.PlayerState.DIALOGUE;
  }
}
