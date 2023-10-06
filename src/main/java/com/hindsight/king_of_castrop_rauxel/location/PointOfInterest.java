package com.hindsight.king_of_castrop_rauxel.location;

import static com.hindsight.king_of_castrop_rauxel.location.AbstractAmenity.*;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.characters.Npc;

import java.util.List;

public interface PointOfInterest {

  String getName();

  String getDescription();

  PoiType getType();

  Location getParent();

  void loadAvailableActions();

  List<Action> getAvailableActions();

  Npc getNpc();

  String getSummary(); // TODO: Replace with objects so that it can be used via API
}
