package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.utils.StringGenerator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractSettlement extends AbstractLocation {

  protected final StringGenerator stringGenerator;

  protected Size size;
  protected Player loyalTo;
  protected float area;
  protected List<AbstractAmenity> amenities = new ArrayList<>();
  @Getter protected List<Npc> inhabitants = new ArrayList<>();
  @Getter protected List<Location> neighbours = new ArrayList<>();

  protected AbstractSettlement(StringGenerator stringGenerator) {
    super();
    this.stringGenerator = stringGenerator;
    stringGenerator.setRandom(random);
  }

  public void addNeighbour(Location neighbour) {
    neighbours.add(neighbour);
  }

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
    return "%s [ Size: %s | Inhabitants: %d | Density: %s | Amenities: %s | Neighbours: %s | %s ]"
        .formatted(
            name,
            size,
            inhabitants.size(),
            getPopulationDensity(),
            amenities.size(),
            neighbours.size(),
            loyalTo == null ? "Neutral" : "Loyal to " + loyalTo.getName());
  }

  private String getPopulationDensity() {
    DecimalFormat f = new DecimalFormat("##.00");
    return f.format(inhabitants.size() / area) + " / km²";
  }
}
