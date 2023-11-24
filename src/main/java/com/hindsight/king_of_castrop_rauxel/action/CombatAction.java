package com.hindsight.king_of_castrop_rauxel.action;

import static com.hindsight.king_of_castrop_rauxel.configuration.EnvironmentResolver.*;

import com.hindsight.king_of_castrop_rauxel.character.Player;
import com.hindsight.king_of_castrop_rauxel.encounter.EncounterSequence;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/** This action initiates an encounter by changing the state to IN_COMBAT and . */
@Getter
@Builder
public class CombatAction implements Action {

  @Setter private Environment environment;
  @Setter private int index;
  @Setter private String name;
  private EncounterSequence sequence;

  @Override
  public void execute(Player player) {
    nextState(player);
    sequence.execute(player);
  }

  @Override
  public Player.State getNextState() {
    return Player.State.IN_COMBAT;
  }
}
