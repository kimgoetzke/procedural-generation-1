package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.combat.EnemyDetails;
import lombok.Builder;

import java.util.Arrays;
import java.util.List;

@Builder
public record DungeonDetails(
    String id,
    String name,
    String description,
    long seed,
    int tier,
    int level,
    List<List<EnemyDetails>> encounterDetails,
    Type type) {

  public enum Type {
    GOBLIN,
    IMP,
    CYNOCEPHALY,
    SKELETON,
    UNDEAD,
    DEMON,
    ORC,
    TROLL,
    ONOCENTAUR,
    CENTICORE,
    POOKA,
    MAPUCHE,
    SPHINX,
    MINOTAUR,
    CHIMERA,
    CYCLOPS,
    HYDRA,
    PHOENIX,
    DRAGON,
  }

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
