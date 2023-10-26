package com.hindsight.king_of_castrop_rauxel.location;

import static com.hindsight.king_of_castrop_rauxel.configuration.AppConstants.*;
import static com.hindsight.king_of_castrop_rauxel.location.PointOfInterest.Type;

import com.hindsight.king_of_castrop_rauxel.world.Bounds;

import java.util.*;

import com.hindsight.king_of_castrop_rauxel.world.Generatable;
import com.hindsight.king_of_castrop_rauxel.world.Range;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LocationBuilder {

  private static final Map<Size, SettlementConfig> SETTLEMENT_CONFIGS = new EnumMap<>(Size.class);
  private static final Map<Integer, List<DungeonDetails.Type>> DUNGEON_TYPES_CONFIG =
      new HashMap<>();
  private static final Map<Integer, EnemyConfig> ENEMY_CONFIGS = new HashMap<>();

  public LocationBuilder() {
    configureSettlements();
    configureDungeons();
    configureEnemies();
    log.debug(this.toString());
  }

  private void configureEnemies() {
    // TODO: Set here
  }

  public static SettlementConfig getSettlementConfig(Size size) {
    return SETTLEMENT_CONFIGS.get(size);
  }

  public static int dungeonTierFrom(int targetLevel) {
    return (targetLevel / DUNGEON_TIER_DIVIDER) + 1;
  }

  public static DungeonDetails.Type dungeonTypeFrom(int tier, Random random) {
    var types = DUNGEON_TYPES_CONFIG.get(tier);
    return types.get(random.nextInt(types.size()));
  }

  public static int[] encountersFrom(Random random) {
    var dLower = ENCOUNTERS_PER_DUNGEON.getLower();
    var dUpper = ENCOUNTERS_PER_DUNGEON.getUpper();
    var encounters = new int[random.nextInt(dUpper - dLower + 1) + dLower];
    var eLower = ENEMIES_PER_ENCOUNTER.getLower();
    var eUpper = ENEMIES_PER_ENCOUNTER.getUpper();
    Arrays.setAll(encounters, i -> random.nextInt(eUpper - eLower + 1) + eLower);
    return encounters;
  }

  private void configureSettlements() {
    SettlementConfig xs = new SettlementConfig();
    SettlementConfig s = new SettlementConfig();
    SettlementConfig m = new SettlementConfig();
    SettlementConfig l = new SettlementConfig();
    SettlementConfig xl = new SettlementConfig();

    xs.setInhabitants(XS_INHABITANTS);
    s.setInhabitants(S_INHABITANTS);
    m.setInhabitants(M_INHABITANTS);
    l.setInhabitants(L_INHABITANTS);
    xl.setInhabitants(XL_INHABITANTS);

    xs.area = XS_AREA;
    s.area = S_AREA;
    m.area = M_AREA;
    l.area = L_AREA;
    xl.area = XL_AREA;

    xs.amenities = new EnumMap<>(Type.class);
    s.amenities = new EnumMap<>(Type.class);
    m.amenities = new EnumMap<>(Type.class);
    l.amenities = new EnumMap<>(Type.class);
    xl.amenities = new EnumMap<>(Type.class);

    xs.amenities.put(Type.ENTRANCE, XS_AMENITIES_ENTRANCE);
    s.amenities.put(Type.ENTRANCE, S_AMENITIES_ENTRANCE);
    m.amenities.put(Type.ENTRANCE, M_AMENITIES_ENTRANCE);
    l.amenities.put(Type.ENTRANCE, L_AMENITIES_ENTRANCE);
    xl.amenities.put(Type.ENTRANCE, XL_AMENITIES_ENTRANCE);

    xs.amenities.put(Type.MAIN_SQUARE, XS_AMENITIES_MAIN_SQUARE);
    s.amenities.put(Type.MAIN_SQUARE, S_AMENITIES_MAIN_SQUARE);
    m.amenities.put(Type.MAIN_SQUARE, M_AMENITIES_MAIN_SQUARE);
    l.amenities.put(Type.MAIN_SQUARE, L_AMENITIES_MAIN_SQUARE);
    xl.amenities.put(Type.MAIN_SQUARE, XL_AMENITIES_MAIN_SQUARE);

    xs.amenities.put(Type.SHOP, XS_AMENITIES_SHOP);
    s.amenities.put(Type.SHOP, S_AMENITIES_SHOP);
    m.amenities.put(Type.SHOP, M_AMENITIES_SHOP);
    l.amenities.put(Type.SHOP, L_AMENITIES_SHOP);
    xl.amenities.put(Type.SHOP, XL_AMENITIES_SHOP);

    xs.amenities.put(Type.QUEST_LOCATION, XS_AMENITIES_QUEST_LOCATION);
    s.amenities.put(Type.QUEST_LOCATION, S_AMENITIES_QUEST_LOCATION);
    m.amenities.put(Type.QUEST_LOCATION, M_AMENITIES_QUEST_LOCATION);
    l.amenities.put(Type.QUEST_LOCATION, L_AMENITIES_QUEST_LOCATION);
    xl.amenities.put(Type.QUEST_LOCATION, XL_AMENITIES_QUEST_LOCATION);

    xs.amenities.put(Type.DUNGEON, XS_AMENITIES_DUNGEON);
    s.amenities.put(Type.DUNGEON, S_AMENITIES_DUNGEON);
    m.amenities.put(Type.DUNGEON, M_AMENITIES_DUNGEON);
    l.amenities.put(Type.DUNGEON, L_AMENITIES_DUNGEON);
    xl.amenities.put(Type.DUNGEON, XL_AMENITIES_DUNGEON);

    SETTLEMENT_CONFIGS.put(Size.XS, xs);
    SETTLEMENT_CONFIGS.put(Size.S, s);
    SETTLEMENT_CONFIGS.put(Size.M, m);
    SETTLEMENT_CONFIGS.put(Size.L, l);
    SETTLEMENT_CONFIGS.put(Size.XL, xl);
  }

  private void configureDungeons() {
    DUNGEON_TYPES_CONFIG.put(1, DUNGEON_TYPES_T1);
    DUNGEON_TYPES_CONFIG.put(2, DUNGEON_TYPES_T2);
    DUNGEON_TYPES_CONFIG.put(3, DUNGEON_TYPES_T3);
    DUNGEON_TYPES_CONFIG.put(4, DUNGEON_TYPES_T4);
    DUNGEON_TYPES_CONFIG.put(5, DUNGEON_TYPES_T5);
    DUNGEON_TYPES_CONFIG.put(6, DUNGEON_TYPES_T6);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Available settlement configurations:%n".formatted());
    for (var entry : SETTLEMENT_CONFIGS.entrySet()) {
      sb.append("- [%s=%s]%n".formatted(entry.getKey(), entry.getValue()));
    }
    for (var entry : DUNGEON_TYPES_CONFIG.entrySet()) {
      sb.append("- [%s=%s]%n".formatted(entry.getKey(), entry.getValue()));
    }
    return sb.toString();
  }

  /**
   * Returns a random Size enum. Must be provided with a Random in order to ensure reproducibility.
   */
  public static Size randomSize(Random random) {
    var randomNumber = random.nextInt(0, 21);
    return switch (randomNumber) {
      case 0, 1, 2, 3, 4, 5 -> Size.XS;
      case 6, 7, 8, 9, 10, 11, 12, 13, 14 -> Size.S;
      case 15, 16, 17 -> Size.M;
      case 18, 19 -> Size.L;
      default -> Size.XL;
    };
  }

  /** Returns a random float that expresses the area of a settlement in square kilometers. */
  public static int randomArea(Random random, Size size) {
    var bounds = SETTLEMENT_CONFIGS.get(size).getArea();
    return random.nextInt(bounds.getUpper() - bounds.getLower() + 1) + bounds.getLower();
  }

  public static void throwIfRepeatedRequest(Generatable generatable, boolean toBeLoaded) {
    if (generatable.isLoaded() == toBeLoaded) {
      throw new IllegalStateException(
          "Request to %s settlement '%s' even though it already is, check your logic"
              .formatted(toBeLoaded ? "loaded" : "unloaded", generatable.getId()));
    }
  }

  @Getter
  @Setter
  public static class SettlementConfig {
    private Bounds area;
    private Bounds inhabitants;
    private Map<Type, Bounds> amenities;

    @Override
    public String toString() {
      return "{area=" + area + ", inhabitants=" + inhabitants + ", amenities=" + amenities + '}';
    }
  }

  @Getter
  @Setter
  @ToString(includeFieldNames = false)
  public static class EnemyConfig {
    private Range health;
    private Range damage;
    private Range experience;
    private Range gold;
  }
}
