package com.hindsight.king_of_castrop_rauxel.player;

import com.hindsight.king_of_castrop_rauxel.location.Location;
import com.hindsight.king_of_castrop_rauxel.utils.Visitor;
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
  private Location currentLocation;
  private Set<Location> visitedLocations = new HashSet<>();

  public Player(String name, @NonNull Location currentLocation) {
    this.name = name;
    id = UUID.randomUUID().toString();
    setCurrentLocation(currentLocation);
  }

  public void setCurrentLocation(Location currentLocation) {
    visitedLocations.add(this.currentLocation);
    this.currentLocation = currentLocation;
  }
}
