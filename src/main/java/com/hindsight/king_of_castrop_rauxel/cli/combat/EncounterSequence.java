package com.hindsight.king_of_castrop_rauxel.cli.combat;

import com.hindsight.king_of_castrop_rauxel.characters.BasicEnemy;
import com.hindsight.king_of_castrop_rauxel.characters.Combatant;
import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.location.LocationBuilder;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
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
@ToString(
    exclude = {"random", "isLoaded", "encounters", "currentEncounter", "parent", "inProgress"})
public class EncounterSequence implements Generatable {

  @Getter private final String id;
  private final Random random;
  private final PointOfInterest parent;
  private final List<Encounter> encounters = new ArrayList<>();
  private final Coordinates coordinates;
  @Getter @Setter private boolean isLoaded;
  @Getter private DungeonDetails dungeonDetails;
  private int currentEncounter = 0;
  @Getter private boolean inProgress = false;

  public EncounterSequence(PointOfInterest parent) {
    var parentCoords = parent.getParent().getCoordinates();
    var seed = SeedBuilder.seedFrom(parentCoords.getGlobal());
    this.coordinates = parentCoords;
    this.random = new Random(seed);
    this.id = IdBuilder.idFrom(this.getClass(), parent.getId());
    this.parent = parent;
    load();
  }

  public void execute(Player player) {
    execute(player, true);
  }

  public void execute(Player player, boolean hasTheInitiative) {
    inProgress = true;
    encounters.get(currentEncounter).execute(player, hasTheInitiative);
    currentEncounter++;
    if (currentEncounter >= dungeonDetails.encounters()) {
      inProgress = false;
    }
  }

  // TODO: Procedurally generate and abstract away everything necessary
  @Override
  public void load() {
    LocationBuilder.throwIfRepeatedRequest(this, true);
    var targetLevel = calculateTargetLevel();
    this.dungeonDetails = DungeonDetails.load(random, targetLevel);
    for (int i = 0; i < dungeonDetails.encounters(); i++) {
      var count = random.nextInt(3) + 1;
      var enemies = new ArrayList<Combatant>();
      IntStream.range(0, count).forEach(j -> enemies.add(new BasicEnemy(dungeonDetails)));
      encounters.add(new Encounter(null, enemies));
    }
    setLoaded(true);
  }

  private int calculateTargetLevel() {
    return coordinates.distanceTo(parent.getParent().getCoordinates().getGlobal());
  }

  @Override
  public void logResult() {
    var action = isLoaded ? "Generated" : "Unloaded";
    log.info("{}: {}", action, this);
  }
}
