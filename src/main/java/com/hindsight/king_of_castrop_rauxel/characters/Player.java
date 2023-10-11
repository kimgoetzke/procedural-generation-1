package com.hindsight.king_of_castrop_rauxel.characters;

import com.hindsight.king_of_castrop_rauxel.event.Event;
import com.hindsight.king_of_castrop_rauxel.location.Location;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import com.hindsight.king_of_castrop_rauxel.world.Coordinates;

import java.util.*;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

@Slf4j
@Getter
public class Player implements Visitor {

  private final String id;
  private final String name;
  private final Set<Location> visitedLocations = new LinkedHashSet<>();
  private final List<Event> events = new ArrayList<>();
  private final Coordinates coordinates;
  private int gold = 100;
  private int experience = 0;
  private int age = 15;
  private int activityPoints = 20;
  private State state = State.AT_LOCATION;
  private Location currentLocation;
  private PointOfInterest currentPoi;
  @Setter private Event currentEvent;

  public enum State {
    AT_LOCATION,
    CHOOSE_POI,
    AT_POI,
    DIALOGUE,
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

  public void addEvent(Event event) {
    events.add(event);
  }

  public void addGold(int amount) {
    this.gold += amount;
  }

  public void addExperience(int amount) {
    this.experience -= amount;
  }

  public void setState(State state) {
    this.state = state;
    log.info("Updating CLI state to {}", state);
  }

  public List<Event> getActiveEvents() {
    return events.stream().filter(e -> e.getEventState() == Event.State.ACTIVE).toList();
  }
}
