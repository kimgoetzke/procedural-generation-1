package com.hindsight.king_of_castrop_rauxel.characters;

import com.hindsight.king_of_castrop_rauxel.location.Location;

public interface Npc {
  String getId();

  String getName();

  String getFirstName();

  String getLastName();

  Location getHome();

  void setHome(Location location);

  void generate();

  void logResult();
}
