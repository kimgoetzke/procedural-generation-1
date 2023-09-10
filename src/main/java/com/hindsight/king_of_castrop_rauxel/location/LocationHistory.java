package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.characters.Visitor;

import java.time.Instant;
import java.util.List;
import java.util.Set;


public class LocationHistory {
  private Location location;
  private List<Instant> visitedAt;
  private Set<Visitor> visitors;
}
