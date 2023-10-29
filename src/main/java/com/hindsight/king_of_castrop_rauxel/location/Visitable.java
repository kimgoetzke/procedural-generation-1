package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.characters.Visitor;

public interface Visitable {
  boolean hasBeenVisited();

  boolean hasVisited(Visitor visitor);

  void addVisitor(Visitor visitor);
}
