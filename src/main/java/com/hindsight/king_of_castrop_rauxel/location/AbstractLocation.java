package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.characters.Visitor;
import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.world.*;

import java.util.*;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

@Slf4j
@ToString(
    of = {"name", "coordinates", "isLoaded"},
    includeFieldNames = false)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class AbstractLocation implements Location {

  @EqualsAndHashCode.Include @Getter protected final String id;
  @EqualsAndHashCode.Include @Getter protected final long seed;
  @Getter @Setter protected String name;
  @EqualsAndHashCode.Include @Getter protected final Coordinates coordinates;
  protected final AppProperties appProperties;
  protected List<Action> availableActions = new ArrayList<>();
  protected Set<Visitor> visitors = new HashSet<>();
  protected Random random;
  @Getter @Setter private boolean isLoaded;

  protected AbstractLocation(
      Pair<Integer, Integer> worldCoords,
      Pair<Integer, Integer> chunkCoords,
      AppProperties appProperties) {
    this.coordinates = new CoordinateFactory(appProperties).create(worldCoords, chunkCoords);
    this.seed = SeedBuilder.seedFrom(coordinates.getGlobal());
    this.random = new Random(seed);
    this.id = IdBuilder.idFrom(this.getClass(), coordinates);
    this.appProperties = appProperties;
  }

  @Override
  public boolean hasBeenVisited() {
    return !visitors.isEmpty();
  }

  @Override
  public boolean hasVisited(Visitor visitor) {
    return visitors.contains(visitor);
  }

  @Override
  public void addVisitor(Visitor visitor) {
    visitors.add(visitor);
  }

  @Override
  public CardinalDirection getCardinalDirection(Pair<Integer, Integer> other) {
    int dx = other.getFirst() - getCoordinates().cX();
    int dy = other.getSecond() - getCoordinates().cY();

    if (dx == 0) {
      if (dy < 0) {
        return CardinalDirection.NORTH;
      } else if (dy > 0) {
        return CardinalDirection.SOUTH;
      }
    } else if (dy == 0) {
      if (dx < 0) {
        return CardinalDirection.WEST;
      } else {
        return CardinalDirection.EAST;
      }
    } else {
      if (dx < 0 && dy < 0) {
        return CardinalDirection.NORTH_WEST;
      } else if (dx < 0) {
        return CardinalDirection.SOUTH_WEST;
      } else if (dy < 0) {
        return CardinalDirection.NORTH_EAST;
      } else {
        return CardinalDirection.SOUTH_EAST;
      }
    }
    return CardinalDirection.THIS;
  }

  /** Returns a random float that expresses the area of a settlement in square kilometers. */
  public int randomArea(Size size) {
    var bounds = appProperties.getSettlementProperties().get(size).getArea();
    return random.nextInt(bounds.getUpper() - bounds.getLower() + 1) + bounds.getLower();
  }

  /**
   * Returns a random Size enum. Must be provided with a Random in order to ensure reproducibility.
   */
  public Size randomSize() {
    var randomNumber = random.nextInt(0, 21);
    return switch (randomNumber) {
      case 0, 1, 2, 3, 4, 5 -> Size.XS;
      case 6, 7, 8, 9, 10, 11, 12, 13, 14 -> Size.S;
      case 15, 16, 17 -> Size.M;
      case 18, 19 -> Size.L;
      default -> Size.XL;
    };
  }
}
