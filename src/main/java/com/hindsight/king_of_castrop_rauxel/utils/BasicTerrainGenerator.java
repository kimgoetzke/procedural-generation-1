package com.hindsight.king_of_castrop_rauxel.utils;

import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.world.Coordinates;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;

/**
 * Currently only used to generate difficulty values for each chunk based on their distance to the
 * centre of the world. Over time, this class should use Perlin noise to generate terrain properties
 * (such as temperature, continentalness, humidity) and translate them into biomes which affect
 * settlement and POI generation including enemy types and difficulty values (and possibly also to
 * generate an ASCII art visual map).
 */
@Slf4j
public class BasicTerrainGenerator implements TerrainGenerator {

  private final AppProperties appProperties;

  private final int[][] targetLevel;
  private final int worldSize;
  private final int worldCentre;

  public BasicTerrainGenerator(AppProperties appProperties) {
    this.appProperties = appProperties;
    this.worldSize = appProperties.getWorldProperties().size();
    this.worldCentre = appProperties.getWorldProperties().centre();
    targetLevel = new int[worldSize][worldSize];
  }

  @Override
  public void initialise(Random random) {
    createTargetLevelMatrix();
  }

  private void createTargetLevelMatrix() {
    for (int row = 0; row < worldSize; row++) {
      for (int col = 0; col < worldSize; col++) {
        int distance = Math.max(Math.abs(row - worldCentre) + Math.abs(col - worldCentre), 1);
        targetLevel[row][col] = distance;
      }
    }
  }

  public int getTargetLevel(Coordinates coordinates) {
    var chunkDifficulty = this.targetLevel[coordinates.wX()][coordinates.wY()];
    log.debug("Generated difficulty {} for {}", chunkDifficulty, coordinates.globalToString());
    return chunkDifficulty;
  }
}
