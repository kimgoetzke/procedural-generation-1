package com.hindsight.king_of_castrop_rauxel.settings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import static com.hindsight.king_of_castrop_rauxel.location.AbstractAmenity.AmenityType;
import static com.hindsight.king_of_castrop_rauxel.location.AbstractLocation.Size;

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

    xs.amenities = new EnumMap<>(AmenityType.class);
    s.amenities = new EnumMap<>(AmenityType.class);
    m.amenities = new EnumMap<>(AmenityType.class);
    l.amenities = new EnumMap<>(AmenityType.class);
    xl.amenities = new EnumMap<>(AmenityType.class);

    xs.amenities.put(AmenityType.ENTRANCE, new Bounds(0, 0));
    s.amenities.put(AmenityType.ENTRANCE, new Bounds(0, 1));
    m.amenities.put(AmenityType.ENTRANCE, new Bounds(2, 4));
    l.amenities.put(AmenityType.ENTRANCE, new Bounds(1, 1));
    xl.amenities.put(AmenityType.ENTRANCE, new Bounds(4, 8));

    xs.amenities.put(AmenityType.MAIN_SQUARE, new Bounds(1, 1));
    s.amenities.put(AmenityType.MAIN_SQUARE, new Bounds(1, 1));
    m.amenities.put(AmenityType.MAIN_SQUARE, new Bounds(1, 1));
    l.amenities.put(AmenityType.MAIN_SQUARE, new Bounds(1, 1));
    xl.amenities.put(AmenityType.MAIN_SQUARE, new Bounds(1, 1));

    xs.amenities.put(AmenityType.SHOP, new Bounds(0, 1));
    s.amenities.put(AmenityType.SHOP, new Bounds(1, 3));
    m.amenities.put(AmenityType.SHOP, new Bounds(3, 6));
    l.amenities.put(AmenityType.SHOP, new Bounds(5, 10));
    xl.amenities.put(AmenityType.SHOP, new Bounds(8, 14));

    xs.amenities.put(AmenityType.QUEST_LOCATION, new Bounds(0, 2));
    s.amenities.put(AmenityType.QUEST_LOCATION, new Bounds(2, 5));
    m.amenities.put(AmenityType.QUEST_LOCATION, new Bounds(3, 8));
    l.amenities.put(AmenityType.QUEST_LOCATION, new Bounds(6, 12));
    xl.amenities.put(AmenityType.QUEST_LOCATION, new Bounds(10, 20));

    settlementConfigs.put(Size.XS, xs);
    settlementConfigs.put(Size.S, s);
    settlementConfigs.put(Size.M, m);
    settlementConfigs.put(Size.L, l);
    settlementConfigs.put(Size.XL, xl);
    log.info(settlementConfigs.toString());
  }

  /**
   * Returns a random Size enum. Must be provided with a Random in order to ensure reproducibility.
   * TODO: Allow for more fine-grained control of probabilities.
   */
  public static Size randomSize(Random random) {
    var randomNumber = random.nextInt(0, 100) / 10;
    Size size =
        switch (Integer.toString(randomNumber)) {
          case "1", "2", "3", "4" -> Size.XS;
          case "5", "6" -> Size.S;
          case "7", "8" -> Size.M;
          case "9" -> Size.L;
          default -> Size.XL;
        };
    log.info("Set size to {} (derived from {})", size, randomNumber);
    return size;
  }

  @Getter
  @Setter
  public static class SettlementConfig {
    private Bounds inhabitants;
    private Map<AmenityType, Bounds> amenities;
  }

  /** Inclusive bounds for random number generation. */
  @Getter
  @Setter
  @AllArgsConstructor
  public static class Bounds {
    private int lower;
    private int upper;
  }
}
