package com.hindsight.king_of_castrop_rauxel.location;

import static com.hindsight.king_of_castrop_rauxel.location.AbstractAmenity.*;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.event.Event;

import java.util.List;

public interface PointOfInterest {

  String getName();

  String getDescription();

  PoiType getType();

  Location getParent();

  void addAvailableAction(Event event);

  List<Action> getAvailableActions();

  Npc getNpc();

  String getSummary();
}
