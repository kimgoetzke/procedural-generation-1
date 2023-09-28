package com.hindsight.king_of_castrop_rauxel.action;

import static com.hindsight.king_of_castrop_rauxel.characters.Player.*;
import static com.hindsight.king_of_castrop_rauxel.cli.CliComponent.*;

import com.hindsight.king_of_castrop_rauxel.characters.Player;

public interface Action {

  int getIndex();

  void setIndex(int index);

  String getName();

  State getNextState();

  default void execute(Player player) {
    setPlayerState(player);
  }

  default void setPlayerState(Player player) {
    player.setState(getNextState());
  }

  default String print() {
    return "%s[%s%s%s]%s %s"
        .formatted(FMT.WHITE, FMT.CYAN, getIndex(), FMT.WHITE, FMT.RESET, getName());
  }
}
