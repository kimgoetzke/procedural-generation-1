package com.hindsight.king_of_castrop_rauxel.action;

import static com.hindsight.king_of_castrop_rauxel.character.Player.*;
import static com.hindsight.king_of_castrop_rauxel.cli.CliComponent.*;

import com.hindsight.king_of_castrop_rauxel.character.Player;
import com.hindsight.king_of_castrop_rauxel.configuration.EnvironmentResolver;

public interface Action {

  EnvironmentResolver.Environment getEnvironment();

  void setEnvironment(EnvironmentResolver.Environment environment);

  int getIndex();

  void setIndex(int index);

  String getName();

  void setName(String name);

  State getNextState();

  default void execute(Player player) {
    nextState(player);
  }

  default void nextState(Player player) {
    player.setState(getNextState());
  }

  default String print() {
    return "%s[%s%s%s]%s %s"
        .formatted(FMT.WHITE, FMT.CYAN, getIndex(), FMT.WHITE, FMT.RESET, getName());
  }
}
