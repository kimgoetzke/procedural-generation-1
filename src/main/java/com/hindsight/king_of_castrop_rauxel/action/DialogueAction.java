package com.hindsight.king_of_castrop_rauxel.action;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.event.Event;
import lombok.*;

/** This action is used in dialogue and allows branching logic. */
@Getter
@Builder
@ToString
public class DialogueAction implements Action {

  @EqualsAndHashCode.Exclude @Setter private int index;
  @Setter private String name;
  private Event.State eventState;
  private Player.State playerState;
  private Integer nextInteraction;

  @Override
  public void execute(Player player) {
    if (playerState == null && eventState == null) {
      throw new IllegalStateException("DialogueAction must have a playerState or eventState");
    }
    if (playerState != null) {
      player.setState(playerState);
    }
    if (eventState == Event.State.NONE && nextInteraction == null) {
      throw new IllegalStateException("DialogueAction must have an eventState or nextInteraction");
    }
    if (eventState != null) {
      switch (eventState) {
          // Subtract 1 because the dialogue will progress after this action is executed.
        case NONE -> player.getCurrentEvent().setCurrentInteraction(nextInteraction - 1);
        case COMPLETED -> player.getCurrentEvent().completeEvent(player);
        default -> player.getCurrentEvent().progressEvent(eventState);
      }
    }
  }

  @Override
  public Player.State getNextState() {
    return Player.State.IN_DIALOGUE;
  }
}
