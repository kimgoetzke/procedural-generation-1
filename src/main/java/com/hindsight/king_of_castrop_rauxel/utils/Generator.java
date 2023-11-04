package com.hindsight.king_of_castrop_rauxel.utils;

import java.util.Random;

public interface Generator {

  void setInitialised(boolean isInitialised);

  boolean isInitialised();

  void initialise(Random random);

  default void throwIfNotInitialised() {
    if (!isInitialised()) {
      throw new IllegalStateException("Generator has not been initialised");
    }
  }
}
