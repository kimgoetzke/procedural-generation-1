package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.action.BuyAction;
import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.items.Buyable;
import com.hindsight.king_of_castrop_rauxel.utils.DataServices;
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
  private final DataServices dataServices;
  private final Shop.Type shopType;
  private final int tier;
  private final List<Buyable> items = new ArrayList<>();

  public Shop(PointOfInterest.Type type, Npc npc, Location parent, int tier) {
    super(type, npc, parent);
    this.generators = parent.getGenerators();
    this.dataServices = parent.getDataServices();
    this.shopType = Shop.Type.from(random.nextInt(0, Shop.Type.values().length));
    this.tier = tier;
    load();
    logResult();
  }

  @Override
  public void load() {
    this.name = generators.nameGenerator().shopNameFrom(this, shopType, parent.getName(), npc);
    items.addAll(dataServices.consumableService().getConsumablesByTypeAndTier(shopType, tier));
    loadPlayerActions();
    setLoaded(true);
  }

  private void loadPlayerActions() {
    items.forEach(item -> availableActions.add(new BuyAction(availableActions.size() + 1, item)));
  }

  @Override
  public List<Action> getAvailableActions() {
    return availableActions;
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
