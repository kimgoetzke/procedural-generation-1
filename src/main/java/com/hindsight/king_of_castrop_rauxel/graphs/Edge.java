package com.hindsight.king_of_castrop_rauxel.graphs;

import com.hindsight.king_of_castrop_rauxel.location.Location;

public record Edge<T extends Location>(Vertex<T> start, Vertex<T> end, Integer weight) {}
