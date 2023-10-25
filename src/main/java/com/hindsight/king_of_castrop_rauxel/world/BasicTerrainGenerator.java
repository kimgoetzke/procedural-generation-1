package com.hindsight.king_of_castrop_rauxel.world;

import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import static com.hindsight.king_of_castrop_rauxel.configuration.AppConstants.WORLD_CENTER;
import static com.hindsight.king_of_castrop_rauxel.configuration.AppConstants.WORLD_SIZE;

/**
 * Currently only used to generate difficulty values for each chunk based on their distance to the
 * centre of the world. Over time, this class should use Perlin noise to generate terrain properties
 * (such as temperature, continentalness, humidity) and translate them into biomes which affect
 * settlement and POI generation including enemy types and difficulty values (and possibly also to
 * generate an ASCII art visual map).
 */
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BasicTerrainGenerator implements TerrainGenerator {

  private final int[][] targetLevel = new int[WORLD_SIZE][WORLD_SIZE];

  @Override
  public void initialise(Random random) {
    createTargetLevelMatrix();
  }

  private void createTargetLevelMatrix() {
    for (int row = 0; row < WORLD_SIZE; row++) {
      for (int col = 0; col < WORLD_SIZE; col++) {
        int distance = Math.max(Math.abs(row - WORLD_CENTER) + Math.abs(col - WORLD_CENTER), 1);
        targetLevel[row][col] = distance;
      }
    }
  }

  public int getTargetLevel(Coordinates coordinates) {
    var chunkDifficulty = this.targetLevel[coordinates.wX()][coordinates.wY()];
    log.info("Generated difficulty {} for {}", chunkDifficulty, coordinates.globalToString());
    return chunkDifficulty;
  }
}
