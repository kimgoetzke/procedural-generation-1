package com.hindsight.king_of_castrop_rauxel.location;

import lombok.Builder;

@Builder
public record DungeonDetails(String id, int level, int encounters, DungeonHandler.Type type) {}
