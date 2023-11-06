package com.hindsight.king_of_castrop_rauxel.world;

import static com.google.common.base.Preconditions.checkState;

public interface Generatable {

  String getId();

  boolean isLoaded();

  void load();

  void logResult();

  default void throwIfRepeatedRequest(boolean toBeLoaded) {
    var state = toBeLoaded ? "loaded" : "unloaded";
    checkState(
        isLoaded() != toBeLoaded,
        "Requested to %s settlement '%s' but it already is",
        state,
        getId());
  }
}
