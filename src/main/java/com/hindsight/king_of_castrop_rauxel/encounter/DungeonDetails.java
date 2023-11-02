package com.hindsight.king_of_castrop_rauxel.encounter;

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
    DRAGON;

    public static Type from(int ordinal) {
      return Type.values()[ordinal];
    }

    public static Type from(String name) {
      return Type.valueOf(name);
    }
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
