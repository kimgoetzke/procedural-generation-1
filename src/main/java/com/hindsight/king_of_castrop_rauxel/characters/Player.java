package com.hindsight.king_of_castrop_rauxel.characters;

import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import com.hindsight.king_of_castrop_rauxel.location.Location;
import com.hindsight.king_of_castrop_rauxel.world.Coordinates;
import java.util.HashSet;
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
  private final Set<Location> visitedLocations = new HashSet<>();
  private final Coordinates coordinates;
  @Setter private int gold;
  @Setter private int level;
  @Setter private int age;
  @Setter private int activityPoints;
  @Setter @Getter private State state = State.AT_DEFAULT_POI;
  private Location currentLocation;
  private PointOfInterest currentPoi;

  public Player(
      String name, @NonNull Location currentLocation, Pair<Integer, Integer> worldCoordinates) {
    this.name = name;
    this.id = "PLA~" + UUID.randomUUID();
    this.coordinates = new Coordinates(worldCoordinates, currentLocation.getChunkCoords());
    this.currentLocation = currentLocation;
    this.currentPoi = currentLocation.getDefaultPoi();
  }

  public void setCurrentPoi(PointOfInterest currentPoi) {
    var location = currentPoi.getParent();
    this.currentPoi = currentPoi;
    this.currentLocation = location;
    visitedLocations.add(this.currentLocation);
    location.addVisitor(this);
    updateCoordinates(location.getGlobalCoords());
  }

  private void updateCoordinates(Pair<Integer, Integer> globalCoords) {
    this.coordinates.setTo(globalCoords);
  }

  public enum State {
    AT_DEFAULT_POI,
    CHOOSE_POI,
    AT_SPECIFIC_POI,
    DEBUG
  }
}
