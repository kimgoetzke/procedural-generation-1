package com.hindsight.king_of_castrop_rauxel.location;

import static com.hindsight.king_of_castrop_rauxel.location.AbstractLocation.*;
import static com.hindsight.king_of_castrop_rauxel.world.WorldBuildingComponent.*;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.utils.StringGenerator;
import java.util.List;
import java.util.Set;
import org.springframework.data.util.Pair;

public interface Location extends Visitable, Generatable {

  String getName();

  Size getSize();

  String getDescription();

  List<Action> getAvailableActions();

  List<PointOfInterest> getPointsOfInterest();

  PointOfInterest getDefaultPoi();

  Set<Location> getNeighbours();

  Pair<Integer, Integer> getCoordinates();

  CardinalDirection getCardinalDirection(Pair<Integer, Integer> otherCoordinates);

  StringGenerator getStringGenerator();

  String getFullSummary(); // TODO: Replace with objects so that it can be used via API

  String getBriefSummary();

  default int distanceTo(Location end) {
    int deltaX = end.getCoordinates().getFirst() - getCoordinates().getFirst();
    int deltaY = end.getCoordinates().getSecond() - getCoordinates().getSecond();
    double distance = Math.sqrt((double) deltaX * deltaX + deltaY * deltaY);
    return (int) Math.round(distance);
  }
}
