package com.hindsight.king_of_castrop_rauxel.action;

import static com.google.common.base.Preconditions.checkArgument;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.event.Event;
import lombok.*;

/** This action is used in dialogue and allows branching logic. */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DialogueAction implements Action {

  @EqualsAndHashCode.Exclude private int index;
  private String name;
  private Event.State eventState;
  private Player.State playerState;
  private Integer nextInteraction;

  @Override
  public void execute(Player player) {
    var areBothNull = playerState == null && eventState == null;
    checkArgument(!areBothNull, "DialogueAction must have a playerState or eventState");
    if (playerState != null) {
      player.setState(playerState);
    }
    var isMisconfigured = eventState == Event.State.NONE && nextInteraction == null;
    checkArgument(!isMisconfigured, "DialogueAction with State.NONE must have nextInteraction");
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
