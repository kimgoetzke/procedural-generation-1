package com.hindsight.king_of_castrop_rauxel.characters;

import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.encounter.Damage;
import com.hindsight.king_of_castrop_rauxel.event.Event;
import com.hindsight.king_of_castrop_rauxel.event.Loot;
import com.hindsight.king_of_castrop_rauxel.event.Reward;
import com.hindsight.king_of_castrop_rauxel.location.Location;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import com.hindsight.king_of_castrop_rauxel.world.Coordinates;
import com.hindsight.king_of_castrop_rauxel.world.IdBuilder;
import java.util.*;
import java.util.function.Predicate;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

@Slf4j
@Getter
public class Player implements Visitor, Combatant {

  private final String id;
  private final String name;
  private final Enemy.Type type = Enemy.Type.PLAYER;
  private final Set<Location> visitedLocations = new LinkedHashSet<>();
  private final List<Event> events = new ArrayList<>();
  private final Coordinates coordinates;
  private final Pair<Integer, Integer> startCoordinates;
  private final Random random = new Random();
  private final AppProperties.PlayerProperties playerProperties;
  private final AppProperties.GameProperties gameProperties;
  private int gold;
  private int health;
  private int maxHealth;
  private int experience = 0;
  private int level = 1;
  private Damage damage;
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
    IN_MENU,
    DEBUGGING
  }

  public Player(
      @NonNull String name,
      @NonNull Location currentLocation,
      @NonNull AppProperties appProperties) {
    this.name = name;
    this.coordinates = Coordinates.of(currentLocation.getCoordinates());
    this.startCoordinates = coordinates.getGlobal();
    this.id = IdBuilder.idFrom(this.getClass(), name, coordinates);
    this.currentLocation = currentLocation;
    this.currentPoi = currentLocation.getDefaultPoi();
    this.gameProperties = appProperties.getGameProperties();
    this.playerProperties = appProperties.getPlayerProperties();
    this.gold = playerProperties.startingGold();
    this.health = playerProperties.startingMaxHealth();
    this.maxHealth = playerProperties.startingMaxHealth();
    this.damage = playerProperties.startingDamage();
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

  public void addExperience(int amount) {
    this.experience += amount;
    if (experience >= playerProperties.experienceToLevelUp()) {
      level++;
      experience = experience % playerProperties.experienceToLevelUp();
    }
  }

  public void changeGoldBy(int amount) {
    this.gold += amount;
  }

  public void changeHealthBy(int health) {
    this.health = Math.max(0, Math.min(maxHealth, this.health + health));
  }

  public void setHealth(int health) {
    this.health = health;
  }

  public void changeMaxHealthBy(int maxHealth) {
    this.maxHealth += maxHealth;
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
    return events.stream().filter(isActiveOrReady()).toList();
  }

  private static Predicate<Event> isActiveOrReady() {
    return e -> e.getEventState() == Event.State.ACTIVE || e.getEventState() == Event.State.READY;
  }
}
