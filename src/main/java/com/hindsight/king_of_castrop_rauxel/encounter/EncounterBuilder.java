package com.hindsight.king_of_castrop_rauxel.encounter;

import static com.hindsight.king_of_castrop_rauxel.configuration.AppConstants.*;

import com.hindsight.king_of_castrop_rauxel.event.Loot;
import com.hindsight.king_of_castrop_rauxel.world.Range;
import java.util.*;
import java.util.stream.IntStream;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EncounterBuilder {

  private static final Map<Integer, List<DungeonDetails.Type>> DUNGEON_TYPES_CONFIGS =
      new HashMap<>();
  private static final Map<Integer, EnemyConfig> ENEMY_CONFIGS = new HashMap<>();

  public EncounterBuilder() {
    configureDungeons();
    configureEnemies();
    log.debug(this.toString());
  }

  public static int getDungeonTier(int targetLevel) {
    return (targetLevel / DUNGEON_TIER_DIVIDER) + 1;
  }

  public static DungeonDetails.Type getDungeonType(Random random, int tier) {
    var types = DUNGEON_TYPES_CONFIGS.get(tier);
    return DungeonDetails.Type.valueOf(types.get(random.nextInt(types.size())).name());
  }

  public static List<List<EnemyDetails>> getEncounterDetails(
      Random random, int targetLevel, DungeonDetails.Type type) {
    var encounterDetails = new ArrayList<List<EnemyDetails>>();
    var encountersArray = getEncounters(random);
    for (int encounter : encountersArray) {
      var list =
          IntStream.range(0, encounter)
              .mapToObj(j -> getEnemyDetails(targetLevel, type, random))
              .toList();
      encounterDetails.add(list);
    }
    return encounterDetails;
  }

  private static int[] getEncounters(Random random) {
    var dLower = ENCOUNTERS_PER_DUNGEON.getLower();
    var dUpper = ENCOUNTERS_PER_DUNGEON.getUpper();
    var encounters = new int[random.nextInt(dUpper - dLower + 1) + dLower];
    var eLower = ENEMIES_PER_ENCOUNTER.getLower();
    var eUpper = ENEMIES_PER_ENCOUNTER.getUpper();
    Arrays.setAll(encounters, i -> random.nextInt(eUpper - eLower + 1) + eLower);
    return encounters;
  }

  private static EnemyDetails getEnemyDetails(
      int targetLevel, DungeonDetails.Type type, Random random) {
    var tier = getDungeonTier(targetLevel);
    var config = ENEMY_CONFIGS.get(tier);
    var damageBounds = config.getDamage().toBounds(targetLevel);
    var damage = new Damage(damageBounds.getLower(), damageBounds.getUpper());
    var experience = config.getExperience().toRandomActual(random, targetLevel);
    var gold = config.getGold().toRandomActual(random, targetLevel);
    var loot = new Loot().gold(gold).experience(experience);
    return EnemyDetails.builder()
        .health(config.getHealth().toRandomActual(random, targetLevel))
        .damage(damage)
        .loot(loot)
        .level(targetLevel)
        .type(type)
        .build();
  }

  private void configureDungeons() {
    DUNGEON_TYPES_CONFIGS.put(1, DUNGEON_TYPES_T1);
    DUNGEON_TYPES_CONFIGS.put(2, DUNGEON_TYPES_T2);
    DUNGEON_TYPES_CONFIGS.put(3, DUNGEON_TYPES_T3);
    DUNGEON_TYPES_CONFIGS.put(4, DUNGEON_TYPES_T4);
    DUNGEON_TYPES_CONFIGS.put(5, DUNGEON_TYPES_T5);
  }

  private void configureEnemies() {
    ENEMY_CONFIGS.put(
        1,
        EnemyConfig.builder()
            .health(T1_ENEMY_HP_XP_GOLD)
            .damage(T1_ENEMY_DAMAGE)
            .experience(T1_ENEMY_HP_XP_GOLD)
            .gold(T1_ENEMY_HP_XP_GOLD)
            .build());
    ENEMY_CONFIGS.put(
        2,
        EnemyConfig.builder()
            .health(T2_ENEMY_HP_XP_GOLD)
            .damage(T2_ENEMY_DAMAGE)
            .experience(T2_ENEMY_HP_XP_GOLD)
            .gold(T2_ENEMY_HP_XP_GOLD)
            .build());
    ENEMY_CONFIGS.put(
        3,
        EnemyConfig.builder()
            .health(T3_ENEMY_HP_XP_GOLD)
            .damage(T3_ENEMY_DAMAGE)
            .experience(T3_ENEMY_HP_XP_GOLD)
            .gold(T3_ENEMY_HP_XP_GOLD)
            .build());
    ENEMY_CONFIGS.put(
        4,
        EnemyConfig.builder()
            .health(T4_ENEMY_HP_XP_GOLD)
            .damage(T4_ENEMY_DAMAGE)
            .experience(T4_ENEMY_HP_XP_GOLD)
            .gold(T4_ENEMY_HP_XP_GOLD)
            .build());
    ENEMY_CONFIGS.put(
        5,
        EnemyConfig.builder()
            .health(T5_ENEMY_HP_XP_GOLD)
            .damage(T5_ENEMY_DAMAGE)
            .experience(T5_ENEMY_HP_XP_GOLD)
            .gold(T5_ENEMY_HP_XP_GOLD)
            .build());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Available dungeon types by tier:%n".formatted());
    for (var entry : DUNGEON_TYPES_CONFIGS.entrySet()) {
      sb.append("- Tier %s=%s%n".formatted(entry.getKey(), entry.getValue()));
    }
    return sb.toString();
  }

  @Getter
  @Setter
  @Builder
  @ToString(includeFieldNames = false)
  public static class EnemyConfig {
    private Range health;
    private Range damage;
    private Range experience;
    private Range gold;
  }
}
