package com.hindsight.king_of_castrop_rauxel.items;

import com.hindsight.king_of_castrop_rauxel.characters.Player;

public interface Buyable {

  String getName();

  String getDescription();

  int getBasePrice();

  boolean isBoughtBy(Player player);
}
