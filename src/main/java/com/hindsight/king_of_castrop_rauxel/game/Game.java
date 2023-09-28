package com.hindsight.king_of_castrop_rauxel.game;

import com.hindsight.king_of_castrop_rauxel.action.Action;

import java.util.List;
import java.util.Optional;

public interface Game {
  void start();

  void getActions(List<Action> actions);

  void processAction(Optional<Action> action);

  void updateWorld();
}
