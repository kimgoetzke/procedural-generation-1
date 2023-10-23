package com.hindsight.king_of_castrop_rauxel.characters;

import com.hindsight.king_of_castrop_rauxel.cli.CliComponent;
import com.hindsight.king_of_castrop_rauxel.event.Reward;
import org.springframework.data.util.Pair;

import java.util.List;

public interface Combatant {

  String getId();

  String getName();

  default boolean isAlive() {
    return getHealth() > 0;
  }

  int getHealth();

  void setHealth(int health);

  Pair<Integer, Integer> getDamageRange();

  int getLevel();

  List<Reward> getReward();

  default boolean hasTarget() {
    return getTarget() != null && getTarget().getHealth() > 0;
  }

  void setTarget(Combatant target);

  Combatant getTarget();

  default int attack() {
    return attack(getTarget());
  }

  int attack(Combatant target);

  default void takeDamage(int damage) {
    var newHealth = getHealth() - damage;
    if (newHealth <= 0) {
      setHealth(0);
      return;
    }
    setHealth(newHealth);
  }

  default String combatantToString() {
    var health = getHealth() > 0 ? ", " + CliComponent.health(getHealth()) + " HP" : "";
    return CliComponent.bold(getName())
        + " (Level "
        + CliComponent.level(getLevel())
        + health
        + ")";
  }
}
