package com.hindsight.king_of_castrop_rauxel.cli.loop;

import static java.lang.System.out;
import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.cli.CliComponent;
import de.codeshelf.consoleui.prompt.ConsolePrompt;
import de.codeshelf.consoleui.prompt.ListResult;
import jline.TerminalFactory;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public abstract class AbstractLoop {

  protected Player player;

  protected abstract Scanner getScanner();

  public void initialise(Player player) {
    this.player = player;
  }

  public abstract void execute(List<Action> actions);

  protected void printHeaders(boolean showPoi) {
    CliComponent.clearConsole();
    out.printf(
        "%sSTATS: [ Gold: %s%s%s%s | Level: %s%s%s%s | Activity points left: %s%s%s%s ]%s%n",
        CliComponent.FMT.DEFAULT_BOLD,
        CliComponent.FMT.YELLOW_BOLD,
        player.getGold(),
        CliComponent.FMT.RESET,
        CliComponent.FMT.DEFAULT_BOLD,
        CliComponent.FMT.MAGENTA_BOLD,
        player.getLevel(),
        CliComponent.FMT.RESET,
        CliComponent.FMT.DEFAULT_BOLD,
        CliComponent.FMT.GREEN_BOLD,
        player.getActivityPoints(),
        CliComponent.FMT.RESET,
        CliComponent.FMT.DEFAULT_BOLD,
        CliComponent.FMT.RESET);
    var currentLocation = player.getCurrentLocation();
    out.printf(
        "%sCURRENT LOCATION: %s%s%n%n",
        CliComponent.FMT.DEFAULT_BOLD, currentLocation.getFullSummary(), CliComponent.FMT.RESET);
    if (showPoi) {
      out.printf(
          "%sYou are at: %s.%s ",
          CliComponent.FMT.DEFAULT_BOLD, player.getCurrentPoi().getName(), CliComponent.FMT.RESET);
    }
  }

  protected void promptPlayer(List<Action> actions, String message) {
    if (actions.isEmpty()) {
      return;
    }
    if (Boolean.TRUE.equals(CliComponent.getIsRunningAsJar())) {
      useConsoleUi(actions, message);
      return;
    }
    useSystemOut(actions, message);
  }

  @SuppressWarnings("CallToPrintStackTrace")
  private void useConsoleUi(List<Action> actions, String message) {
    out.println();
    message = message == null ? "Your response:" : message;
    var prompt = new ConsolePrompt();
    var promptBuilder = prompt.getPromptBuilder();
    var listPrompt = promptBuilder.createListPrompt();
    listPrompt.name("prompt").message(message);
    actions.forEach(a -> listPrompt.newItem(String.valueOf(a.getIndex())).text(a.getName()).add());
    listPrompt.addPrompt();
    try {
      var result = prompt.prompt(promptBuilder.build());
      var selectedIndex = ((ListResult) result.get("prompt")).getSelectedId();
      var action = getValidActionOrThrow(Integer.parseInt(selectedIndex), actions);
      action.execute(player);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        TerminalFactory.get().restore();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private void useSystemOut(List<Action> actions, String message) {
    if (message != null) {
      out.printf("%s%s%s%n", CliComponent.FMT.DEFAULT_BOLD, message, CliComponent.FMT.RESET);
    }
    actions.forEach(a -> out.println(a.print()));
    out.printf("%n%s>%s ", CliComponent.FMT.WHITE_BOLD_BRIGHT, CliComponent.FMT.RESET);
    takeAction(actions);
  }

  protected void takeAction(List<Action> actions) {
    if (actions.isEmpty()) {
      return;
    }
    var anyInput = getScanner().next();
    try {
      var validInput = Integer.parseInt(anyInput);
      var action = getValidActionOrThrow(validInput, actions);
      action.execute(player);
    } catch (NumberFormatException e) {
      out.println(CliComponent.FMT.RED + "Invalid choice, try again..." + CliComponent.FMT.RESET);
      recoverInvalidAction();
    }
    out.println();
  }

  private Action getValidActionOrThrow(Integer validInput, List<Action> actions) {
    return actions.stream()
        .filter(a -> a.getIndex() == validInput)
        .findFirst()
        .orElseThrow(() -> new NumberFormatException("Couldn't find input in actions"));
  }

  protected void recoverInvalidAction() {
    // Empty by default but can be overridden, e.g. to resent any quest progression
  }
}
