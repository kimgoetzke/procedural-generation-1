package com.hindsight.king_of_castrop_rauxel.utils;

import com.hindsight.king_of_castrop_rauxel.world.Coordinates;
import java.util.Random;

public interface TerrainGenerator extends Generator {
  @Override
  void initialise(Random random);

  int getTargetLevel(Coordinates coordinates);
}
