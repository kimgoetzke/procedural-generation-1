package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.characters.Npc;

import java.util.List;
import java.util.Random;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true)
public class Dungeon extends AbstractAmenity {

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
    setLoaded(true);
  }

  @Override
  public List<Action> getAvailableActions() {
    // Add dungeon-specific actions
    return availableActions;
  }

  @Override
  public void unload() {
    LocationBuilder.throwIfRepeatedRequest(this, false);
    random = new Random(seed);
    availableActions.clear();
    setLoaded(false);
    log.info("Unloaded: {}", this);
  }

  @Override
  public void logResult() {
    log.info("Generated: {}", this);
  }
}
