package com.hindsight.king_of_castrop_rauxel.action;

import static com.hindsight.king_of_castrop_rauxel.characters.Player.*;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/** The only purpose of this action is to exit the game when playing via the CLI. */
@Getter
@Builder
public class ExitAction implements Action {

  @Setter private int index;
  private String name;
  private static final State NEXT_STATE = State.AT_DEFAULT_POI;

  @Override
  public void execute(Player player) {
    setPlayerState(player);
    System.out.printf("Goodbye!%n%n");
    System.exit(0);
  }

  public State getNextState() {
    return NEXT_STATE;
  }
}
