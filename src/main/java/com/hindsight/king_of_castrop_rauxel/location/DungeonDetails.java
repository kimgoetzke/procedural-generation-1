package com.hindsight.king_of_castrop_rauxel.location;

import lombok.Builder;

import java.util.Arrays;

@Builder
public record DungeonDetails(
    String id, String name, String description, int tier, int level, int[] encounters, Type type) {

  public enum Type {
    SKELETON,
    GOBLIN,
    ORC,
    TROLL,
    VAMPIRE,
    WEREWOLF,
    DEMON,
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
