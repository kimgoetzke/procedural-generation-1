package com.hindsight.king_of_castrop_rauxel.characters;

import com.hindsight.king_of_castrop_rauxel.cli.CliComponent;
import com.hindsight.king_of_castrop_rauxel.event.Loot;

public interface Combatant {

  String getId();

  String getName();

  Enemy.Type getType();

  default boolean isAlive() {
    return getHealth() > 0;
  }

  int getHealth();

  void setHealth(int health);

  int getLevel();

  Loot getLoot();

  default boolean hasTarget() {
    return getTarget() != null && getTarget().getHealth() > 0;
  }

  void setTarget(Combatant target);

  Combatant getTarget();

  default int attack() {
    if (!hasTarget()) {
      return 0;
    }
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
