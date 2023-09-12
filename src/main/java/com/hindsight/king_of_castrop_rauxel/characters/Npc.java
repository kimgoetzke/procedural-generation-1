package com.hindsight.king_of_castrop_rauxel.characters;

import com.hindsight.king_of_castrop_rauxel.location.Generatable;
import com.hindsight.king_of_castrop_rauxel.location.Location;

public interface Npc extends Generatable {
  String getName();

  String getFirstName();

  String getLastName();

  String getId();

  Location getHome();

  void setHome(Location location);
}
