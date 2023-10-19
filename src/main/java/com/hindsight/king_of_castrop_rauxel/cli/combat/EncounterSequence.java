package com.hindsight.king_of_castrop_rauxel.cli.combat;

import com.hindsight.king_of_castrop_rauxel.location.LocationBuilder;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import com.hindsight.king_of_castrop_rauxel.world.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString(exclude = {"random", "isLoaded", "encounters"})
public class EncounterSequence implements Generatable {

  @Getter private final String id;
  @Getter @Setter private boolean isLoaded;
  private final long seed;
  private final Coordinates coordinates;
  private final Random random;
  private DungeonDetails dungeonDetails;
  List<Encounter> encounters = new ArrayList<>();

  public EncounterSequence(PointOfInterest parent) {
    var parentCoords = parent.getParent().getCoordinates();
    this.coordinates = parentCoords;
    this.seed = SeedBuilder.seedFrom(parentCoords.getGlobal());
    this.random = new Random(seed);
    this.id = IdBuilder.idFrom(this.getClass(), parent.getId());
    load();
  }

  @Override
  public void load() {
    LocationBuilder.throwIfRepeatedRequest(this, true);
    var targetLevel = calculateTargetLevel();
    this.dungeonDetails = DungeonDetails.load(random, targetLevel);
    for (int i = 0; i < dungeonDetails.encounters(); i++) {
      // Generate enemies
      // Make level mean something
      // Fix calculateTargetLevel as currently way to high
      encounters.add(new Encounter());
    }
    setLoaded(true);
  }

  private int calculateTargetLevel() {
    return coordinates.distanceTo(World.getCentreCoords());
  }

  @Override
  public void logResult() {
    var action = isLoaded ? "Generated" : "Unloaded";
    log.info("{}: {}", action, this);
  }
}
