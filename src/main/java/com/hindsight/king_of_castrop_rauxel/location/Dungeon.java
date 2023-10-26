package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.action.CombatAction;
import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.cli.CliComponent;
import com.hindsight.king_of_castrop_rauxel.cli.combat.EncounterSequence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hindsight.king_of_castrop_rauxel.utils.Generators;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import static com.hindsight.king_of_castrop_rauxel.configuration.AppConstants.*;

@Getter
@Slf4j
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true, includeFieldNames = false)
public class Dungeon extends AbstractAmenity {

  private final Generators generators;
  private EncounterSequence sequence;
  @ToString.Include private DungeonDetails dungeonDetails;

  public Dungeon(PointOfInterest.Type type, Npc npc, Location parent) {
    super(type, npc, parent);
    this.generators = parent.getGenerators();
    load();
    logResult();
  }

  @Override
  public void load() {
    this.dungeonDetails = createDungeonDetails();
    this.name = dungeonDetails.name();
    this.description = dungeonDetails.description();
    this.sequence = new EncounterSequence(dungeonDetails, parent.getGenerators());
    setLoaded(true);
  }

  private DungeonDetails createDungeonDetails() {
    var encounters = loadEncounters();
    var targetLevel = generators.terrainGenerator().getTargetLevel(parent.getCoordinates());
    var tier = LocationBuilder.getDungeonTier(targetLevel);
    var type = LocationBuilder.getDungeonType(tier, random);
    var dungeonName = generators.nameGenerator().dungeonNameFrom(this.getClass(), type);
    var dungeonDescription =
        generators.nameGenerator().dungeonDescriptionFrom(parent.getClass(), type);
    return DungeonDetails.builder()
        .id(id)
        .name(dungeonName)
        .description(dungeonDescription)
        .tier(tier)
        .level(targetLevel)
        .encounters(encounters)
        .type(type)
        .build();
  }

  private int[] loadEncounters() {
    var dLower = ENCOUNTERS_PER_DUNGEON.getLower();
    var dUpper = ENCOUNTERS_PER_DUNGEON.getUpper();
    var encounters = new int[random.nextInt(dUpper - dLower + 1) + dLower];
    var eLower = ENEMIES_PER_ENCOUNTER.getLower();
    var eUpper = ENEMIES_PER_ENCOUNTER.getUpper();
    Arrays.setAll(encounters, i -> random.nextInt(eUpper - eLower + 1) + eLower);
    return encounters;
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
    var labelText = "Combat, level " + dungeonDetails.level() + "+";
    var label = CliComponent.label(labelText, CliComponent.FMT.RED);
    var actionName = "Storm the " + name + (sequence.isInProgress() ? " again " : " ") + label;
    processedActions.add(
        CombatAction.builder()
            .name(actionName)
            .index(availableActions.size() + 1)
            .sequence(sequence)
            .build());
  }

  @Override
  public String getDescription() {
    return "%s%s"
        .formatted(
            dungeonDetails.description(),
            sequence.isCompleted()
                ? ", devoid of any life. You have slain all creatures here already."
                : ", rumored to be filled with treasure.");
  }

  @Override
  public void logResult() {
    log.info("Generated: {}", this);
  }
}
