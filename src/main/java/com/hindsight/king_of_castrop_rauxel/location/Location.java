package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import java.util.List;
import java.util.Set;

import com.hindsight.king_of_castrop_rauxel.utils.StringGenerator;
import org.springframework.data.util.Pair;

import static com.hindsight.king_of_castrop_rauxel.location.AbstractLocation.*;

public interface Location extends Visitable, Generatable {

  String getName();

  Size getSize();

  String getDescription();

  List<Action> getAvailableActions();

  List<PointOfInterest> getPointsOfInterest();

  PointOfInterest getDefaultPoi();

  Set<Location> getNeighbours();

  Pair<Integer, Integer> getCoordinates();

  StringGenerator getStringGenerator();

  String getSummary(); // TODO: Replace with objects so that it can be used via API
}
