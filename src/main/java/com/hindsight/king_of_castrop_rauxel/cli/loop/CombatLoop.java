package com.hindsight.king_of_castrop_rauxel.cli.loop;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.action.ActionHandler;
import com.hindsight.king_of_castrop_rauxel.cli.CliComponent;
import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.game.GameHandler;
import java.util.List;
import java.util.Scanner;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.lang.System.out;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CombatLoop extends AbstractLoop {

  private final ActionHandler actionHandler;
  private final GameHandler gameHandler;
  @Getter private final Scanner scanner;
  @Getter private final AppProperties appProperties;

  // TODO: Implement actual attack/defend loop
  //  - Start encounter and give summary of opponents and initiative (later description)
  //  - Loop through combat actions until encounter is over (DONE)
  //  - Give summary, reward and update player stats (DONE)
  //  - Return to this loop and show actions (press on vs retreat)
  //  - Optional: Once sequence is complete, show summary of the entire sequence
  @Override
  public void execute(List<Action> actions) {
    printHeaders(false);
    prepareActions(actions);
    promptPlayer(actions, "What now?");
    postProcess();
  }

  @Override
  protected void printHeaders(boolean showPoi) {
    super.printHeaders(showPoi);
    if (showPoi) {
      out.printf(
          "%sYou are at %s.%s ", // TODO: Add description of where player is
          CliComponent.FMT.DEFAULT_BOLD, player.getCurrentPoi().getName(), CliComponent.FMT.RESET);
    }
  }

  private void prepareActions(List<Action> actions) {
    actionHandler.getCombatActions(actions);
  }

  private void postProcess() {
    // TODO: Implement gameHandler and/or other combat processing
    //  - Update after combat action
    //  - Update before next combat interaction
    //  - Finish event if no more interactions and give reward
  }
}
