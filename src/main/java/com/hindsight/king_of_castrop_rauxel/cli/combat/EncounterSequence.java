package com.hindsight.king_of_castrop_rauxel.cli.combat;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.location.Location;
import com.hindsight.king_of_castrop_rauxel.world.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EncounterSequence {

  private final Random random;
  List<Encounter> encounters = new ArrayList<>();
  DungeonDetails.DungeonType type;

  public EncounterSequence(Location parent, Player player, DungeonDetails.DungeonType type) {
    var coordinates = parent.getCoordinates();
    var seed = SeedBuilder.seedFrom(coordinates.getGlobal());
    this.random = new Random(seed);
    this.type = type;
    var targetLevel = calculateTargetLevel(coordinates, player);
    var details = DungeonDetails.random(random, type, targetLevel, 2);
    for (int i = 0; i < details.encounters(); i++) {
      // Generate enemies
      encounters.add(new Encounter());
    }
  }

  private int calculateTargetLevel(Coordinates coordinates, Player player) {
    var distance = coordinates.distanceTo(player.getStartCoordinates());
    if (distance < 275) {
      return 1;
    } else if (distance < 550) {
      return 2;
    } else if (distance < 800) {
      return 3;
    } else if (distance < 1000) {
      return 4;
    } else {
      return 5;
    }
  }

  public void execute() {
    // To implement...
  }
}
