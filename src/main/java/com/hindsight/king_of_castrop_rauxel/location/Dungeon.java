package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.action.CombatAction;
import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.cli.CliComponent;
import com.hindsight.king_of_castrop_rauxel.cli.combat.EncounterSequence;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(
    callSuper = true,
    exclude = {"dungeonHandler", "sequence"})
public class Dungeon extends AbstractAmenity {

  private final DungeonHandler dungeonHandler = new DungeonHandler(this);
  private EncounterSequence sequence;
  private DungeonDetails dungeonDetails;

  public Dungeon(Type type, Npc npc, Location parent) {
    super(type, npc, parent);
    load();
    logResult();
  }

  // TODO: Generate dungeon details and inject into sequence
  @Override
  public void load() {
    this.dungeonDetails = dungeonHandler.build();
    this.name = parent.getGenerators().nameGenerator().dungeonNameFrom(this.getClass());
    sequence = new EncounterSequence(dungeonDetails);
    setLoaded(true);
  }

  @Override
  public List<Action> getAvailableActions() {
    var processedActions = new ArrayList<>(availableActions);
    addEnterDungeonAction(processedActions);
    return processedActions;
  }

  private void addEnterDungeonAction(ArrayList<Action> processedActions) {
    if (sequence.isCompleted()) {
      return;
    }
    var labelText = "Combat, level " + sequence.getDungeonDetails().level() + "+";
    var label = CliComponent.label(labelText, CliComponent.FMT.RED);
    var actionName = "Storm the " + name + (sequence.isInProgress() ? " again " : " ") + label;
    processedActions.add(
        CombatAction.builder()
            .name(actionName)
            .index(availableActions.size() + 1)
            .sequence(sequence)
            .build());
  }

  // TODO: Adapt description to dungeon style/type
  @Override
  public String getDescription() {
    return "A dark and foreboding place%s"
        .formatted(
            sequence.isCompleted()
                ? ", devoid of any life. You have slain all creatures here already."
                : ", rumored to be filled with treasure.");
  }

  @Override
  public void logResult() {
    log.info("Generated: {}", this);
  }
}
