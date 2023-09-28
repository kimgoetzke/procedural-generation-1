package com.hindsight.king_of_castrop_rauxel.characters;

import com.hindsight.king_of_castrop_rauxel.location.Location;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import com.hindsight.king_of_castrop_rauxel.world.Coordinates;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.util.Pair;

@Getter
public class Player implements Visitor {

  private final String id;
  private final String name;
  private final Set<Location> visitedLocations = new LinkedHashSet<>();
  private final Coordinates coordinates;
  @Setter private int gold = 100;
  @Setter private int level;
  @Setter private int age = 15;
  @Setter private int activityPoints = 20;
  @Setter @Getter private State state = State.AT_DEFAULT_POI;
  private Location currentLocation;
  private PointOfInterest currentPoi;

  public enum State {
    AT_DEFAULT_POI,
    CHOOSE_POI,
    AT_SPECIFIC_POI,
    DEBUG
  }

  public Player(
      String name, @NonNull Location currentLocation, Pair<Integer, Integer> worldCoords) {
    this.name = name;
    this.id = "PLA~" + UUID.randomUUID();
    this.coordinates = new Coordinates(worldCoords, currentLocation.getCoordinates().getChunk());
    this.currentLocation = currentLocation;
    this.currentPoi = currentLocation.getDefaultPoi();
    visitedLocations.add(currentLocation);
    currentLocation.addVisitor(this);
  }

  public void setCurrentPoi(PointOfInterest currentPoi) {
    var location = currentPoi.getParent();
    this.currentLocation = location;
    this.currentPoi = currentPoi;
    this.coordinates.setTo(location.getCoordinates().getGlobal());
    visitedLocations.add(location);
    location.addVisitor(this);
  }
}
