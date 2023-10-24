package com.hindsight.king_of_castrop_rauxel.characters;

import com.hindsight.king_of_castrop_rauxel.cli.combat.DungeonDetails;
import com.hindsight.king_of_castrop_rauxel.event.Reward;
import com.hindsight.king_of_castrop_rauxel.utils.NameGenerator;
import com.hindsight.king_of_castrop_rauxel.world.Generatable;
import com.hindsight.king_of_castrop_rauxel.world.IdBuilder;
import java.util.List;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

@Slf4j
@Getter
@ToString(onlyExplicitlyIncluded = true)
public class BasicEnemy implements Combatant, Generatable {

  @ToString.Include private final String id;
  private final Random random = new Random();
  private final NameGenerator nameGenerator;
  private final DungeonDetails dungeonDetails;
  @ToString.Include private int level;
  @ToString.Include private List<Reward> reward;
  @ToString.Include private Pair<Integer, Integer> damageRange;
  @ToString.Include private String name;
  @ToString.Include @Setter private int health;
  @Setter private Combatant target;
  @Setter private boolean isLoaded;

  // TODO: Procedurally generate all relevant enemy stats
  public BasicEnemy(DungeonDetails dungeonDetails, NameGenerator nameGenerator) {
    this.id = IdBuilder.idFrom(this.getClass());
    this.nameGenerator = nameGenerator;
    this.dungeonDetails = dungeonDetails;
    load();
  }

  private List<Reward> loadReward() {
    var gold = random.nextInt(12) + 1;
    var range = Pair.of((level - 1) * 10, (level + 1) * 10);
    var exp = random.nextInt(range.getSecond() - range.getFirst() + 1) + range.getFirst();
    return List.of(new Reward(Reward.Type.GOLD, gold), new Reward(Reward.Type.EXPERIENCE, exp));
  }

  @Override
  public int attack(Combatant target) {
    if (target == null) {
      return 0;
    }
    var damage =
        random.nextInt(damageRange.getSecond() - damageRange.getFirst() + 1)
            + damageRange.getFirst();
    target.takeDamage(damage);
    return damage;
  }

  @Override
  public void load() {
    health = 10;
    level = dungeonDetails.level();
    damageRange = Pair.of(0, 3);
    reward = loadReward();
    name = nameGenerator.enemyNameFrom(this.getClass(), dungeonDetails.type());
    setLoaded(true);
    logResult();
  }

  @Override
  public void logResult() {
    log.info("Generated: {}", this);
  }
}
