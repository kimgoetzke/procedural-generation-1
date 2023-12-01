package com.hindsight.king_of_castrop_rauxel.encounter;

import com.hindsight.king_of_castrop_rauxel.character.BasicEnemy;
import com.hindsight.king_of_castrop_rauxel.character.Combatant;
import com.hindsight.king_of_castrop_rauxel.character.Player;
import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.encounter.web.EncounterSummaryDto;
import com.hindsight.king_of_castrop_rauxel.event.DefeatEvent;
import com.hindsight.king_of_castrop_rauxel.event.Event;
import com.hindsight.king_of_castrop_rauxel.location.Dungeon;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import com.hindsight.king_of_castrop_rauxel.utils.NameGenerator;
import com.hindsight.king_of_castrop_rauxel.web.exception.GenericWebException;
import java.util.ArrayList;
import java.util.List;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString(onlyExplicitlyIncluded = true)
public class EncounterSequence {

  @ToString.Include private final List<Encounter> encounters = new ArrayList<>();
  private final PointOfInterest parent;
  private int currentEncounter = 0;
  private Event.State state = Event.State.AVAILABLE;

  public EncounterSequence(
      AppProperties appProperties, Dungeon parent, DungeonDetails dungeonDetails) {
    this.parent = parent;
    var seed = dungeonDetails.seed();
    var nameGenerator = parent.getGenerators().nameGenerator();
    generateSequence(appProperties, dungeonDetails, seed, nameGenerator);
  }

  private void generateSequence(
      AppProperties properties, DungeonDetails details, long seed, NameGenerator nameGenerator) {
    for (int i = 0; i < details.encounterDetails().size(); i++) {
      var enemies = new ArrayList<Combatant>();
      var encounter = details.encounterDetails().get(i);
      for (var enemyDetails : encounter) {
        enemies.add(new BasicEnemy(enemyDetails, seed, nameGenerator));
      }
      encounters.add(new Encounter(null, enemies, properties));
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
      setToCompleted(player);
    }
  }

  public EncounterSummaryDto getPreviousEncounterSummary() {
    var i = currentEncounter - 1;
    if (i < 0) {
      throw new GenericWebException("No encounters have been executed yet");
    }
    return encounters.get(i).getSummaryData();
  }

  private void setToCompleted(Player player) {
    state = Event.State.COMPLETED;
    player.getActiveEvents().stream()
        .filter(DefeatEvent.class::isInstance)
        .map(e -> (DefeatEvent) e)
        .forEach(e -> e.setEventStateToReady(parent));
  }

  public boolean isInProgress() {
    return state == Event.State.ACTIVE;
  }

  public boolean isCompleted() {
    return state == Event.State.COMPLETED;
  }
}
