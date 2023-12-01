package com.hindsight.king_of_castrop_rauxel.item;

import com.hindsight.king_of_castrop_rauxel.character.Player;

public interface Buyable {

  String getName();

  String getDescription();

  int getTier();

  int getBasePrice();

  boolean isBoughtBy(Player player);
}
