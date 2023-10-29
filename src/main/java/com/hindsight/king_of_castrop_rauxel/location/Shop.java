package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.utils.Generators;

import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, onlyExplicitlyIncluded = true, includeFieldNames = false)
public class Shop extends AbstractAmenity {

  private final Generators generators;
  private final Shop.Type shopType;

  public Shop(PointOfInterest.Type type, Npc npc, Location parent) {
    super(type, npc, parent);
    this.generators = parent.getGenerators();
    this.shopType = Shop.Type.from(random.nextInt(0, Shop.Type.values().length));
    load();
    logResult();
  }

  @Override
  public void load() {
    this.name =
        parent.getGenerators().nameGenerator().shopNameFrom(this, shopType, parent.getName(), npc);
    setLoaded(true);
  }

  @Override
  public List<Action> getAvailableActions() {
    var processedActions = new ArrayList<>(availableActions);
    // Add items to purchase as actions?
    return processedActions;
  }

  @Override
  public void logResult() {
    log.info("Generated: {}", this);
  }

  /**
   * The type of shop which determines products on offer. When adding new values here, make sure to
   * also add a corresponding entry in the TXT file containing names, otherwise fallback names will
   * be used.
   */
  @Getter
  public enum Type {
    GENERAL,
    ALCHEMY;

    public static Type from(int i) {
      return values()[i];
    }
  }
}
