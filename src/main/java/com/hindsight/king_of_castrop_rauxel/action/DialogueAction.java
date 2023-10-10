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
      switch (playerState) {
        case AT_LOCATION -> player.setState(Player.State.AT_LOCATION);
        case CHOOSE_POI -> player.setState(Player.State.CHOOSE_POI);
        case AT_POI -> player.setState(Player.State.AT_POI);
        case DIALOGUE -> player.setState(Player.State.DIALOGUE);
        case DEBUG -> player.setState(Player.State.DEBUG);
      }
    }
    if (eventState == Event.State.NONE && nextInteraction == null) {
      throw new IllegalStateException("DialogueAction must have an eventState or nextInteraction");
    }
    if (eventState != null) {
      switch (eventState) {
          // Subtract 1 because the dialogue will progress after this action is executed.
        case NONE -> player.getCurrentEvent().setCurrentInteraction(nextInteraction - 1);
        case AVAILABLE -> player.getCurrentEvent().progressEvent(Event.State.AVAILABLE);
        case ACTIVE -> player.getCurrentEvent().progressEvent(Event.State.ACTIVE);
        case READY -> player.getCurrentEvent().progressEvent(Event.State.READY);
        case COMPLETED -> player.getCurrentEvent().completeEvent(player);
        case DECLINED -> player.getCurrentEvent().progressEvent(Event.State.DECLINED);
      }
    }
  }

  @Override
  public Player.State getNextState() {
    return Player.State.DIALOGUE;
  }
}
