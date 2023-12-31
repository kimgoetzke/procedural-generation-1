package com.hindsight.king_of_castrop_rauxel.cli.loop;

import static java.lang.System.out;

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

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DialogueLoop extends AbstractLoop {

  private final ActionHandler actionHandler;
  private final GameHandler gameHandler;
  @Getter private final Scanner scanner;
  @Getter private final AppProperties appProperties;

  @Override
  public void execute(List<Action> actions) {
    printInteraction();
    prepareActions(actions);
    promptPlayer(actions, null);
    postProcess();
  }

  private void printInteraction() {
    var dialogue = player.getCurrentEvent();
    if (dialogue.hasCurrentInteraction()) {
      // TODO: Fix dialogue.isBeginningOfDialogue() as it doesn't work when accepting reward
      if (dialogue.isBeginningOfDialogue() && appProperties.getGeneralProperties().clearConsole()) {
        CliComponent.clearConsole();
        out.println();
      }
      out.printf(
          "%s%s%s%s: %s%n%n",
          CliComponent.FMT.BLACK,
          CliComponent.FMT.WHITE_BACKGROUND,
          dialogue.getCurrentNpc().getName(),
          CliComponent.FMT.RESET,
          dialogue.getCurrentInteraction().getText().formatted());
    }
  }

  @Override
  protected void prepareActions(List<Action> actions) {
    if (player.getCurrentEvent().getCurrentActions().isEmpty()) {
      actionHandler.getNone(actions);
      CliComponent.awaitEnterKeyPress();
      return;
    }
    actionHandler.getDialogueActions(player, actions);
  }

  @Override
  protected void recoverFromInvalidAction() {
    log.info("Invalid action - trying to recover...");
    player.getCurrentEvent().rewindBy(1);
  }

  private void postProcess() {
    gameHandler.updateCurrentEventDialogue(player);
  }
}
