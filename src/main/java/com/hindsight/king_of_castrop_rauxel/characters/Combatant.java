package com.hindsight.king_of_castrop_rauxel.characters;

import com.hindsight.king_of_castrop_rauxel.event.Reward;
import org.springframework.data.util.Pair;

import java.util.List;

public interface Combatant {

  String getName();

  int getHealth();

  void setHealth(int health);

  Pair<Integer, Integer> getDamageRange();

  int getLevel();

  List<Reward> getReward();

  void attack(Combatant other);

  default void takeDamage(int damage) {
    var newHealth = getHealth() - damage;
    if (newHealth <= 0) {
      setHealth(0);
      die();
      return;
    }
    setHealth(newHealth);
  }

  void die();
}
