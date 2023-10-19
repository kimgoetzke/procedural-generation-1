package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.characters.Visitor;
import com.hindsight.king_of_castrop_rauxel.world.Coordinates;
import com.hindsight.king_of_castrop_rauxel.world.IdBuilder;
import com.hindsight.king_of_castrop_rauxel.world.SeedBuilder;
import java.util.*;

import com.hindsight.king_of_castrop_rauxel.world.WorldHandler;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

@Slf4j
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class AbstractLocation implements Location {

  @EqualsAndHashCode.Include @Getter protected final String id;
  @EqualsAndHashCode.Include @Getter protected final long seed;
  @Getter @Setter protected String name;
  @Getter @Setter protected String description;
  @Getter protected List<Action> availableActions = new ArrayList<>();
  protected Set<Visitor> visitors = new HashSet<>();
  protected Random random;
  @EqualsAndHashCode.Include @Getter protected final Coordinates coordinates;
  @Getter @Setter private boolean isLoaded;

  protected AbstractLocation(
      Pair<Integer, Integer> worldCoords, Pair<Integer, Integer> chunkCoords) {
    this.coordinates = new Coordinates(worldCoords, chunkCoords);
    this.seed = SeedBuilder.seedFrom(coordinates.getGlobal());
    this.random = new Random(seed);
    this.id = IdBuilder.idFrom(this.getClass(), coordinates);
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

  @Getter
  public enum Size {
    XS("Very small", 0),
    S("Small", 1),
    M("Medium", 2),
    L("Large", 3),
    XL("Very large", 4);

    private final String name;
    private final int ordinal;

    Size(String s, int i) {
      this.name = s;
      this.ordinal = i;
    }
  }

  @Override
  public String toString() {
    return "Location(name="
        + name
        + ", coordinates="
        + coordinates
        + ", isLoaded="
        + isLoaded
        + ")";
  }
}
