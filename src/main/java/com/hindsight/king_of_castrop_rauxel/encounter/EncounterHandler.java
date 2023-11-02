package com.hindsight.king_of_castrop_rauxel.encounter;

import static com.hindsight.king_of_castrop_rauxel.configuration.AppConstants.*;

import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.event.Loot;
import com.hindsight.king_of_castrop_rauxel.world.Range;
import java.util.*;
import java.util.stream.IntStream;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EncounterHandler {

  private final AppProperties.DungeonProperties dungeonProperties;
  private final AppProperties.EnemyProperties enemyProperties;
  private final Map<Integer, List<DungeonDetails.Type>> dungeonTypesConfigs = new HashMap<>();
  private final Map<Integer, EnemyConfig> enemyConfigs = new HashMap<>();

  public EncounterHandler(AppProperties appProperties) {
    this.dungeonProperties = appProperties.getDungeonProperties();
    this.enemyProperties = appProperties.getEnemyProperties();
    configureDungeons();
    configureEnemies();
    log.debug(this.toString());
  }

  public int getDungeonTier(int targetLevel) {
    return (targetLevel / LEVEL_TO_TIER_DIVIDER) + 1;
  }

  public DungeonDetails.Type getDungeonType(Random random, int tier) {
    var types = dungeonTypesConfigs.get(tier);
    return DungeonDetails.Type.valueOf(types.get(random.nextInt(types.size())).name());
  }

  public List<List<EnemyDetails>> getEncounterDetails(
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

  private int[] getEncounters(Random random) {
    var dLower = dungeonProperties.encountersPerDungeon().getLower();
    var dUpper = dungeonProperties.encountersPerDungeon().getUpper();
    var encounters = new int[random.nextInt(dUpper - dLower + 1) + dLower];
    var eLower = dungeonProperties.enemiesPerEncounter().getLower();
    var eUpper = dungeonProperties.enemiesPerEncounter().getUpper();
    Arrays.setAll(encounters, i -> random.nextInt(eUpper - eLower + 1) + eLower);
    return encounters;
  }

  private EnemyDetails getEnemyDetails(int targetLevel, DungeonDetails.Type type, Random random) {
    var tier = getDungeonTier(targetLevel);
    var config = enemyConfigs.get(tier);
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
    dungeonTypesConfigs.put(1, dungeonProperties.t1Types());
    dungeonTypesConfigs.put(2, dungeonProperties.t2Types());
    dungeonTypesConfigs.put(3, dungeonProperties.t3Types());
    dungeonTypesConfigs.put(4, dungeonProperties.t4Types());
    dungeonTypesConfigs.put(5, dungeonProperties.t5Types());
  }

  private void configureEnemies() {
    enemyConfigs.put(1, enemyProperties.t1());
    enemyConfigs.put(2, enemyProperties.t2());
    enemyConfigs.put(3, enemyProperties.t3());
    enemyConfigs.put(4, enemyProperties.t4());
    enemyConfigs.put(5, enemyProperties.t5());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Available dungeon types by tier:%n".formatted());
    for (var entry : dungeonTypesConfigs.entrySet()) {
      sb.append("- Tier %s=%s%n".formatted(entry.getKey(), entry.getValue()));
    }
    return sb.toString();
  }

  @Getter
  @Setter
  @Builder
  @ToString(includeFieldNames = false)
  public static class EnemyConfig {
    private Range damage;
    private Range health;
    private Range experience;
    private Range gold;
  }
}
