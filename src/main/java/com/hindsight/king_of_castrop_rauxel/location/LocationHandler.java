package com.hindsight.king_of_castrop_rauxel.location;

import static com.hindsight.king_of_castrop_rauxel.location.PointOfInterest.Type;

import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.world.Bounds;
import com.hindsight.king_of_castrop_rauxel.world.Generatable;
import java.util.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocationHandler {

  private final AppProperties appProperties;
  private final LocationFactory locationFactory;

  private static final Map<Size, SettlementConfig> SETTLEMENT_CONFIGS = new EnumMap<>(Size.class);

  public LocationHandler(AppProperties appProperties, LocationFactory locationFactory) {
    this.appProperties = appProperties;
    this.locationFactory = locationFactory;
    configureSettlements();
    log.debug(this.toString());
  }

  public static SettlementConfig getSettlementConfig(Size size) {
    return SETTLEMENT_CONFIGS.get(size);
  }

  private void configureSettlements() {
    SETTLEMENT_CONFIGS.put(Size.XS, appProperties.getSettlementProperties().xs());
    SETTLEMENT_CONFIGS.put(Size.S, appProperties.getSettlementProperties().s());
    SETTLEMENT_CONFIGS.put(Size.M, appProperties.getSettlementProperties().m());
    SETTLEMENT_CONFIGS.put(Size.L, appProperties.getSettlementProperties().l());
    SETTLEMENT_CONFIGS.put(Size.XL, appProperties.getSettlementProperties().xl());
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

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Available settlement configurations:%n".formatted());
    for (var entry : SETTLEMENT_CONFIGS.entrySet()) {
      sb.append("- [%s=%s]%n".formatted(entry.getKey(), entry.getValue()));
    }
    return sb.toString();
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
}
