package com.hindsight.king_of_castrop_rauxel.characters;

import com.hindsight.king_of_castrop_rauxel.location.Location;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.util.Pair;

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
  @Getter @Setter private Pair<Integer, Integer> worldCoords;
  private Location currentLocation;
  private PointOfInterest currentPoi;
  private final Set<Location> visitedLocations = new HashSet<>();

  public Player(
      String name, @NonNull Location currentLocation, Pair<Integer, Integer> worldCoordinates) {
    this.name = name;
    this.id = UUID.randomUUID().toString();
    this.currentLocation = currentLocation;
    this.currentPoi = currentLocation.getDefaultPoi();
    this.worldCoords = worldCoordinates;
  }

  public void setCurrentPoi(PointOfInterest currentPoi) {
    var location = currentPoi.getParent();
    this.currentPoi = currentPoi;
    this.currentLocation = location;
    visitedLocations.add(this.currentLocation);
    location.addVisitor(this);
  }

  public Pair<Integer, Integer> getChunkCoords() {
    return currentLocation.getChunkCoords();
  }

  public enum State {
    AT_DEFAULT_POI,
    CHOOSE_POI,
    AT_SPECIFIC_POI,
    DEBUG
  }
}
