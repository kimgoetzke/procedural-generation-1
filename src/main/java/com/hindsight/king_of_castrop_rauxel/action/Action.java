package com.hindsight.king_of_castrop_rauxel.action;

import com.hindsight.king_of_castrop_rauxel.characters.Player;

public interface Action {
  String getName();

  int getNumber();

  boolean execute(Player player);

  String print();
}
