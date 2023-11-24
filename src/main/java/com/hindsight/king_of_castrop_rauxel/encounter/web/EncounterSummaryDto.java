package com.hindsight.king_of_castrop_rauxel.encounter.web;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EncounterSummaryDto {

  private boolean playerHadInitiative;
  private boolean playerHasWon;
  private List<CombatantDto> enemiesDefeated = new ArrayList<>();
  private List<CombatantDto> attackers = new ArrayList<>();
  private List<CombatantDto> defenders = new ArrayList<>();
  private List<EncounterRecordDto> records = new ArrayList<>();

  public void addRecord(String attacker, int damage, String target, int health, boolean isAlive) {
    var recordDto = new EncounterRecordDto(attacker, damage, target, health, isAlive);
    records.add(recordDto);
  }
}
