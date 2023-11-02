package com.hindsight.king_of_castrop_rauxel.world;

public interface Generatable {

  String getId();

  boolean isLoaded();

  void load();

  void logResult();

  default void throwIfRepeatedRequest(boolean toBeLoaded) {
    if (isLoaded() == toBeLoaded) {
      throw new IllegalStateException(
          "Request to %s settlement '%s' even though it already is, check your logic"
              .formatted(toBeLoaded ? "loaded" : "unloaded", getId()));
    }
  }
}
