package com.hindsight.king_of_castrop_rauxel.utils;

import java.util.List;
import java.util.Random;

public record Generators(
    NameGenerator nameGenerator, EventGenerator eventGenerator, TerrainGenerator terrainGenerator) {

  public List<Generator> getAll() {
    return List.of(nameGenerator, eventGenerator, terrainGenerator);
  }

  public void initialiseAll(Random random) {
    for (var generator : getAll()) {
      generator.initialise(random);
    }
  }
}
