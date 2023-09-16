package com.hindsight.king_of_castrop_rauxel.action;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.hindsight.king_of_castrop_rauxel.characters.Player.*;

@Getter
@Builder
public class ExitAction implements Action {

  @Setter private int index;
  private String name;
  private static final State NEXT_STATE = State.AT_DEFAULT_POI;

  @Override
  public void execute(Player player, List<Action> actions) {
    setPlayerState(player);
    System.out.printf("Goodbye!%n%n");
    System.exit(0);
  }

  public State getNextState() {
    return NEXT_STATE;
  }
}
