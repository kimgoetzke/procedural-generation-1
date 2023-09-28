package com.hindsight.king_of_castrop_rauxel.cli;

import com.hindsight.king_of_castrop_rauxel.action.ActionHandler;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired), access = AccessLevel.PRIVATE)
public class NewCliGame {

  private final ActionHandler actionHandler;
  private final CliGame game;

  @SuppressWarnings("InfiniteLoopStatement")
  public void play() {
    var actions = actionHandler.getEmpty();
    game.start();
    while (true) {
      game.getActions(actions);
      game.displayActions(actions);
      game.processInput(actions);
      game.updateWorld();
    }
  }
}
