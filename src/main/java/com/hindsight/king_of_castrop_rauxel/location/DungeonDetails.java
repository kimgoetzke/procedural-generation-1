package com.hindsight.king_of_castrop_rauxel.location;

import lombok.Builder;

import java.util.Arrays;

@Builder
public record DungeonDetails(
    String id, String name, String description, int tier, int level, int[] encounters, Type type) {

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
        + ", encounters="
        + Arrays.toString(encounters)
        + ", type="
        + type
        + '}';
  }
}
