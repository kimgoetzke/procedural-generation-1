package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.PoiAction;
import com.hindsight.king_of_castrop_rauxel.characters.Inhabitant;
import com.hindsight.king_of_castrop_rauxel.characters.Npc;
import com.hindsight.king_of_castrop_rauxel.cli.CliComponent;
import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest.Type;
import com.hindsight.king_of_castrop_rauxel.utils.DataServices;
import com.hindsight.king_of_castrop_rauxel.utils.Generators;
import java.util.stream.IntStream;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

@Slf4j
@ToString(callSuper = true, includeFieldNames = false)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Settlement extends AbstractSettlement {

  public Settlement(
      Pair<Integer, Integer> worldCoords,
      Pair<Integer, Integer> chunkCoords,
      Generators generators,
      DataServices dataServices,
      AppProperties appProperties) {
    super(worldCoords, chunkCoords, generators, dataServices, appProperties);
    generateFoundation();
    logResult(true);
  }

  @Override
  public void load() {
    var startTime = System.currentTimeMillis();
    log.info("Generating full settlement '{}'...", id);
    throwIfRepeatedRequest(true);
    loadPois();
    loadEvents();
    loadInhabitants();
    loadPlayerActions();
    setLoaded(true);
    logResult();
    log.info("Generated '{}' in {} seconds", id, (System.currentTimeMillis() - startTime) / 1000.0);
  }

  private void generateFoundation() {
    size = randomSize();
    area = randomArea(size);
    name = generators.nameGenerator().locationNameFrom(this.getClass());
  }

  private void loadPois() {
    var amenities = appProperties.getSettlementProperties().get(size).getAmenities();
    for (var amenity : amenities.entrySet()) {
      var bounds = amenity.getValue();
      var count = random.nextInt(bounds.getUpper() - bounds.getLower() + 1) + bounds.getLower();
      var type = amenity.getKey();
      IntStream.range(0, count).forEach(i -> addPoi(type));
    }
  }

  private void addPoi(Type type) {
    var npc = new Inhabitant(random, generators);
    var poi = createInstance(this, npc, type);
    if (pointsOfInterests.stream().noneMatch(a -> a.getName().equals(poi.getName()))) {
      pointsOfInterests.add(poi);
      inhabitants.add(npc);
    } else {
      revert(poi);
      addPoi(type);
    }
  }

  public PointOfInterest createInstance(Location parent, Npc npc, PointOfInterest.Type type) {
    return switch (type) {
      case DUNGEON -> new Dungeon(appProperties, type, npc, parent);
      case SHOP -> new Shop(type, npc, parent);
      default -> new Amenity(type, npc, parent);
    };
  }

  private void revert(PointOfInterest poi) {
    poi.getNpc().setHome(null);
    log.info(
        "Skipping duplicate {} POI '{}' and generating alternative", poi.getType(), poi.getName());
  }

  /**
   * Generates events and available actions for each POI. This method must be called after the
   * settlement and POIs have been generated as the event can reference other POIs, etc.
   */
  private void loadEvents() {
    pointsOfInterests.stream()
        .filter(poi -> poi.getType() != Type.DUNGEON && poi.getType() != Type.ENTRANCE)
        .forEach(this::loadPrimaryEvent);
  }

  private void loadPrimaryEvent(PointOfInterest poi) {
    poi.getNpc().loadPrimaryEvent();
    var event = poi.getNpc().getPrimaryEvent();
    var participatingNpcs = event.getParticipantNpcs();
    for (var npc : participatingNpcs) {
      npc.addSecondaryEvent(event);
      npc.getHome().addAvailableAction(event);
    }
  }

  private void loadInhabitants() {
    var bounds = appProperties.getSettlementProperties().get(size).getInhabitants();
    var i = random.nextInt(bounds.getUpper() - bounds.getLower() + 1) + bounds.getLower();
    inhabitantCount = Math.max(i, inhabitants.size());
  }

  private void loadPlayerActions() {
    for (int i = 0; i < pointsOfInterests.size(); i++) {
      availableActions.add(
          PoiAction.builder()
              .index(i)
              .name(getActionName(i))
              .poi(pointsOfInterests.get(i))
              .build());
    }
  }

  private String getActionName(int i) {
    return "Go to %s%s"
        .formatted(
            pointsOfInterests.get(i).getName(),
            CliComponent.label(pointsOfInterests.get(i).getType()));
  }

  @Override
  public void logResult() {
    logResult(false);
  }

  public void logResult(boolean initial) {
    var action = initial || isLoaded() ? "Generated" : "Unloaded";
    var summary = initial ? this.getBriefSummary() : this.toString();
    log.info("{}: {}", action, summary);
  }
}
