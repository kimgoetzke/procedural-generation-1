package com.hindsight.king_of_castrop_rauxel.location;

import static com.hindsight.king_of_castrop_rauxel.settings.LocationComponent.*;

import com.hindsight.king_of_castrop_rauxel.player.Player;
import java.util.*;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractSettlement extends AbstractLocation {

  protected final Random random = new Random();

  protected Size size;
  protected Player loyalTo;
  protected int inhabitants;
  protected List<Neighbour> neighbours = new ArrayList<>();
  protected List<AbstractAmenity> amenities = new ArrayList<>();

  @Override
  public String toString() {
    return "AbstractSettlement(super="
        + super.toString()
        + "), size="
        + size
        + ", loyalTo="
        + loyalTo
        + ", inhabitants="
        + inhabitants
        + ", neighbours="
        + neighbours.size()
        + ", amenities="
        + amenities.size();
  }

  @Override
  public String getSummary() {
    return "%s [ Size: %s | Inhabitants: %d | Amenities: %s | Neighbours: %s | %s ]"
        .formatted(
            name,
            size,
            inhabitants,
            amenities.size(),
            neighbours.size(),
            loyalTo == null ? "Neutral" : "Loyal to " + loyalTo.getName());
  }

  protected AbstractSettlement() {
    super();
  }
}
