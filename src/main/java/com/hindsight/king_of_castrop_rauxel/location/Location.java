package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.PlayerAction;

import java.util.List;

public interface Location extends Visitable, Generatable {
  String getId();

  long getSeed();

  String getName();

  String getDescription();

  List<PlayerAction> getAvailableActions();

  String getSummary(); // TODO: Replace with objects so that it can be used via API
}
