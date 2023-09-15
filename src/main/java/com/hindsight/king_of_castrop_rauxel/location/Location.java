package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.PlayerAction;
import org.springframework.data.util.Pair;

import java.util.List;

public interface Location extends Visitable, Generatable {

  long getSeed();

  String getName();

  String getDescription();

  List<PlayerAction> getAvailableActions();

  Pair<Integer, Integer> getCoordinates();

  String getSummary(); // TODO: Replace with objects so that it can be used via API
}
