package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.PoiAction;
import com.hindsight.king_of_castrop_rauxel.characters.Inhabitant;
import com.hindsight.king_of_castrop_rauxel.location.AbstractAmenity.PoiType;
import com.hindsight.king_of_castrop_rauxel.utils.StringGenerator;

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
      StringGenerator stringGenerator) {
    super(worldCoords, chunkCoords, stringGenerator);
    generateFoundation();
    logResult(true);
  }

  @Override
  public void load() {
    log.info("Generating full settlement '{}'...", id);
    generateInhabitants();
    generateAmenities();
    generatePlayerActions();
    setLoaded(true);
    logResult();
  }

  private void generateFoundation() {
    size = LocationComponent.randomSize(random);
    area = LocationComponent.randomArea(random, size);
    name = stringGenerator.locationNameFrom(this.getClass());
  }

  private void generateInhabitants() {
    var bounds = LocationComponent.getSettlementConfigs().get(size).getInhabitants();
    var inhabitantCount =
        random.nextInt(bounds.getUpper() - bounds.getLower() + 1) + bounds.getLower();
    IntStream.range(0, inhabitantCount)
        .forEach(i -> inhabitants.add(new Inhabitant(stringGenerator)));
  }

  private void generateAmenities() {
    var amenities = LocationComponent.getSettlementConfigs().get(size).getAmenities();
    for (var amenity : amenities.entrySet()) {
      var bounds = amenity.getValue();
      var count = random.nextInt(bounds.getUpper() - bounds.getLower() + 1) + bounds.getLower();
      IntStream.range(0, count).forEach(i -> addAmenity(amenity.getKey()));
    }
  }

  private void addAmenity(PoiType type) {
    var npc = inhabitants.stream().filter(i -> i.getHome() == null).findFirst().orElse(null);
    var amenity = new Amenity(type, npc, this);
    if (pointsOfInterests.stream().noneMatch(a -> a.getName().equals(amenity.getName()))) {
      pointsOfInterests.add(amenity);
    } else {
      amenity.getNpc().setHome(null);
      log.info("Skipping duplicate amenity '{}' and generating alternative", amenity.getName());
      addAmenity(type);
    }
  }

  private void generatePlayerActions() {
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
    random = new Random(seed);
    inhabitants.clear();
    pointsOfInterests.clear();
    neighbours.clear();
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
