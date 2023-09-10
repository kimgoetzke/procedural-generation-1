package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.PlayerAction;
import com.hindsight.king_of_castrop_rauxel.characters.Visitor;
import java.util.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString(exclude = {"description", "visitors", "availableActions"})
public abstract class AbstractLocation implements Location {

  @Getter protected final String id;
  @Getter @Setter protected String name;
  @Getter @Setter protected String description;
  @Getter protected List<PlayerAction> availableActions = new ArrayList<>();
  protected Set<Visitor> visitors = new HashSet<>();

  protected AbstractLocation() {
    this.id = UUID.randomUUID().toString();
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
}
