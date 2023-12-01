package com.hindsight.king_of_castrop_rauxel.character;

public interface Enemy {

  String getId();

  String getName();

  int getLevel();

  Type getType();

  enum Type {
    PLAYER,
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
}
