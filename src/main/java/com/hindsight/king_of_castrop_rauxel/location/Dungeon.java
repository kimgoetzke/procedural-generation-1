package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.action.CombatAction;
import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.cli.CliComponent;
import com.hindsight.king_of_castrop_rauxel.cli.combat.EncounterSequence;

import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true)
public class Dungeon extends AbstractAmenity {

  public static final String LABEL_FOR_USER = CliComponent.label("Combat", CliComponent.FMT.RED);
  private EncounterSequence sequence;

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
    sequence = new EncounterSequence(this);
    setLoaded(true);
  }

  @Override
  public List<Action> getAvailableActions() {
    var processedActions = new ArrayList<>(availableActions);
    processedActions.add(
        CombatAction.builder()
            .name(
                "Storm the %s %s%s"
                    .formatted(name, sequence.isInProgress() ? "again " : "", LABEL_FOR_USER))
            .index(availableActions.size() + 1)
            .sequence(sequence)
            .build());
    return processedActions;
  }

  @Override
  public void logResult() {
    log.info("Generated: {}", this);
  }
}
