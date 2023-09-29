package com.hindsight.king_of_castrop_rauxel.location;

import static com.hindsight.king_of_castrop_rauxel.location.AbstractLocation.*;
import static com.hindsight.king_of_castrop_rauxel.world.WorldHandler.*;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.utils.NameGenerator;
import java.util.List;
import java.util.Set;

import com.hindsight.king_of_castrop_rauxel.world.Coordinates;
import org.springframework.data.util.Pair;

public interface Location extends Visitable, Generatable {

  String getName();

  Size getSize();

  String getDescription();

  List<Action> getAvailableActions();

  List<PointOfInterest> getPointsOfInterest();

  PointOfInterest getDefaultPoi();

  Set<Location> getNeighbours();

  void addNeighbour(Location neighbour);

  Coordinates getCoordinates();

  CardinalDirection getCardinalDirection(Pair<Integer, Integer> otherCoordinates);

  NameGenerator getNameGenerator();

  String getFullSummary(); // TODO: Replace with objects so that it can be used via API

  String getBriefSummary();

  default int distanceTo(Location end) {
    return getCoordinates().distanceTo(end.getCoordinates());
  }
}
