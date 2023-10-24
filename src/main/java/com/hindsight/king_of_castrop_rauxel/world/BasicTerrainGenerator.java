package com.hindsight.king_of_castrop_rauxel.world;

import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;

import static com.hindsight.king_of_castrop_rauxel.configuration.AppConstants.WORLD_CENTER;
import static com.hindsight.king_of_castrop_rauxel.configuration.AppConstants.WORLD_SIZE;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BasicTerrainGenerator implements TerrainGenerator {

  private final int[][] difficulty = new int[WORLD_SIZE][WORLD_SIZE];

  @Override
  public void initialise(Random random) {
    for (int row = 0; row < WORLD_SIZE; row++) {
      for (int col = 0; col < WORLD_SIZE; col++) {
        int distance = Math.abs(row - WORLD_CENTER) + Math.abs(col - WORLD_CENTER);
        difficulty[row][col] = distance;
      }
    }
  }

  @Override
  public int getDifficulty(Pair<Integer, Integer> globalCoords) {
    var value = this.difficulty[globalCoords.getFirst()][globalCoords.getSecond()];
    log.info(
        "Generated difficulty {} for globalCoords: g({}-{})",
        value,
        globalCoords.getFirst(),
        globalCoords.getSecond());
    return value;
  }
}
