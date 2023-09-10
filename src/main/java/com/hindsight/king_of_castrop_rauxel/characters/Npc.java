package com.hindsight.king_of_castrop_rauxel.characters;

import com.hindsight.king_of_castrop_rauxel.location.Generatable;

public interface Npc extends Generatable {
  String getName();

  String getFirstName();

  String getLastName();

  String getId();
}
