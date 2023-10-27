package com.hindsight.king_of_castrop_rauxel.combat;

import com.hindsight.king_of_castrop_rauxel.event.Loot;
import com.hindsight.king_of_castrop_rauxel.location.DungeonDetails;
import lombok.Builder;

@Builder
public record EnemyDetails(
    int level, Loot loot, Damage damage, int health, DungeonDetails.Type type) {

  @Override
  public String toString() {
    return "EnemyDetails(level="
        + level
        + ", loot="
        + loot
        + ", damage="
        + damage
        + ", health="
        + health
        + ", type="
        + type
        + ')';
  }
}
