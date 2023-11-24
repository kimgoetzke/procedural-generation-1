package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.action.CombatAction;
import com.hindsight.king_of_castrop_rauxel.character.Npc;
import com.hindsight.king_of_castrop_rauxel.cli.CliComponent;
import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.encounter.DungeonDetails;
import com.hindsight.king_of_castrop_rauxel.encounter.DungeonHandler;
import com.hindsight.king_of_castrop_rauxel.encounter.EncounterSequence;
import com.hindsight.king_of_castrop_rauxel.utils.Generators;
import com.hindsight.king_of_castrop_rauxel.world.SeedBuilder;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
public class Dungeon extends AbstractAmenity {

  private final AppProperties appProperties;
  private final Generators generators;
  private final DungeonHandler dungeonHandler;
  private EncounterSequence sequence;
  private DungeonDetails dungeonDetails;

  public Dungeon(AppProperties appProperties, Type type, Npc npc, Location parent) {
    super(type, npc, parent);
    this.appProperties = appProperties;
    this.generators = parent.getGenerators();
    this.dungeonHandler = new DungeonHandler(appProperties);
    load();
    logResult();
  }

  @Override
  public void load() {
    this.dungeonDetails = createDungeonDetails();
    this.name = dungeonDetails.name();
    this.description = dungeonDetails.description();
    this.sequence = new EncounterSequence(appProperties, this, dungeonDetails);
    setLoaded(true);
  }

  private DungeonDetails createDungeonDetails() {
    var targetLevel = generators.terrainGenerator().getTargetLevel(parent.getCoordinates());
    var tier = dungeonHandler.getDungeonTier(targetLevel);
    var type = dungeonHandler.getDungeonType(random, tier);
    var encounterDetails = dungeonHandler.getEncounterDetails(random, targetLevel, type);
    var seed = SeedBuilder.seedFrom(parent.getCoordinates().getGlobal());
    var dungeonName = generators.nameGenerator().dungeonNameFrom(this.getClass(), type);
    var dungeonDescription =
        generators.nameGenerator().dungeonDescriptionFrom(parent.getClass(), type);
    return DungeonDetails.builder()
        .id(id)
        .name(dungeonName)
        .description(dungeonDescription)
        .tier(tier)
        .level(targetLevel)
        .encounterDetails(encounterDetails)
        .type(type)
        .seed(seed)
        .build();
  }

  @Override
  public List<Action> getAvailableActions() {
    var processedActions = super.getAvailableActions();
    addEnterDungeonAction(processedActions);
    return processedActions;
  }

  private void addEnterDungeonAction(List<Action> processedActions) {
    if (sequence.isCompleted()) {
      return;
    }
    var labelText = "Combat, level " + dungeonDetails.level();
    var label = CliComponent.label(labelText, CliComponent.FMT.RED);
    var actionName = "Storm the " + name + (sequence.isInProgress() ? " again" : "") + label;
    processedActions.add(
        CombatAction.builder()
            .name(actionName)
            .index(availableActions.size() + 1)
            .sequence(sequence)
            .build());
  }

  @Override
  public String getDescription() {
    var done = ", devoid of any life. You have slain all creatures here already.";
    var toDo = ", rumored to be filled with treasure.";
    return "%s%s".formatted(dungeonDetails.description(), sequence.isCompleted() ? done : toDo);
  }

  @Override
  public void logResult() {
    log.info("Generated: {}", this);
  }
}
