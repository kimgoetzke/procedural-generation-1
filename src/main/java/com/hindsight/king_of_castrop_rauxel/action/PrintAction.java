package com.hindsight.king_of_castrop_rauxel.action;

import static com.hindsight.king_of_castrop_rauxel.characters.Player.*;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.cli.CliComponent;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * This action is a CLI-only action and it prints the . It will not be displayed in a web
 * environment.
 */
@Getter
@Builder
public class PrintAction implements Action {

  @Setter private int index;
  @Setter private String name;
  private static final State NEXT_STATE = State.IN_MENU;

  String header;
  Object toPrint;

  @Override
  public void execute(Player player) {
    System.out.println(header);
    System.out.printf("%n%s%n", toPrint.toString());
    CliComponent.awaitEnterKeyPress();
    nextState(player);
  }

  public State getNextState() {
    return NEXT_STATE;
  }
}
