package com.hindsight.king_of_castrop_rauxel.encounter;

import com.hindsight.king_of_castrop_rauxel.characters.Enemy;
import java.util.Arrays;
import java.util.List;
import lombok.Builder;

@Builder
public record DungeonDetails(
    String id,
    String name,
    String description,
    long seed,
    int tier,
    int level,
    List<List<EnemyDetails>> encounterDetails,
    Enemy.Type type) {

  @Override
  public String toString() {
    return "{id="
        + id
        + ", name='"
        + name
        + ", description='"
        + description
        + ", tier="
        + tier
        + ", level="
        + level
        + ", encounterDetails="
        + Arrays.deepToString(encounterDetails.toArray())
        + ", type="
        + type
        + '}';
  }
}
