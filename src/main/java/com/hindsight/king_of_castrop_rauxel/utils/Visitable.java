package com.hindsight.king_of_castrop_rauxel.utils;

public interface Visitable {
  boolean hasBeenVisited();

  boolean hasVisited(Visitor visitor);

  void addVisitor(Visitor visitor);
}
