package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.PoiAction;
import com.hindsight.king_of_castrop_rauxel.characters.Inhabitant;
import com.hindsight.king_of_castrop_rauxel.location.AbstractAmenity.PoiType;
import com.hindsight.king_of_castrop_rauxel.utils.Generators;
import java.util.Random;
import java.util.stream.IntStream;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

@Slf4j
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Settlement extends AbstractSettlement {

  public Settlement(
      Pair<Integer, Integer> worldCoords,
      Pair<Integer, Integer> chunkCoords,
      Generators generators) {
    super(worldCoords, chunkCoords, generators);
    generateFoundation();
    logResult(true);
  }

  @Override
  public void load() {
    var startTime = System.currentTimeMillis();
    log.info("Generating full settlement '{}'...", id);
    LocationBuilder.throwIfRepeatedRequest(this, true);
    loadAmenities();
    loadEvents();
    loadInhabitants();
    loadPlayerActions();
    setLoaded(true);
    logResult();
    log.info("Generated '{}' in {} seconds", id, (System.currentTimeMillis() - startTime) / 1000.0);
  }

  private void generateFoundation() {
    size = LocationBuilder.randomSize(random);
    area = LocationBuilder.randomArea(random, size);
    name = nameGenerator.locationNameFrom(this.getClass());
  }

  private void loadAmenities() {
    var amenities = LocationBuilder.getSettlementConfig(size).getAmenities();
    for (var amenity : amenities.entrySet()) {
      var bounds = amenity.getValue();
      var count = random.nextInt(bounds.getUpper() - bounds.getLower() + 1) + bounds.getLower();
      IntStream.range(0, count).forEach(i -> addAmenity(amenity.getKey()));
    }
  }

  private void addAmenity(PoiType type) {
    var npc = new Inhabitant(nameGenerator, eventGenerator);
    var amenity = new Amenity(type, npc, this);
    if (pointsOfInterests.stream().noneMatch(a -> a.getName().equals(amenity.getName()))) {
      pointsOfInterests.add(amenity);
      inhabitants.add(npc);
    } else {
      amenity.getNpc().setHome(null);
      log.info("Skipping duplicate amenity '{}' and generating alternative", amenity.getName());
      addAmenity(type);
    }
  }

  /**
   * Generates events and available actions for each POI. This method must be called after the
   * settlement and POIs have been generated as the event can reference other POIs, etc.
   */
  private void loadEvents() {
    pointsOfInterests.forEach(
        poi -> {
          poi.getNpc().loadPrimaryEvent();
          var event = poi.getNpc().getPrimaryEvent();
          var participatingNpcs = event.getParticipantNpcs();
          for (var npc : participatingNpcs) {
            npc.addSecondaryEvent(event);
            npc.getHome().addAvailableAction(event);
          }
        });
  }

  private void loadInhabitants() {
    var bounds = LocationBuilder.getSettlementConfig(size).getInhabitants();
    inhabitantCount =
        Math.max(
            random.nextInt(bounds.getUpper() - bounds.getLower() + 1) + bounds.getLower(),
            inhabitants.size());
  }

  private void loadPlayerActions() {
    for (int i = 0; i < pointsOfInterests.size(); i++) {
      availableActions.add(
          PoiAction.builder()
              .index(i)
              .name("Go to %s".formatted(pointsOfInterests.get(i).getName()))
              .poi(pointsOfInterests.get(i))
              .build());
    }
  }

  @Override
  public void unload() {
    LocationBuilder.throwIfRepeatedRequest(this, false);
    random = new Random(seed);
    inhabitants.clear();
    pointsOfInterests.clear();
    availableActions.clear();
    setLoaded(false);
    logResult();
  }

  @Override
  public void logResult() {
    logResult(false);
  }

  public void logResult(boolean initial) {
    var action = initial || isLoaded() ? "Generated" : "Unloaded";
    log.info("{}: {}", action, this);
  }
}
