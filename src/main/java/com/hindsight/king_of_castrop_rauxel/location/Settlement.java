package com.hindsight.king_of_castrop_rauxel.location;

import static com.hindsight.king_of_castrop_rauxel.settings.LocationComponent.*;

import com.hindsight.king_of_castrop_rauxel.action.PlayerAction;
import com.hindsight.king_of_castrop_rauxel.settings.LocationComponent;
import com.hindsight.king_of_castrop_rauxel.utils.BasicStringGenerator;
import java.util.stream.IntStream;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString(callSuper = true)
public class Settlement extends AbstractSettlement {

  public Settlement() {
    generate();
    logResult();
  }

  @Override
  public void generate() {
    log.info("Generating settlement...");
    generateFoundation();
    generateAmenities();
    generatePlayerActions();
  }

  private void generateFoundation() {
    size = LocationComponent.randomSize();
    name = BasicStringGenerator.generate(this.getClass());
    var bounds = getSettlementConfigurations().get(size).getInhabitants();
    inhabitants = random.nextInt(bounds.getMaxInclusive() - bounds.getMinInclusive() + 1);
  }

  private void generateAmenities() {
    getSettlementConfigurations()
        .get(size)
        .getAmenities()
        .forEach(
            (k, v) ->
                IntStream.range(v.getMinInclusive(), v.getMaxInclusive() + 1)
                    .forEach(i -> addAmenity(k)));
  }

  private void addAmenity(AmenityType type) {
    var amenity = new Amenity(type, size, name);
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
