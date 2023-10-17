package com.hindsight.king_of_castrop_rauxel.action;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.event.Event;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/** This action initiates an encounter by changing the state to IN_COMBAT and . */
@Getter
@Builder
public class CombatAction implements Action {

  @Setter private int index;
  @Setter private String name;
  private Event event;
  private PointOfInterest poi;

  @Override
  public void execute(Player player) {
    nextState(player);
    throw new IllegalStateException("CombatAction has not been implemented yet.");
  }

  @Override
  public Player.State getNextState() {
    return Player.State.IN_COMBAT;
  }
}
