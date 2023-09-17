package com.hindsight.king_of_castrop_rauxel.location;

import static com.hindsight.king_of_castrop_rauxel.location.AbstractAmenity.PoiType;
import static com.hindsight.king_of_castrop_rauxel.location.AbstractLocation.Size;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import com.hindsight.king_of_castrop_rauxel.world.Bounds;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LocationComponent {

  @Getter
  private static final Map<Size, SettlementConfig> settlementConfigs = new EnumMap<>(Size.class);

  public LocationComponent() {
    initialiseSettlementConfigurations();
  }

  private void initialiseSettlementConfigurations() {
    SettlementConfig xs = new SettlementConfig();
    SettlementConfig s = new SettlementConfig();
    SettlementConfig m = new SettlementConfig();
    SettlementConfig l = new SettlementConfig();
    SettlementConfig xl = new SettlementConfig();

    xs.inhabitants = new Bounds(1, 10);
    s.inhabitants = new Bounds(11, 100);
    m.inhabitants = new Bounds(101, 1000);
    l.inhabitants = new Bounds(1001, 10000);
    xl.inhabitants = new Bounds(10000, 100000);

    // Area occupied by the settlement in square kilometers; will be multiplied by 1000 to get
    // square meters for locations of amenities inside the settlement
    xs.area = new Bounds(1, 1);
    s.area = new Bounds(1, 2);
    m.area = new Bounds(1, 3);
    l.area = new Bounds(2, 8);
    xl.area = new Bounds(15, 40);

    xs.amenities = new EnumMap<>(PoiType.class);
    s.amenities = new EnumMap<>(PoiType.class);
    m.amenities = new EnumMap<>(PoiType.class);
    l.amenities = new EnumMap<>(PoiType.class);
    xl.amenities = new EnumMap<>(PoiType.class);

    xs.amenities.put(PoiType.ENTRANCE, new Bounds(0, 0));
    s.amenities.put(PoiType.ENTRANCE, new Bounds(0, 1));
    m.amenities.put(PoiType.ENTRANCE, new Bounds(2, 4));
    l.amenities.put(PoiType.ENTRANCE, new Bounds(1, 1));
    xl.amenities.put(PoiType.ENTRANCE, new Bounds(4, 8));

    xs.amenities.put(PoiType.MAIN_SQUARE, new Bounds(1, 1));
    s.amenities.put(PoiType.MAIN_SQUARE, new Bounds(1, 1));
    m.amenities.put(PoiType.MAIN_SQUARE, new Bounds(1, 1));
    l.amenities.put(PoiType.MAIN_SQUARE, new Bounds(1, 1));
    xl.amenities.put(PoiType.MAIN_SQUARE, new Bounds(1, 1));

    xs.amenities.put(PoiType.SHOP, new Bounds(0, 1));
    s.amenities.put(PoiType.SHOP, new Bounds(1, 3));
    m.amenities.put(PoiType.SHOP, new Bounds(3, 6));
    l.amenities.put(PoiType.SHOP, new Bounds(5, 10));
    xl.amenities.put(PoiType.SHOP, new Bounds(8, 14));

    xs.amenities.put(PoiType.QUEST_LOCATION, new Bounds(0, 2));
    s.amenities.put(PoiType.QUEST_LOCATION, new Bounds(2, 5));
    m.amenities.put(PoiType.QUEST_LOCATION, new Bounds(3, 8));
    l.amenities.put(PoiType.QUEST_LOCATION, new Bounds(6, 12));
    xl.amenities.put(PoiType.QUEST_LOCATION, new Bounds(10, 20));

    settlementConfigs.put(Size.XS, xs);
    settlementConfigs.put(Size.S, s);
    settlementConfigs.put(Size.M, m);
    settlementConfigs.put(Size.L, l);
    settlementConfigs.put(Size.XL, xl);
    log.debug(this.toString());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Available settlement configurations:%n".formatted());
    for (var entry : settlementConfigs.entrySet()) {
      sb.append("- [%s=%s]%n".formatted(entry.getKey(), entry.getValue()));
    }
    return sb.toString();
  }

  /**
   * Returns a random Size enum. Must be provided with a Random in order to ensure reproducibility.
   */
  public static Size randomSize(Random random) {
    var randomNumber = random.nextInt(0, 21);
    Size size =
        switch (randomNumber) {
          case 0, 1, 2, 3, 4, 5 -> Size.XS;
          case 6, 7, 8, 9, 10, 11, 12, 13, 14 -> Size.S;
          case 15, 16, 17 -> Size.M;
          case 18, 19 -> Size.L;
          default -> Size.XL;
        };
    log.info("Set size to {}", size);
    return size;
  }

  /** Returns a random float that expresses the area of a settlement in square kilometers. */
  public static int randomArea(Random random, Size size) {
    var bounds = settlementConfigs.get(size).getArea();
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
