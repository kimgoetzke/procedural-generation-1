package com.hindsight.king_of_castrop_rauxel.graphs;

import com.hindsight.king_of_castrop_rauxel.location.Location;
import com.hindsight.king_of_castrop_rauxel.world.Coordinates;

public record LocationDto(String id, String name, Coordinates coordinates) {

  static LocationDto from(Location location) {
    return new LocationDto(location.getId(), location.getName(), location.getCoordinates());
  }

  public String getSummary() {
    return "%s: %s at %s".formatted(id, name, coordinates.globalToString());
  }

  public int distanceTo(Location other) {
    return coordinates.distanceTo(other.getCoordinates());
  }

  public int distanceTo(LocationDto other) {
    return coordinates.distanceTo(other.coordinates());
  }
}
