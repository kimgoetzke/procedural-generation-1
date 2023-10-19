package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.cli.combat.EncounterSequence;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true)
public class Dungeon extends AbstractAmenity {

  private EncounterSequence encounterSequence;

  public Dungeon(Type type, Npc npc, Location parent) {
    super(type, npc, parent);
    load();
    logResult();
  }

  @Override
  public void load() {
    this.name =
        parent
            .getNameGenerator()
            .locationNameFrom(this, parent.getSize(), parent.getName(), npc, this.getClass());
    encounterSequence = new EncounterSequence(this);
    setLoaded(true);
  }

  @Override
  public List<Action> getAvailableActions() {
    // Add dungeon-specific actions
    return availableActions;
  }

  @Override
  public void logResult() {
    log.info("Generated: {}", this);
  }
}
