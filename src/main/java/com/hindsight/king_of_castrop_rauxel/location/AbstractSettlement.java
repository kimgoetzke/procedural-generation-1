package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.utils.StringGenerator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

@Slf4j
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public abstract class AbstractSettlement extends AbstractLocation {

  @Getter protected final StringGenerator stringGenerator;
  @Getter protected Size size;
  protected Player loyalTo;
  protected int area;
  protected List<PointOfInterest> pointsOfInterests = new ArrayList<>();
  @Getter protected List<Npc> inhabitants = new ArrayList<>();
  @EqualsAndHashCode.Include @Getter protected int inhabitantCount;
  @Getter protected Set<Location> neighbours = new HashSet<>();

  protected AbstractSettlement(
      Pair<Integer, Integer> worldCoords,
      Pair<Integer, Integer> chunkCoords,
      StringGenerator stringGenerator) {
    super(worldCoords, chunkCoords);
    this.stringGenerator = stringGenerator;
    stringGenerator.setRandom(random);
  }

  public void addNeighbour(Location neighbour) {
    neighbours.add(neighbour);
  }

  public PointOfInterest getDefaultPoi() {
    return pointsOfInterests.stream()
        .filter(a -> a.getType() == AbstractAmenity.PoiType.MAIN_SQUARE)
        .findFirst()
        .orElse(null);
  }

  @Override
  public List<PointOfInterest> getPointsOfInterest() {
    return pointsOfInterests;
  }

  @Override
  public String toString() {
    return super.toString()
        + ", size="
        + size
        + ", loyalTo="
        + loyalTo
        + ", inhabitants="
        + inhabitants.size()
        + ", inhabitantCount="
        + inhabitantCount
        + ", neighbours="
        + neighbours.size()
        + ", amenities="
        + pointsOfInterests.size();
  }

  @Override
  public String getBriefSummary() {
    return "%s [ Size: %s | %s | Neighbours: %s | Generated: %s ]"
        .formatted(name, size, coordinates.toString(), neighbours.size(), isLoaded());
  }

  @Override
  public String getFullSummary() {
    return "%s [ Size: %s | %d inhabitants | Population density: %s | %s points of interest | Coordinates: %s | Connected to %s location(s) | Stance: %s ]"
        .formatted(
            name,
            size.getName(),
            inhabitantCount,
            getPopulationDensity(),
            pointsOfInterests.size(),
            coordinates.globalToString(),
            neighbours.size(),
            loyalTo == null ? "Neutral" : "Loyal to " + loyalTo.getName());
  }

  private String getPopulationDensity() {
    return String.format("%.1f", (float) inhabitants.size() / area) + "/km²";
  }
}
