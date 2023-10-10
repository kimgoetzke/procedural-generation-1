package com.hindsight.king_of_castrop_rauxel.event;

import java.util.List;

public record EventDetails(Event.Type eventType, String about, List<Reward> rewards) {}
