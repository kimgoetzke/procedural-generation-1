package com.hindsight.king_of_castrop_rauxel.configuration;

import com.hindsight.king_of_castrop_rauxel.encounter.Damage;
import com.hindsight.king_of_castrop_rauxel.world.Bounds;
import com.hindsight.king_of_castrop_rauxel.world.Range;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.hindsight.king_of_castrop_rauxel.encounter.DungeonDetails.*;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class AppConstants {

  // GAME PROPERTIES

  /**
   * The delay in milliseconds between each step. Currently used when displaying each action in an
   * encounter.
   */
  public static final long DELAY_IN_MS = 175;

  /**
   * The speed modifier for the progress bar. The higher the value, the slower the progress.
   * Examples: A modifier of 1 means that it takes 10 seconds to travel a distance of 100 km. A
   * modifier of 0.5 means that it takes 5 seconds to travel a distance of 100 km.
   */
  public static final float SPEED_MODIFIER = 0.1F;

  // WORLD PROPERTIES
  public static final int WORLD_SIZE = 50;
  public static final int WORLD_CENTER = WORLD_SIZE / 2;
  public static final int RETENTION_ZONE = 2;

  // CHUNK PROPERTIES
  public static final int CHUNK_SIZE = 500;
  public static final int MIN_PLACEMENT_DISTANCE = 30;
  public static final int MAX_GUARANTEED_NEIGHBOUR_DISTANCE = 130;
  public static final int GENERATION_TRIGGER_ZONE = 100;
  public static final Bounds DENSITY = new Bounds(5, 10);

  // LOCATION & POINT OF INTEREST PROPERTIES
  // Settlements
  public static final Bounds XS_INHABITANTS = new Bounds(1, 10);
  public static final Bounds S_INHABITANTS = new Bounds(11, 100);
  public static final Bounds M_INHABITANTS = new Bounds(101, 1000);
  public static final Bounds L_INHABITANTS = new Bounds(1001, 10000);
  public static final Bounds XL_INHABITANTS = new Bounds(10000, 250000);
  public static final Bounds XS_AREA = new Bounds(1, 1);
  public static final Bounds S_AREA = new Bounds(1, 2);
  public static final Bounds M_AREA = new Bounds(1, 3);
  public static final Bounds L_AREA = new Bounds(2, 8);
  public static final Bounds XL_AREA = new Bounds(12, 30);
  public static final Bounds XS_AMENITIES_ENTRANCE = new Bounds(0, 0);
  public static final Bounds S_AMENITIES_ENTRANCE = new Bounds(0, 1);
  public static final Bounds M_AMENITIES_ENTRANCE = new Bounds(1, 1);
  public static final Bounds L_AMENITIES_ENTRANCE = new Bounds(2, 3);
  public static final Bounds XL_AMENITIES_ENTRANCE = new Bounds(4, 5);
  public static final Bounds AMENITIES_MAIN_SQUARE = new Bounds(1, 1);
  public static final Bounds XS_AMENITIES_SHOP = new Bounds(0, 1);
  public static final Bounds S_AMENITIES_SHOP = new Bounds(1, 3);
  public static final Bounds M_AMENITIES_SHOP = new Bounds(3, 5);
  public static final Bounds L_AMENITIES_SHOP = new Bounds(5, 9);
  public static final Bounds XL_AMENITIES_SHOP = new Bounds(8, 12);
  public static final Bounds XS_AMENITIES_QUEST_LOCATION = new Bounds(0, 2);
  public static final Bounds S_AMENITIES_QUEST_LOCATION = new Bounds(2, 5);
  public static final Bounds M_AMENITIES_QUEST_LOCATION = new Bounds(3, 6);
  public static final Bounds L_AMENITIES_QUEST_LOCATION = new Bounds(7, 10);
  public static final Bounds XL_AMENITIES_QUEST_LOCATION = new Bounds(9, 14);
  public static final Bounds XS_AMENITIES_DUNGEON = new Bounds(0, 1);
  public static final Bounds S_AMENITIES_DUNGEON = new Bounds(0, 1);
  public static final Bounds M_AMENITIES_DUNGEON = new Bounds(1, 2);
  public static final Bounds L_AMENITIES_DUNGEON = new Bounds(2, 3);
  public static final Bounds XL_AMENITIES_DUNGEON = new Bounds(3, 4);

  // Dungeons
  public static final Bounds ENCOUNTERS_PER_DUNGEON = new Bounds(1, 4);
  public static final Bounds ENEMIES_PER_ENCOUNTER = new Bounds(1, 3);
  public static final int DUNGEON_TIER_DIVIDER = 10;
  public static final List<Type> DUNGEON_TYPES_T1 =
      List.of(Type.GOBLIN, Type.IMP, Type.CYNOCEPHALY);
  public static final List<Type> DUNGEON_TYPES_T2 = List.of(Type.SKELETON, Type.UNDEAD, Type.DEMON);
  public static final List<Type> DUNGEON_TYPES_T3 = List.of(Type.ORC, Type.TROLL, Type.ONOCENTAUR);
  public static final List<Type> DUNGEON_TYPES_T4 =
      List.of(Type.CENTICORE, Type.POOKA, Type.MAPUCHE);
  public static final List<Type> DUNGEON_TYPES_T5 =
      List.of(Type.SPHINX, Type.MINOTAUR, Type.CHIMERA);
  public static final List<Type> DUNGEON_TYPES_T6 = List.of(Type.CYCLOPS, Type.HYDRA, Type.PHOENIX);

  // ENEMIES PROPERTIES
  // Basic Enemy
  public static final Range T1_ENEMY_HP_XP_GOLD = new Range(10, 0.7F, 1.1F);
  public static final Range T2_ENEMY_HP_XP_GOLD = new Range(8, 0.8F, 1.1F);
  public static final Range T3_ENEMY_HP_XP_GOLD = new Range(6, 0.8F, 1.2F);
  public static final Range T4_ENEMY_HP_XP_GOLD = new Range(4, 0.8F, 1.3F);
  public static final Range T5_ENEMY_HP_XP_GOLD = new Range(2, 0.9F, 1.3F);
  public static final Range T1_ENEMY_DAMAGE = new Range(1, 0, 2);
  public static final Range T2_ENEMY_DAMAGE = new Range(1, 0.9F, 1.1F);
  public static final Range T3_ENEMY_DAMAGE = new Range(1, 1, 1.2F);
  public static final Range T4_ENEMY_DAMAGE = new Range(1, 1, 1.5F);
  public static final Range T5_ENEMY_DAMAGE = new Range(1, 1.2F, 2);

  // PLAYER PROPERTIES
  public static final int PLAYER_STARTING_GOLD = 100;
  public static final int PLAYER_STARTING_MAX_HEALTH = 100;
  public static final int PLAYER_EXPERIENCE_TO_LEVEL_UP = 100;
  public static final Damage PLAYER_STARTING_DAMAGE = new Damage(1, 4);
}
