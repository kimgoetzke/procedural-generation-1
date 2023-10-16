package com.hindsight.king_of_castrop_rauxel.cli.loop;

import static java.lang.System.out;
import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.cli.CliComponent;
import de.codeshelf.consoleui.prompt.ConsolePrompt;
import de.codeshelf.consoleui.prompt.PromtResultItemIF;
import jline.TerminalFactory;

import java.io.IOException;
import java.util.HashMap;
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
        "%sSTATS: [ Gold: %s%s%s | Level: %s%s%s | Activity points left: %s%s%s ]%s%n",
        CliComponent.FMT.DEFAULT_BOLD,
        CliComponent.FMT.YELLOW_BOLD,
        player.getGold(),
        CliComponent.FMT.DEFAULT_BOLD,
        CliComponent.FMT.MAGENTA_BOLD,
        player.getLevel(),
        CliComponent.FMT.DEFAULT_BOLD,
        CliComponent.FMT.GREEN_BOLD,
        player.getActivityPoints(),
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

  protected void printActions(List<Action> actions, String prompt) {
    if (actions.isEmpty()) {
      return;
    }
    if (prompt != null) {
      out.printf("%s%s%s%n", CliComponent.FMT.DEFAULT_BOLD, prompt, CliComponent.FMT.RESET);
    }
    //    System.out.println(ansi().eraseScreen().render("Simple list example:"));
    var p = new ConsolePrompt();
    var promptBuilder = p.getPromptBuilder();
    var list = promptBuilder.createListPrompt();
    list.name("asd").message("asd");
    actions.forEach(a -> list.newItem(String.valueOf(a.getIndex())).text(a.getName()).add());
    list.addPrompt();
    try {
      HashMap<String, ? extends PromtResultItemIF> result = p.prompt(promptBuilder.build());
      System.out.println("result = " + result);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        TerminalFactory.get().restore();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    out.printf("%n%s>%s ", CliComponent.FMT.WHITE_BOLD_BRIGHT, CliComponent.FMT.RESET);
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
