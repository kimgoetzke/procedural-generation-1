package com.hindsight.king_of_castrop_rauxel.cli.loop;

import static com.hindsight.king_of_castrop_rauxel.cli.CliComponent.*;
import static java.lang.System.out;

import com.google.common.base.Strings;
import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import de.codeshelf.consoleui.prompt.ConsolePrompt;
import de.codeshelf.consoleui.prompt.ListResult;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import jline.TerminalFactory;

public abstract class AbstractLoop {

  protected Player player;

  protected abstract Scanner getScanner();

  protected abstract AppProperties getAppProperties();

  public void initialise(Player player) {
    this.player = player;
  }

  public abstract void execute(List<Action> actions);

  protected void printHeaders(boolean showPoi) {
    if (getAppProperties().getEnvironment().clearConsole()) {
      clearConsole();
    }
    out.printf(
        "%sSTATS [ Gold: %s%s%s%s | Level: %s%s%s%s | Experience: %s%s%s%s | Health Points: %s%s%s%s ]%s%n",
        FMT.DEFAULT_BOLD,
        FMT.YELLOW_BOLD,
        player.getGold(),
        FMT.RESET,
        FMT.DEFAULT_BOLD,
        FMT.MAGENTA_BOLD,
        player.getLevel(),
        FMT.RESET,
        FMT.DEFAULT_BOLD,
        FMT.BLUE_BOLD,
        player.getExperience(),
        FMT.RESET,
        FMT.DEFAULT_BOLD,
        FMT.RED_BOLD,
        player.getHealth(),
        FMT.RESET,
        FMT.DEFAULT_BOLD,
        FMT.RESET);
    var currentLocation = player.getCurrentLocation();
    out.printf(
        "%sCURRENT LOCATION: %s%s%n%n",
        FMT.DEFAULT_BOLD, currentLocation.getFullSummary(), FMT.RESET);
    if (showPoi) {
      out.printf(
          "%sYou are at: %s.%s ", FMT.DEFAULT_BOLD, player.getCurrentPoi().getName(), FMT.RESET);
    }
  }

  protected void promptPlayer(List<Action> actions, String message) {
    if (actions.isEmpty()) {
      return;
    }
    if (getAppProperties().getEnvironment().useConsoleUi()) {
      useConsoleUi(actions, message);
      return;
    }
    useSystemOut(actions, message);
  }

  @SuppressWarnings("CallToPrintStackTrace")
  private void useConsoleUi(List<Action> actions, String message) {
    out.println();
    message = message == null ? "Your response:" : getDescription() + message;
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
      out.printf("%s%s%s%s%n", FMT.DEFAULT_BOLD, getDescription(), message, FMT.RESET);
    }
    actions.forEach(a -> out.println(a.print()));
    out.printf("%n%s>%s ", FMT.WHITE_BOLD_BRIGHT, FMT.RESET);
    takeAction(actions);
  }

  private String getDescription() {
    if (player.getState() != Player.State.AT_POI) {
      return "";
    }
    var poi = player.getCurrentPoi();
    var hasNoActions = poi.getAvailableActions().isEmpty();
    var text = hasNoActions ? "There is nothing to do here. " : "";
    var description = poi.getDescription();
    return Strings.isNullOrEmpty(description) ? text : description + text + " ";
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
      out.println(FMT.RED + "Invalid choice, try again..." + FMT.RESET);
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
    // Empty by default but can be overridden, e.g. to reset any quest progression
  }
}
