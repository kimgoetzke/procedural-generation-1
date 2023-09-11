package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.PlayerAction;
import com.hindsight.king_of_castrop_rauxel.characters.Inhabitant;
import com.hindsight.king_of_castrop_rauxel.location.AbstractAmenity.AmenityType;
import com.hindsight.king_of_castrop_rauxel.settings.LocationComponent;
import com.hindsight.king_of_castrop_rauxel.utils.StringGenerator;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.IntStream;

@Slf4j
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Settlement extends AbstractSettlement {

  public Settlement(StringGenerator stringGenerator) {
    super(stringGenerator);
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
    name = stringGenerator.locationNameFrom(this.getClass());
  }

  private void generateInhabitants() {
    var bounds = LocationComponent.getSettlementConfigs().get(size).getInhabitants();
    var inhabitantCount = random.nextInt(bounds.getMaxInclusive() - bounds.getMinInclusive()) + 1;
    IntStream.range(0, inhabitantCount).forEach(i -> inhabitants.add(new Inhabitant(stringGenerator)));
  }

  private void generateAmenities() {
    LocationComponent.getSettlementConfigs()
        .get(size)
        .getAmenities()
        .forEach(
            (k, v) ->
                IntStream.range(v.getMinInclusive(), v.getMaxInclusive() + 1)
                    .forEach(i -> addAmenity(k)));
  }

  private void addAmenity(AmenityType type) {
    var amenity = new Amenity(type, this);
    if (amenities.stream().noneMatch(a -> a.getName().equals(amenity.getName()))) {
      amenities.add(amenity);
    } else {
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

  /**
   * Modify once higher-level locations such as countries or lands are implemented. This method is
   * currently redundant but would allow referencing the parent name in its own name.
   */
  @Override
  public void generate(String parentName) {
    generate();
  }

  @Override
  public void logResult() {
    log.info("Generated: {}", this);
  }
}
