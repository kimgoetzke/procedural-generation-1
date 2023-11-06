package com.hindsight.king_of_castrop_rauxel.utils;

import java.util.Random;

import static com.google.common.base.Preconditions.checkState;

public interface Generator {

  void setInitialised(boolean isInitialised);

  boolean isInitialised();

  void initialise(Random random);

  default void throwIfNotInitialised() {
    checkState(isInitialised(), "Generator has not been initialised");
  }
}
