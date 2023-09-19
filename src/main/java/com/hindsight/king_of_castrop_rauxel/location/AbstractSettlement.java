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
  @Getter protected Set<Location> neighbours = new HashSet<>();

  protected AbstractSettlement(
      StringGenerator stringGenerator, Pair<Integer, Integer> chunkCoords) {
    super(chunkCoords);
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
        + pointsOfInterests.size();
  }

  @Override
  public String getFullSummary() {
    return "%s [ Size: %s | %d inhabitants | Population density: %s | %s points of interest | Located at %s | Connected to %s location(s) | Stance: %s ]"
        .formatted(
            name,
            size.getName(),
            inhabitants.size(),
            getPopulationDensity(),
            pointsOfInterests.size(),
            "(%s, %s)".formatted(getChunkCoords().getFirst(), getChunkCoords().getSecond()),
            neighbours.size(),
            loyalTo == null ? "Neutral" : "Loyal to " + loyalTo.getName());
  }

  @Override
  public String getBriefSummary() {
    return "%s [ Size: %s | Location: %s | Connected to %s location(s) | Generated: %s ]"
        .formatted(
            name,
            size.getName(),
            "(%s, %s)".formatted(getChunkCoords().getFirst(), getChunkCoords().getSecond()),
            neighbours.size(),
            isLoaded());
  }

  private String getPopulationDensity() {
    return String.format("%.1f", (float) inhabitants.size() / area) + "/km²";
  }
}
