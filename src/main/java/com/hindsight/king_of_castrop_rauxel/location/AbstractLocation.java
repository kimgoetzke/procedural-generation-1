package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.PlayerAction;
import com.hindsight.king_of_castrop_rauxel.characters.Visitor;
import com.hindsight.king_of_castrop_rauxel.settings.SeedComponent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@ToString(exclude = {"description", "visitors", "availableActions", "seed", "random"})
public abstract class AbstractLocation implements Location {

  @Getter protected final String id;
  @Getter protected final long seed;
  @Getter @Setter protected String name;
  @Getter @Setter protected String description;
  @Getter protected List<PlayerAction> availableActions = new ArrayList<>();
  protected Set<Visitor> visitors = new HashSet<>();
  protected Random random;

  protected AbstractLocation() {
    this.id = UUID.randomUUID().toString();
    this.seed = SeedComponent.getSeed();
    this.random = SeedComponent.getInstance();
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

  public enum Size {
    XS,
    S,
    M,
    L,
    XL
  }
}
