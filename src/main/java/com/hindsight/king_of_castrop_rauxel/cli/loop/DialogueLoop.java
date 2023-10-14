package com.hindsight.king_of_castrop_rauxel.cli.loop;

import static java.lang.System.out;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.action.ActionHandler;
import com.hindsight.king_of_castrop_rauxel.cli.CliComponent;
import com.hindsight.king_of_castrop_rauxel.game.GameHandler;
import java.io.IOException;
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

  @Override
  public void execute(List<Action> actions) {
    printInteraction();
    prepareActions(actions);
    printActions(actions, null);
    takeAction(actions);
    postProcess();
  }

  private void printInteraction() {
    var dialogue = player.getCurrentEvent();
    if (dialogue.hasCurrentInteraction()) {
      if (dialogue.isBeginningOfDialogue()) {
        CliComponent.clearConsole();
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

  private void prepareActions(List<Action> actions) {
    if (player.getCurrentEvent().getCurrentActions().isEmpty()) {
      actionHandler.getNone(actions);
      awaitEnterKeyPress();
      return;
    }
    actionHandler.getDialogueActions(player, actions);
  }

  private void postProcess() {
    gameHandler.updateCurrentEventDialogue(player);
  }

  // TODO: Fix endless loop when having a one-line dialogue
  @SuppressWarnings("ResultOfMethodCallIgnored")
  private void awaitEnterKeyPress() {
    try {
      var message = "Press enter to continue...";
      out.print(message);
      System.in.read();
      CliComponent.removeString(message, true);
    } catch (IOException e) {
      log.error("Could not read input from console", e);
    }
  }

  @Override
  protected void recoverInvalidAction() {
    log.info("Invalid action - recovering");
    player.getCurrentEvent().rewindBy(1);
  }
}
