package com.hindsight.king_of_castrop_rauxel.character;

import com.hindsight.king_of_castrop_rauxel.event.Event;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;

import java.util.List;

public interface Npc {
  String getId();

  String getName();

  String getFirstName();

  PointOfInterest getHome();

  void setHome(PointOfInterest home);

  Event getPrimaryEvent();

  List<Event> getSecondaryEvents();

  void addSecondaryEvent(Event event);

  void load();

  void loadPrimaryEvent();

  void logResult();
}
