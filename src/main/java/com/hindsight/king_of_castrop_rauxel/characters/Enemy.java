package com.hindsight.king_of_castrop_rauxel.characters;

import com.hindsight.king_of_castrop_rauxel.event.Reward;

import java.util.List;

public interface Enemy {

  String getName();

  int getHealth();

  void setHealth(int health);

  int getDamage();

  int getLevel();

  List<Reward> getReward();

  void attack(Player player);

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
