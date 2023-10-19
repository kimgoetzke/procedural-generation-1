package com.hindsight.king_of_castrop_rauxel.action;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.cli.combat.EncounterSequence;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/** This action initiates an encounter by changing the state to IN_COMBAT and . */
@Getter
@Builder
public class CombatAction implements Action {

  @Setter private int index;
  @Setter private String name;
  private EncounterSequence sequence;

  @Override
  public void execute(Player player) {
    sequence.execute(player);
    nextState(player);
    throw new IllegalStateException("CombatAction has not been implemented yet.");
  }

  @Override
  public Player.State getNextState() {
    return Player.State.IN_COMBAT;
  }
}
