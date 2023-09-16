package com.hindsight.king_of_castrop_rauxel.characters;

import com.hindsight.king_of_castrop_rauxel.location.Location;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class Player implements Visitor {
  private final String id;
  private final String name;
  @Setter private int gold;
  @Setter private int level;
  @Setter private int age;
  @Setter private int activityPoints;
  @Setter @Getter private State state = State.AT_DEFAULT_POI;
  private Location currentLocation;
  @Setter private PointOfInterest currentPoi;
  private final Set<Location> visitedLocations = new HashSet<>();

  public Player(String name, @NonNull Location currentLocation) {
    this.name = name;
    id = UUID.randomUUID().toString();
    setCurrentLocation(currentLocation);
    setCurrentPoi(currentLocation.getDefaultPoi());
  }

  public void setCurrentLocation(Location currentLocation) {
    visitedLocations.add(this.currentLocation);
    currentLocation.addVisitor(this);
    this.currentLocation = currentLocation;
  }

  public enum State {
    AT_DEFAULT_POI,
    INSIDE_LOCATION,
    AT_SPECIFIC_POI
  }
}
