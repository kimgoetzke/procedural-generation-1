package com.hindsight.king_of_castrop_rauxel.action;

import static com.hindsight.king_of_castrop_rauxel.character.Player.*;
import static com.hindsight.king_of_castrop_rauxel.configuration.EnvironmentResolver.*;

import com.hindsight.king_of_castrop_rauxel.character.Player;
import com.hindsight.king_of_castrop_rauxel.cli.CliComponent;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * This action is a CLI-only action and it prints the . It will not be displayed in a web
 * environment.
 */
@Getter
@Builder
public class PrintAction<T> implements Action {

  @Setter private Environment environment;
  @Setter private int index;
  @Setter private String name;
  private static final State NEXT_STATE = State.IN_MENU;

  String header;
  List<T> toPrint;

  @Override
  public void execute(Player player) {
    System.out.println();
    System.out.println(header);
    if (toPrint.isEmpty()) {
      System.out.println("None.");
    } else {
      toPrint.forEach(element -> System.out.println(" - " + element));
    }
    System.out.println();
    CliComponent.awaitEnterKeyPress();
    nextState(player);
  }

  public State getNextState() {
    return NEXT_STATE;
  }
}
