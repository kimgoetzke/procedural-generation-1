package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.characters.Visitor;
import com.hindsight.king_of_castrop_rauxel.configuration.AppProperties;
import com.hindsight.king_of_castrop_rauxel.world.Coordinates;
import com.hindsight.king_of_castrop_rauxel.world.IdBuilder;
import com.hindsight.king_of_castrop_rauxel.world.SeedBuilder;
import java.util.*;

import com.hindsight.king_of_castrop_rauxel.world.WorldHandler;
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
  @Getter @Setter protected String description;
  protected List<Action> availableActions = new ArrayList<>();
  protected Set<Visitor> visitors = new HashSet<>();
  protected Random random;
  @EqualsAndHashCode.Include @Getter protected final Coordinates coordinates;
  protected final AppProperties appProperties;
  protected final PoiFactory poiFactory;
  @Getter @Setter private boolean isLoaded;

  protected AbstractLocation(
      Pair<Integer, Integer> worldCoords,
      Pair<Integer, Integer> chunkCoords,
      AppProperties appProperties,
      PoiFactory poiFactory) {
    this.coordinates = new Coordinates(worldCoords, chunkCoords);
    this.seed = SeedBuilder.seedFrom(coordinates.getGlobal());
    this.random = new Random(seed);
    this.id = IdBuilder.idFrom(this.getClass(), coordinates);
    this.appProperties = appProperties;
    this.poiFactory = poiFactory;
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
  public WorldHandler.CardinalDirection getCardinalDirection(Pair<Integer, Integer> other) {
    int dx = other.getFirst() - getCoordinates().cX();
    int dy = other.getSecond() - getCoordinates().cY();

    if (dx == 0) {
      if (dy < 0) {
        return WorldHandler.CardinalDirection.NORTH;
      } else if (dy > 0) {
        return WorldHandler.CardinalDirection.SOUTH;
      }
    } else if (dy == 0) {
      if (dx < 0) {
        return WorldHandler.CardinalDirection.WEST;
      } else {
        return WorldHandler.CardinalDirection.EAST;
      }
    } else {
      if (dx < 0 && dy < 0) {
        return WorldHandler.CardinalDirection.NORTH_WEST;
      } else if (dx < 0) {
        return WorldHandler.CardinalDirection.SOUTH_WEST;
      } else if (dy < 0) {
        return WorldHandler.CardinalDirection.NORTH_EAST;
      } else {
        return WorldHandler.CardinalDirection.SOUTH_EAST;
      }
    }
    return WorldHandler.CardinalDirection.THIS;
  }
}
