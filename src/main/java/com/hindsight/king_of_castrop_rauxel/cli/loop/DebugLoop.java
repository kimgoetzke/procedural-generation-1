package com.hindsight.king_of_castrop_rauxel.cli.loop;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.action.ActionHandler;
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
public class DebugLoop extends AbstractLoop {

  private final ActionHandler actionHandler;
  private final GameHandler gameHandler;
  @Getter private final Scanner scanner;
  @Getter private final AppProperties appProperties;

  @Override
  public void execute(List<Action> actions) {
    printHeaders(false);
    prepareActions(actions);
    promptPlayer(actions, "What would you like to do?");
    postProcess();
  }

  @Override
  protected void prepareActions(List<Action> actions) {
    actionHandler.getDebugActions(player, actions);
  }

  private void postProcess() {
    gameHandler.updateWorld(player);
  }
}
