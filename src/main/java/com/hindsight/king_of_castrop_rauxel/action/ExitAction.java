package com.hindsight.king_of_castrop_rauxel.action;

import static com.hindsight.king_of_castrop_rauxel.character.Player.*;
import static com.hindsight.king_of_castrop_rauxel.configuration.EnvironmentResolver.*;

import com.hindsight.king_of_castrop_rauxel.character.Player;
import com.hindsight.king_of_castrop_rauxel.web.exception.GenericWebException;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * The only purpose of this action is to exit the game when playing via the CLI. It will not be
 * displayed in a web environment.
 */
@Getter
@Builder
public class ExitAction implements Action {

  @Setter private Environment environment;
  @Setter private int index;
  @Setter private String name;
  private static final State NEXT_STATE = State.AT_POI;

  @Override
  public void execute(Player player) {
    switch (environment) {
      case CLI -> executeCli(player);
      case WEB -> executeWeb();
    }
  }

  private void executeCli(Player player) {
    nextState(player);
    System.out.printf("Goodbye!%n%n");
    System.exit(0);
  }

  private void executeWeb() {
    throw new GenericWebException("User logged out", HttpStatus.UNAUTHORIZED);
  }

  public State getNextState() {
    return NEXT_STATE;
  }
}
