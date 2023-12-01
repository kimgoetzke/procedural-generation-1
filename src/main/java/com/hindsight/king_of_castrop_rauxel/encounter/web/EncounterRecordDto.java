package com.hindsight.king_of_castrop_rauxel.encounter.web;

public record EncounterRecordDto(
    String attackerName, int damage, String targetName, int health, boolean isAlive) {}
