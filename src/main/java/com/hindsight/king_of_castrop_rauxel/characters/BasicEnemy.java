package com.hindsight.king_of_castrop_rauxel.characters;

import com.hindsight.king_of_castrop_rauxel.combat.Damage;
import com.hindsight.king_of_castrop_rauxel.combat.EnemyDetails;
import com.hindsight.king_of_castrop_rauxel.event.Loot;
import com.hindsight.king_of_castrop_rauxel.location.DungeonDetails;
import com.hindsight.king_of_castrop_rauxel.utils.NameGenerator;
import com.hindsight.king_of_castrop_rauxel.world.IdBuilder;

import java.util.Random;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@ToString(onlyExplicitlyIncluded = true)
public class BasicEnemy implements Combatant {

  private final Random random;
  @ToString.Include private final String id;
  @ToString.Include private final int level;
  @ToString.Include private final Loot loot;
  @ToString.Include private final Damage damage;
  @ToString.Include private final String name;
  @ToString.Include private final DungeonDetails.Type type;
  @ToString.Include @Setter private int health;
  @Setter private Combatant target;

  public BasicEnemy(EnemyDetails enemyDetails, long seed, NameGenerator nameGenerator) {
    this.id = IdBuilder.idFrom(this.getClass());
    this.type = enemyDetails.type();
    this.name = generateName(nameGenerator);
    this.level = enemyDetails.level();
    this.loot = enemyDetails.loot();
    this.damage = enemyDetails.damage();
    this.health = enemyDetails.health();
    this.random = new Random(seed);
    logResult();
  }

  private String generateName(NameGenerator nameGenerator) {
    return nameGenerator.enemyNameFrom(this.getClass(), type);
  }

  @Override
  public int attack(Combatant target) {
    if (target == null) {
      return 0;
    }
    int actualDamage = damage.actual(random);
    target.takeDamage(actualDamage);
    return actualDamage;
  }

  public void logResult() {
    log.info("Generated: {}", this);
  }
}
