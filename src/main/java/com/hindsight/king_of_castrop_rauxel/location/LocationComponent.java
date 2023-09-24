package com.hindsight.king_of_castrop_rauxel.location;

import static com.hindsight.king_of_castrop_rauxel.location.AbstractAmenity.PoiType;
import static com.hindsight.king_of_castrop_rauxel.location.AbstractLocation.Size;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.world.Bounds;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LocationComponent {

  private static final Map<Size, SettlementConfig> SETTLEMENT_CONFIGS = new EnumMap<>(Size.class);

  public LocationComponent() {
    configureSettlements();
    log.debug(this.toString());
  }

  public static Map<Size, SettlementConfig> getSettlementConfigs() {
    return SETTLEMENT_CONFIGS;
  }

  private void configureSettlements() {
    SettlementConfig xs = new SettlementConfig();
    SettlementConfig s = new SettlementConfig();
    SettlementConfig m = new SettlementConfig();
    SettlementConfig l = new SettlementConfig();
    SettlementConfig xl = new SettlementConfig();

    xs.setInhabitants(AppProperties.XS_INHABITANTS);
    s.setInhabitants(AppProperties.S_INHABITANTS);
    m.setInhabitants(AppProperties.M_INHABITANTS);
    l.setInhabitants(AppProperties.L_INHABITANTS);
    xl.setInhabitants(AppProperties.XL_INHABITANTS);

    xs.area = AppProperties.XS_AREA;
    s.area = AppProperties.S_AREA;
    m.area = AppProperties.M_AREA;
    l.area = AppProperties.L_AREA;
    xl.area = AppProperties.XL_AREA;

    xs.amenities = new EnumMap<>(PoiType.class);
    s.amenities = new EnumMap<>(PoiType.class);
    m.amenities = new EnumMap<>(PoiType.class);
    l.amenities = new EnumMap<>(PoiType.class);
    xl.amenities = new EnumMap<>(PoiType.class);

    xs.amenities.put(PoiType.ENTRANCE, AppProperties.XS_AMENITIES_ENTRANCE);
    s.amenities.put(PoiType.ENTRANCE, AppProperties.S_AMENITIES_ENTRANCE);
    m.amenities.put(PoiType.ENTRANCE, AppProperties.M_AMENITIES_ENTRANCE);
    l.amenities.put(PoiType.ENTRANCE, AppProperties.L_AMENITIES_ENTRANCE);
    xl.amenities.put(PoiType.ENTRANCE, AppProperties.XL_AMENITIES_ENTRANCE);

    xs.amenities.put(PoiType.MAIN_SQUARE, AppProperties.XS_AMENITIES_MAIN_SQUARE);
    s.amenities.put(PoiType.MAIN_SQUARE, AppProperties.S_AMENITIES_MAIN_SQUARE);
    m.amenities.put(PoiType.MAIN_SQUARE, AppProperties.M_AMENITIES_MAIN_SQUARE);
    l.amenities.put(PoiType.MAIN_SQUARE, AppProperties.L_AMENITIES_MAIN_SQUARE);
    xl.amenities.put(PoiType.MAIN_SQUARE, AppProperties.XL_AMENITIES_MAIN_SQUARE);

    xs.amenities.put(PoiType.SHOP, AppProperties.XS_AMENITIES_SHOP);
    s.amenities.put(PoiType.SHOP, AppProperties.S_AMENITIES_SHOP);
    m.amenities.put(PoiType.SHOP, AppProperties.M_AMENITIES_SHOP);
    l.amenities.put(PoiType.SHOP, AppProperties.L_AMENITIES_SHOP);
    xl.amenities.put(PoiType.SHOP, AppProperties.XL_AMENITIES_SHOP);

    xs.amenities.put(PoiType.QUEST_LOCATION, AppProperties.XS_AMENITIES_QUEST_LOCATION);
    s.amenities.put(PoiType.QUEST_LOCATION, AppProperties.S_AMENITIES_QUEST_LOCATION);
    m.amenities.put(PoiType.QUEST_LOCATION, AppProperties.M_AMENITIES_QUEST_LOCATION);
    l.amenities.put(PoiType.QUEST_LOCATION, AppProperties.L_AMENITIES_QUEST_LOCATION);
    xl.amenities.put(PoiType.QUEST_LOCATION, AppProperties.XL_AMENITIES_QUEST_LOCATION);

    LocationComponent.SETTLEMENT_CONFIGS.put(Size.XS, xs);
    LocationComponent.SETTLEMENT_CONFIGS.put(Size.S, s);
    LocationComponent.SETTLEMENT_CONFIGS.put(Size.M, m);
    LocationComponent.SETTLEMENT_CONFIGS.put(Size.L, l);
    LocationComponent.SETTLEMENT_CONFIGS.put(Size.XL, xl);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Available settlement configurations:%n".formatted());
    for (var entry : SETTLEMENT_CONFIGS.entrySet()) {
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

  // TODO: Add dungeons, caves or similar as non-amenity POIs
  @Getter
  @Setter
  public static class SettlementConfig {
    private Bounds area;
    private Bounds inhabitants;
    private Map<PoiType, Bounds> amenities;

    @Override
    public String toString() {
      return "{area=" + area + ", inhabitants=" + inhabitants + ", amenities=" + amenities + '}';
    }
  }
}
