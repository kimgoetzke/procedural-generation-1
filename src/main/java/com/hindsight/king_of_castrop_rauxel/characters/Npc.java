package com.hindsight.king_of_castrop_rauxel.characters;

import com.hindsight.king_of_castrop_rauxel.event.Event;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;

import java.util.List;

public interface Npc {
  String getId();

  String getName();

  String getFirstName();

  PointOfInterest getHome();

  void setHome(PointOfInterest home);

  Event getOriginEvent();

  void loadEvent();

  List<Event> getTargetEvents();

  void addTargetEvent(Event event);

  void load();

  void logResult();
}
