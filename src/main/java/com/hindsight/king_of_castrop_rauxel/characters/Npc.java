package com.hindsight.king_of_castrop_rauxel.characters;

import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;

public interface Npc {
  String getId();

  String getName();

  String getFirstName();

  PointOfInterest getHome();

  void setHome(PointOfInterest home);

  void generate();

  void logResult();
}
