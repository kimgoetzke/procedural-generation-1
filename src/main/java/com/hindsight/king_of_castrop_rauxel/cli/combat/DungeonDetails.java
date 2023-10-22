package com.hindsight.king_of_castrop_rauxel.cli.combat;

import java.util.Random;

import static com.hindsight.king_of_castrop_rauxel.configuration.AppConstants.DUNGEON_ENCOUNTERS_RANGE;
import static com.hindsight.king_of_castrop_rauxel.configuration.AppConstants.DUNGEON_LEVEL_RANGE;

public record DungeonDetails(int level, int encounters, EnemyType enemyType) {

  public static DungeonDetails load(Random random, int targetLevel) {
    var lower = DUNGEON_ENCOUNTERS_RANGE.getLower();
    var upper = DUNGEON_ENCOUNTERS_RANGE.getUpper();
    return new DungeonDetails(
        Math.max(targetLevel - DUNGEON_LEVEL_RANGE + (2 * random.nextInt(DUNGEON_LEVEL_RANGE)), 1),
        random.nextInt(upper - lower + 1) + lower,
        EnemyType.values()[(random.nextInt(EnemyType.values().length))]);
  }

  public enum EnemyType {
    SKELETON,
    ZOMBIE,
    GHOST,
    WEREWOLF,
    VAMPIRE,
    GOBLIN
  }
}
