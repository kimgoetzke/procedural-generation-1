package com.hindsight.king_of_castrop_rauxel.characters;

import com.hindsight.king_of_castrop_rauxel.encounter.Damage;
import com.hindsight.king_of_castrop_rauxel.event.Event;
import com.hindsight.king_of_castrop_rauxel.event.Loot;
import com.hindsight.king_of_castrop_rauxel.event.Reward;
import com.hindsight.king_of_castrop_rauxel.location.Location;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import com.hindsight.king_of_castrop_rauxel.world.Coordinates;

import java.util.*;

import com.hindsight.king_of_castrop_rauxel.world.IdBuilder;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

@Slf4j
@Getter
public class Player implements Visitor, Combatant {

  private final String id;
  private final String name;
  private final Set<Location> visitedLocations = new LinkedHashSet<>();
  private final List<Event> events = new ArrayList<>();
  private final Coordinates coordinates;
  private final Pair<Integer, Integer> startCoordinates;
  private final Random random = new Random();
  private int gold = 100;
  private int health = 100;
  private int experience = 0;
  private int level = 1;
  private Damage damage = Damage.of(1, 4);
  private State previousState = State.AT_POI;
  private State state = State.AT_POI;
  private Location currentLocation;
  private PointOfInterest currentPoi;
  @Setter private Event currentEvent;
  @Setter private Combatant target;

  public enum State {
    CHOOSING_POI,
    AT_POI,
    IN_DIALOGUE,
    IN_COMBAT,
    DEBUGGING
  }

  public Player(
      String name, @NonNull Location currentLocation, Pair<Integer, Integer> worldCoords) {
    this.name = name;
    this.coordinates = new Coordinates(worldCoords, currentLocation.getCoordinates().getChunk());
    this.startCoordinates = coordinates.getGlobal();
    this.id = IdBuilder.idFrom(this.getClass(), coordinates);
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
    this.experience += amount;
  }

  public void setHealth(int health) {
    this.health = health;
  }

  @Override
  public Loot getLoot() {
    gold = 0;
    return new Loot(List.of(new Reward(Reward.Type.GOLD, gold)));
  }

  @Override
  public int attack(Combatant target) {
    if (target == null) {
      return 0;
    }
    var actualDamage = damage.actual(random);
    target.takeDamage(actualDamage);
    return actualDamage;
  }

  public void setState(State state) {
    this.previousState = this.state;
    this.state = state;
    log.info("Updating CLI state to {}", state);
  }

  public boolean hasCurrentEvent() {
    return currentEvent != null;
  }

  public List<Event> getActiveEvents() {
    return events.stream().filter(e -> e.getEventState() == Event.State.ACTIVE).toList();
  }
}
