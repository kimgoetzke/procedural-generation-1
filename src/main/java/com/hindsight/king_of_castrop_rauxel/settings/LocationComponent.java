package com.hindsight.king_of_castrop_rauxel.settings;

import static com.hindsight.king_of_castrop_rauxel.location.AbstractAmenity.AmenityType;
import static com.hindsight.king_of_castrop_rauxel.location.AbstractLocation.Size;

import com.hindsight.king_of_castrop_rauxel.location.Location;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LocationComponent {

  @Getter
  private static final Map<Size, SettlementConfig> settlementConfigs = new EnumMap<>(Size.class);

  public static final float PLANET_RADIUS = 6371.0F;
  public static final int CHUNK_SIZE = 500;
  @Getter private static final Bounds<Integer> chunkDensity = new Bounds<>(5, 50);

  public static final int MAX_DISTANCE_FROM_START = 500;
  @Getter private static final Bounds<Integer> settlementNeighbours = new Bounds<>(0, 4);
  @Getter private static final Bounds<Integer> settlementDistance = new Bounds<>(5, 500);

  public LocationComponent() {
    initialiseSettlementConfigurations();
  }

  private void initialiseSettlementConfigurations() {
    SettlementConfig xs = new SettlementConfig();
    SettlementConfig s = new SettlementConfig();
    SettlementConfig m = new SettlementConfig();
    SettlementConfig l = new SettlementConfig();
    SettlementConfig xl = new SettlementConfig();

    xs.inhabitants = new Bounds<>(1, 10);
    s.inhabitants = new Bounds<>(11, 100);
    m.inhabitants = new Bounds<>(101, 1000);
    l.inhabitants = new Bounds<>(1001, 10000);
    xl.inhabitants = new Bounds<>(10000, 100000);

    xs.area = new Bounds<>(0.01F, 0.02F);
    s.area = new Bounds<>(0.02F, 0.2F);
    m.area = new Bounds<>(0.5F, 2F);
    l.area = new Bounds<>(2F, 10F);
    xl.area = new Bounds<>(15F, 40F);

    xs.amenities = new EnumMap<>(AmenityType.class);
    s.amenities = new EnumMap<>(AmenityType.class);
    m.amenities = new EnumMap<>(AmenityType.class);
    l.amenities = new EnumMap<>(AmenityType.class);
    xl.amenities = new EnumMap<>(AmenityType.class);

    xs.amenities.put(AmenityType.ENTRANCE, new Bounds<>(0, 0));
    s.amenities.put(AmenityType.ENTRANCE, new Bounds<>(0, 1));
    m.amenities.put(AmenityType.ENTRANCE, new Bounds<>(2, 4));
    l.amenities.put(AmenityType.ENTRANCE, new Bounds<>(1, 1));
    xl.amenities.put(AmenityType.ENTRANCE, new Bounds<>(4, 8));

    xs.amenities.put(AmenityType.MAIN_SQUARE, new Bounds<>(1, 1));
    s.amenities.put(AmenityType.MAIN_SQUARE, new Bounds<>(1, 1));
    m.amenities.put(AmenityType.MAIN_SQUARE, new Bounds<>(1, 1));
    l.amenities.put(AmenityType.MAIN_SQUARE, new Bounds<>(1, 1));
    xl.amenities.put(AmenityType.MAIN_SQUARE, new Bounds<>(1, 1));

    xs.amenities.put(AmenityType.SHOP, new Bounds<>(0, 1));
    s.amenities.put(AmenityType.SHOP, new Bounds<>(1, 3));
    m.amenities.put(AmenityType.SHOP, new Bounds<>(3, 6));
    l.amenities.put(AmenityType.SHOP, new Bounds<>(5, 10));
    xl.amenities.put(AmenityType.SHOP, new Bounds<>(8, 14));

    xs.amenities.put(AmenityType.QUEST_LOCATION, new Bounds<>(0, 2));
    s.amenities.put(AmenityType.QUEST_LOCATION, new Bounds<>(2, 5));
    m.amenities.put(AmenityType.QUEST_LOCATION, new Bounds<>(3, 8));
    l.amenities.put(AmenityType.QUEST_LOCATION, new Bounds<>(6, 12));
    xl.amenities.put(AmenityType.QUEST_LOCATION, new Bounds<>(10, 20));

    settlementConfigs.put(Size.XS, xs);
    settlementConfigs.put(Size.S, s);
    settlementConfigs.put(Size.M, m);
    settlementConfigs.put(Size.L, l);
    settlementConfigs.put(Size.XL, xl);
    log.info(settlementConfigs.toString());
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
  public static float randomArea(Random random, Size size) {
    var bounds = settlementConfigs.get(size).getArea();
    return random.nextFloat() * (bounds.getUpper() - bounds.getLower()) + bounds.getLower();
  }

  public static int randomNeighboursCount(Random random) {
    return random.nextInt(settlementNeighbours.getUpper() - settlementNeighbours.getLower() + 1)
        + settlementNeighbours.getLower();
  }

  public static int randomSettlementDistance(Random random) {
    return random.nextInt(settlementDistance.getUpper() - settlementDistance.getLower() + 1)
        + settlementDistance.getLower();
  }

  /**
   * Calculates the distance between two locations in kilometers using the Haversine formula. See <a
   * href="https://www.geeksforgeeks.org/haversine-formula-to-find-distance-between-two-points-on-a-sphere/">this
   * example</a>.
   */
  public static double calculateDistance(Location location1, Location location2) {
    double lat1 = Math.toRadians(location1.getCoordinates().getFirst());
    double lon1 = Math.toRadians(location1.getCoordinates().getSecond());
    double lat2 = Math.toRadians(location2.getCoordinates().getFirst());
    double lon2 = Math.toRadians(location2.getCoordinates().getSecond());
    double dLat = lat2 - lat1;
    double dLon = lon2 - lon1;
    double a =
        Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return PLANET_RADIUS * c;
  }

  @Getter
  @Setter
  public static class SettlementConfig {
    private Bounds<Float> area;
    private Bounds<Integer> inhabitants;
    private Map<AmenityType, Bounds<Integer>> amenities;
  }

  /** Inclusive bounds for random number generation. */
  @Getter
  @Setter
  @AllArgsConstructor
  public static class Bounds<T> {
    private T lower;
    private T upper;
  }
}
