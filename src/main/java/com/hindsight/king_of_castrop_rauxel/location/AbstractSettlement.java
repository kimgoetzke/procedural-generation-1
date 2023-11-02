package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.action.PoiAction;
import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.utils.DataServices;
import com.hindsight.king_of_castrop_rauxel.utils.Generators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

@Slf4j
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public abstract class AbstractSettlement extends AbstractLocation {

  @Getter protected final Generators generators;
  @Getter protected final DataServices dataServices;
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
      Generators generators,
      DataServices dataServices,
      AppProperties appProperties,
      PoiFactory poiFactory) {
    super(worldCoords, chunkCoords, appProperties, poiFactory);
    this.generators = generators;
    this.dataServices = dataServices;
    generators.initialiseAll(random);
  }

  public void addNeighbour(Location neighbour) {
    neighbours.add(neighbour);
  }

  public PointOfInterest getDefaultPoi() {
    return pointsOfInterests.stream()
        .filter(a -> a.getType() == PointOfInterest.Type.MAIN_SQUARE)
        .findFirst()
        .orElse(null);
  }

  @Override
  public List<Action> getAvailableActions() {
    return availableActions.stream().filter(hasActionsOrIsMainSquare()).toList();
  }

  private static Predicate<Action> hasActionsOrIsMainSquare() {
    return a -> {
      if (!(a instanceof PoiAction pa)) return false;
      var isMainSquare = pa.getPoi().getType() == PointOfInterest.Type.MAIN_SQUARE;
      return !pa.getPoi().getAvailableActions().isEmpty() || isMainSquare;
    };
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
    return String.format("%.1f", (float) inhabitants.size() / area) + "/sq. km";
  }
}
