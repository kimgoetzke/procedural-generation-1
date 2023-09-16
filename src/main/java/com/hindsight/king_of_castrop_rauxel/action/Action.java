package com.hindsight.king_of_castrop_rauxel.action;

import com.hindsight.king_of_castrop_rauxel.characters.Player;

import java.util.List;

import static com.hindsight.king_of_castrop_rauxel.characters.Player.*;

public interface Action {

  int getIndex();

  void setIndex(int index);

  String getName();

  State getNextState();

  default void execute(Player player, List<Action> actions) {
    setPlayerState(player);
  }

  default void setPlayerState(Player player) {
    player.setState(getNextState());
  }

  default String print() {
    return "[%s] %s".formatted(getIndex(), getName());
  }
}
