package com.hindsight.king_of_castrop_rauxel.graphs;

import com.hindsight.king_of_castrop_rauxel.location.Location;

public record Edge(
    Vertex<? extends Location> start, Vertex<? extends Location> end, Integer weight) {}
