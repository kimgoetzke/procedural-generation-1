package com.hindsight.king_of_castrop_rauxel.location;

import static com.hindsight.king_of_castrop_rauxel.settings.LocationComponent.*;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.characters.Player;
import java.util.*;

import com.hindsight.king_of_castrop_rauxel.settings.SeedComponent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractSettlement extends AbstractLocation {

  protected final Random random = new Random(SeedComponent.SEED);

  protected Size size;
  protected Player loyalTo;
  @Getter protected List<Npc> inhabitants = new ArrayList<>();
  protected List<Location> neighbours = new ArrayList<>();
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
        + inhabitants.size()
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
            inhabitants.size(),
            amenities.size(),
            neighbours.size(),
            loyalTo == null ? "Neutral" : "Loyal to " + loyalTo.getName());
  }

  protected AbstractSettlement() {
    super();
  }
}
