package com.hindsight.king_of_castrop_rauxel.location;

public interface Generatable {

  String getId();

  boolean isLoaded();

  void load();

  void unload();

  void logResult();
}
