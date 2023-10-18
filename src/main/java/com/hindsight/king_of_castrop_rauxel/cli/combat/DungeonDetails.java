package com.hindsight.king_of_castrop_rauxel.cli.combat;

import java.util.Random;

public record DungeonDetails(int level, DungeonType type, EnemyType enemyType, int encounters) {
  public static DungeonDetails random(
      Random random, DungeonType type, int targetLevel, int levelRange) {
    return new DungeonDetails(
        Math.max(targetLevel + random.nextInt(levelRange) - levelRange, 0), // Wrong
        type,
        EnemyType.values()[(random.nextInt(EnemyType.values().length))],
        random.nextInt(6 - 2 + 1) + 2);
  }

  public enum DungeonType {
    AMENITY
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
