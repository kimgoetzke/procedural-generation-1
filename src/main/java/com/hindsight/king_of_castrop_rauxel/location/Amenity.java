package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.EventAction;
import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true)
public class Amenity extends AbstractAmenity {

  public Amenity(PoiType type, Npc npc, Location parent) {
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

  /**
   * Generates the available actions for this amenity based on its type. This method must be called
   * after the amenity has been generated as the event, to which actions can point, is generated
   * after all amenities have been generated. This is because events can, e.g., point to other POIs.
   */
  @Override
  public void loadAvailableActions() {
    switch (type) {
      case SHOP:
        speakWith("the owner of this establishment");
        break;
      case QUEST_LOCATION:
        speakWith("who appears to want something");
        break;
      default:
        break;
    }
  }

  private void speakWith(String who) {
    availableActions.add(
        EventAction.builder()
            .name("Speak with %s, %s".formatted(npc.getName(), who))
            .index(availableActions.size() + 1)
            .event(npc.getEvent())
            .build());
  }

  @Override
  public void unload() {
    LocationBuilder.throwIfRepeatedRequest(this, true);
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
