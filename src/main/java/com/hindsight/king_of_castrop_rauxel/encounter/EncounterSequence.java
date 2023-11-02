package com.hindsight.king_of_castrop_rauxel.encounter;

import com.hindsight.king_of_castrop_rauxel.characters.BasicEnemy;
import com.hindsight.king_of_castrop_rauxel.characters.Combatant;
import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.cli.combat.Encounter;
import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.event.Event;
import com.hindsight.king_of_castrop_rauxel.utils.Generators;
import java.util.ArrayList;
import java.util.List;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString(onlyExplicitlyIncluded = true)
public class EncounterSequence {

  @ToString.Include private final List<Encounter> encounters = new ArrayList<>();
  private int currentEncounter = 0;
  private Event.State state = Event.State.AVAILABLE;

  public EncounterSequence(
    AppProperties appProperties, Generators generators, DungeonDetails dungeonDetails) {
    var seed = dungeonDetails.seed();
    for (int i = 0; i < dungeonDetails.encounterDetails().size(); i++) {
      var enemies = new ArrayList<Combatant>();
      var encounter = dungeonDetails.encounterDetails().get(i);
      for (var enemyDetails : encounter) {
        enemies.add(new BasicEnemy(enemyDetails, seed, generators.nameGenerator()));
      }
      encounters.add(new Encounter(null, enemies, appProperties));
    }
  }

  public void execute(Player player) {
    execute(player, true);
  }

  public void execute(Player player, boolean hasTheInitiative) {
    state = Event.State.ACTIVE;
    encounters.get(currentEncounter).execute(player, hasTheInitiative);
    currentEncounter++;
    if (currentEncounter >= encounters.size()) {
      state = Event.State.COMPLETED;
    }
  }

  public boolean isInProgress() {
    return state == Event.State.ACTIVE;
  }

  public boolean isCompleted() {
    return state == Event.State.COMPLETED;
  }
}
