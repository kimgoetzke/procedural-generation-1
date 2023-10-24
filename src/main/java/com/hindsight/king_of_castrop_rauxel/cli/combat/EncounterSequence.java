package com.hindsight.king_of_castrop_rauxel.cli.combat;

import com.hindsight.king_of_castrop_rauxel.characters.BasicEnemy;
import com.hindsight.king_of_castrop_rauxel.characters.Combatant;
import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.event.Event;
import com.hindsight.king_of_castrop_rauxel.location.DungeonDetails;
import com.hindsight.king_of_castrop_rauxel.location.LocationBuilder;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import com.hindsight.king_of_castrop_rauxel.utils.Generators;
import com.hindsight.king_of_castrop_rauxel.world.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString(exclude = {"random", "isLoaded", "encounters", "currentEncounter", "parent", "state"})
public class EncounterSequence implements Generatable {

  @Getter private final String id;
  private final Random random;
  private final Generators generators;
  private final PointOfInterest parent;
  private final List<Encounter> encounters = new ArrayList<>();
  private final Coordinates coordinates;
  @Getter @Setter private boolean isLoaded;
  @Getter private DungeonDetails dungeonDetails;
  private int currentEncounter = 0;
  @Getter private Event.State state = Event.State.AVAILABLE;

  public EncounterSequence(PointOfInterest parent) {
    var parentCoords = parent.getParent().getCoordinates();
    var seed = SeedBuilder.seedFrom(parentCoords.getGlobal());
    this.coordinates = parentCoords;
    this.random = new Random(seed);
    this.generators = parent.getParent().getGenerators();
    this.id = IdBuilder.idFrom(this.getClass(), parent.getId());
    this.parent = parent;
    load();
  }

  // TODO: Procedurally generate and abstract away everything necessary
  @Override
  public void load() {
    LocationBuilder.throwIfRepeatedRequest(this, true);
    var targetLevel = calculateTargetLevel();
    this.dungeonDetails = DungeonDetails.load(random, targetLevel);
    for (int i = 0; i < dungeonDetails.encounters(); i++) {
      var count = random.nextInt(2) + 1;
      var enemies = new ArrayList<Combatant>();
      IntStream.range(0, count)
          .forEach(j -> enemies.add(new BasicEnemy(dungeonDetails, generators.nameGenerator())));
      encounters.add(new Encounter(null, enemies));
    }
    setLoaded(true);
  }

  private int calculateTargetLevel() {
    return coordinates.distanceTo(parent.getParent().getCoordinates().getGlobal());
  }

  public void execute(Player player) {
    execute(player, true);
  }

  public void execute(Player player, boolean hasTheInitiative) {
    state = Event.State.ACTIVE;
    encounters.get(currentEncounter).execute(player, hasTheInitiative);
    currentEncounter++;
    if (currentEncounter >= dungeonDetails.encounters()) {
      state = Event.State.COMPLETED;
    }
  }

  public boolean isInProgress() {
    return state == Event.State.ACTIVE;
  }

  public boolean isCompleted() {
    return state == Event.State.COMPLETED;
  }

  @Override
  public void logResult() {
    var action = isLoaded ? "Generated" : "Unloaded";
    log.info("{}: {}", action, this);
  }
}
