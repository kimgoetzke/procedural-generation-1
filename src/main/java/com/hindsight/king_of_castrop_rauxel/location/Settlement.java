package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.PoiAction;
import com.hindsight.king_of_castrop_rauxel.characters.Inhabitant;
import com.hindsight.king_of_castrop_rauxel.components.LocationComponent;
import com.hindsight.king_of_castrop_rauxel.location.AbstractAmenity.PoiType;
import com.hindsight.king_of_castrop_rauxel.utils.StringGenerator;
import java.util.stream.IntStream;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

@Slf4j
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Settlement extends AbstractSettlement {

  public Settlement(StringGenerator stringGenerator, Pair<Integer, Integer> coordinates) {
    super(stringGenerator, coordinates);
    generate();
    logResult();
  }

  @Override
  public void generate() {
    log.info("Generating settlement...");
    generateFoundation();
    generateInhabitants();
    generateAmenities();
    generatePlayerActions();
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
    for (int i = 1; i <= pointsOfInterests.size(); i++) {
      availableActions.add(
          PoiAction.builder()
              .number(i)
              .name("[%s] Go to %s".formatted(i, pointsOfInterests.get(i - 1).getName()))
              .location(pointsOfInterests.get(i - 1))
              .build());
    }
  }

  @Override
  public void logResult() {
    log.info("Generated: {}", this);
  }
}
