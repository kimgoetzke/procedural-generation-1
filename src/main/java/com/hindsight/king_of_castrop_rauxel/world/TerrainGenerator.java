package com.hindsight.king_of_castrop_rauxel.world;

import com.hindsight.king_of_castrop_rauxel.utils.Generator;

import java.util.Random;

public interface TerrainGenerator extends Generator {
  @Override
  void initialise(Random random);

  int getDifficulty(Coordinates coordinates);
}
