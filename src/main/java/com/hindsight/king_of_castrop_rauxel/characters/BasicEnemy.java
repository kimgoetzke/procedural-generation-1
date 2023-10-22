package com.hindsight.king_of_castrop_rauxel.characters;

import com.hindsight.king_of_castrop_rauxel.cli.combat.DungeonDetails;
import com.hindsight.king_of_castrop_rauxel.event.Reward;
import com.hindsight.king_of_castrop_rauxel.world.IdBuilder;
import java.util.List;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.util.Pair;

@Getter
public class BasicEnemy implements Combatant {

  private final String id;
  private final String name;
  private final int level;
  private final Pair<Integer, Integer> damageRange;
  private final List<Reward> reward;
  @Setter private int health;
  @Setter private Combatant target;
  private final Random random = new Random();

  // TODO: Procedurally generate all relevant enemy stats
  public BasicEnemy(DungeonDetails details) {
    var type = details.enemyType();
    this.id = IdBuilder.idFrom(this.getClass());
    this.name = type.name();
    this.health = 10;
    this.level = details.level();
    this.damageRange = Pair.of(0, 3);
    this.reward = loadReward();
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
}
