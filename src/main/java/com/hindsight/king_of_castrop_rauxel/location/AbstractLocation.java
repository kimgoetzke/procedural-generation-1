package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.PlayerAction;
import com.hindsight.king_of_castrop_rauxel.characters.Visitor;
import com.hindsight.king_of_castrop_rauxel.settings.SeedComponent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

import java.util.*;

@Slf4j
@ToString(exclude = {"id", "seed", "description", "availableActions", "visitors", "random"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class AbstractLocation implements Location {

  @EqualsAndHashCode.Include @Getter protected final String id;
  @EqualsAndHashCode.Include @Getter protected final long seed;
  @Getter @Setter protected String name;
  @Getter @Setter protected String description;
  protected int x;
  protected int y;
  @Getter protected List<PlayerAction> availableActions = new ArrayList<>();
  protected Set<Visitor> visitors = new HashSet<>();
  protected Random random;

  protected AbstractLocation(Pair<Integer, Integer> coordinates) {
    this.id = UUID.randomUUID().toString();
    this.seed = SeedComponent.seedFrom(coordinates);
    this.random = new Random(seed);
    setCoordinates(coordinates);
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
  public Pair<Integer, Integer> getCoordinates() {
    return Pair.of(x, y);
  }

  protected void setCoordinates(Pair<Integer, Integer> coordinates) {
    this.x = coordinates.getFirst();
    this.y = coordinates.getSecond();
  }

  public enum Size {
    XS,
    S,
    M,
    L,
    XL
  }
}
