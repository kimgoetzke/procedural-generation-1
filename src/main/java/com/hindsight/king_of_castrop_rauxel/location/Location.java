package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.utils.DataServices;
import com.hindsight.king_of_castrop_rauxel.utils.Generators;
import com.hindsight.king_of_castrop_rauxel.world.CardinalDirection;
import com.hindsight.king_of_castrop_rauxel.world.Coordinates;
import com.hindsight.king_of_castrop_rauxel.world.Generatable;
import java.util.List;
import java.util.Set;
import org.springframework.data.util.Pair;

public interface Location extends Visitable, Generatable {

  String getName();

  Size getSize();

  int getTier();

  List<Action> getAvailableActions();

  List<PointOfInterest> getPointsOfInterest();

  PointOfInterest getDefaultPoi();

  Set<Location> getNeighbours();

  void addNeighbour(Location neighbour);

  Coordinates getCoordinates();

  CardinalDirection getCardinalDirection(Pair<Integer, Integer> otherCoordinates);

  Generators getGenerators();

  DataServices getDataServices();

  String getFullSummary(); // TODO: Replace with objects so that it can be used via API

  String getBriefSummary();

  default int distanceTo(Location end) {
    return getCoordinates().distanceTo(end.getCoordinates());
  }
}
