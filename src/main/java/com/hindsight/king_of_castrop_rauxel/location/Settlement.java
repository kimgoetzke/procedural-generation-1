package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.PlayerAction;
import com.hindsight.king_of_castrop_rauxel.characters.Inhabitant;
import com.hindsight.king_of_castrop_rauxel.location.AbstractAmenity.AmenityType;
import com.hindsight.king_of_castrop_rauxel.settings.LocationComponent;
import com.hindsight.king_of_castrop_rauxel.utils.StringGenerator;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

import java.util.stream.IntStream;

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

  private void addAmenity(AmenityType type) {
    var npc = inhabitants.stream().filter(i -> i.getHome() == null).findFirst().orElse(null);
    var amenity = new Amenity(type, npc, this);
    if (amenities.stream().noneMatch(a -> a.getName().equals(amenity.getName()))) {
      amenities.add(amenity);
    } else {
      amenity.getNpc().setHome(null);
      log.info("Skipping duplicate amenity '{}' and generating alternative", amenity.getName());
      addAmenity(type);
    }
  }

  private void generatePlayerActions() {
    for (int i = 1; i <= amenities.size(); i++) {
      availableActions.add(
          PlayerAction.builder()
              .number(i)
              .name("[%s] Go to %s".formatted(i, amenities.get(i - 1).getName()))
              .location(amenities.get(i - 1))
              .build());
    }
  }

  @Override
  public void logResult() {
    log.info("Generated: {}", this);
  }
}
