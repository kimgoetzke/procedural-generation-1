package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import java.util.List;
import java.util.Set;

import org.springframework.data.util.Pair;

public interface Location extends Visitable, Generatable {

  String getName();

  String getDescription();

  List<Action> getAvailableActions();

  List<PointOfInterest> getPointsOfInterest();

  Set<Location> getNeighbours();

  Pair<Integer, Integer> getCoordinates();

  String getSummary(); // TODO: Replace with objects so that it can be used via API
}
