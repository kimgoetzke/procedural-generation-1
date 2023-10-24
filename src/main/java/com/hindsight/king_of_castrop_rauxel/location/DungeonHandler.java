package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.utils.NameGenerator;
import com.hindsight.king_of_castrop_rauxel.world.SeedBuilder;
import com.hindsight.king_of_castrop_rauxel.world.TerrainGenerator;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

import static com.hindsight.king_of_castrop_rauxel.configuration.AppConstants.DUNGEON_ENCOUNTERS_RANGE;

@Slf4j
public class DungeonHandler {

  private final NameGenerator nameGenerator;
  private final TerrainGenerator terrainGenerator;
  private final PointOfInterest parentPoi;
  private final Location parentLocation;
  private final Random random;

  public DungeonHandler(PointOfInterest poi) {
    this.parentPoi = poi;
    this.parentLocation = poi.getParent();
    this.nameGenerator = parentLocation.getGenerators().nameGenerator();
    this.terrainGenerator = parentLocation.getGenerators().terrainGenerator();
    var seed = SeedBuilder.seedFrom(parentLocation.getCoordinates().getGlobal());
    this.random = new Random(seed);
  }

  public DungeonDetails build() {
    log.info("Building dungeon...");
    var encounters = getNumberOfEncounters();
    var type = DungeonHandler.Type.values()[(random.nextInt(DungeonHandler.Type.values().length))];
    var targetLevel = calculateTargetLevel();
    // TODO:
    //  - Generate target level (from coordinates?)
    //  - Generate level range based on target level
    //  - Generate type (based on what though?)
    //  - Generate boss fight (by chance)
    //  - Generate encounters numbers
    return null;
  }

  private int getNumberOfEncounters() {
    var lower = DUNGEON_ENCOUNTERS_RANGE.getLower();
    var upper = DUNGEON_ENCOUNTERS_RANGE.getUpper();
    return random.nextInt(upper - lower + 1) + lower;
  }

  private int calculateTargetLevel() {
    return terrainGenerator.getDifficulty(parentLocation.getCoordinates().getWorld());
  }

  public enum Type {
    SKELETON,
    GOBLIN
  }
}
