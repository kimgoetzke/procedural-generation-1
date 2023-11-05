package com.hindsight.king_of_castrop_rauxel.encounter;

import com.hindsight.king_of_castrop_rauxel.characters.Enemy;
import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.event.Loot;
import com.hindsight.king_of_castrop_rauxel.world.Range;
import java.util.*;
import java.util.stream.IntStream;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DungeonHandler {

  private final AppProperties.DungeonProperties dungeonProperties;
  private final AppProperties.EnemyProperties enemyProperties;
  private final Map<Integer, EnemyConfig> enemyConfigs = new HashMap<>();
  private final int levelToTierDivider;

  public DungeonHandler(AppProperties appProperties) {
    this.enemyProperties = appProperties.getEnemyProperties();
    this.dungeonProperties = appProperties.getDungeonProperties();
    this.levelToTierDivider = appProperties.getGameProperties().levelToTierDivider();
    configureEnemies();
    log.debug(this.toString());
  }

  public int getDungeonTier(int targetLevel) {
    return (targetLevel / levelToTierDivider) + 1;
  }

  public Enemy.Type getDungeonType(Random random, int tier) {
    var types = dungeonProperties.getType(tier);
    return Enemy.Type.valueOf(types.get(random.nextInt(types.size())).name());
  }

  public List<List<EnemyDetails>> getEncounterDetails(
      Random random, int targetLevel, Enemy.Type type) {
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

  private EnemyDetails getEnemyDetails(int targetLevel, Enemy.Type type, Random random) {
    var tier = getDungeonTier(targetLevel);
    var config = enemyConfigs.get(tier);
    var damageBounds = config.getDamage().toBounds(targetLevel);
    var damage = new Damage(damageBounds.getLower(), damageBounds.getUpper());
    var experience = config.getExperience().toRandomActual(random, targetLevel, levelToTierDivider);
    var gold = config.getGold().toRandomActual(random, targetLevel, levelToTierDivider);
    var loot = new Loot().gold(gold).experience(experience);
    return EnemyDetails.builder()
        .health(config.getHealth().toRandomActual(random, targetLevel, levelToTierDivider))
        .damage(damage)
        .loot(loot)
        .level(targetLevel)
        .type(type)
        .build();
  }

  private void configureEnemies() {
    enemyConfigs.put(1, enemyProperties.t1());
    enemyConfigs.put(2, enemyProperties.t2());
    enemyConfigs.put(3, enemyProperties.t3());
    enemyConfigs.put(4, enemyProperties.t4());
    enemyConfigs.put(5, enemyProperties.t5());
    enemyConfigs.put(6, enemyProperties.t6());
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
