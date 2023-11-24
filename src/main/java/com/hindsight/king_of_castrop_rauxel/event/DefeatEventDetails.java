package com.hindsight.king_of_castrop_rauxel.event;

import com.hindsight.king_of_castrop_rauxel.character.Enemy;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public final class DefeatEventDetails {

  private DefeatEvent.TaskType taskType;
  private PointOfInterest poi;
  private Enemy.Type enemyType;
  private int toDefeat;

  public DefeatEventDetails() {
    this.taskType = null;
    this.enemyType = null;
    this.toDefeat = 0;
  }

  @Override
  public String toString() {
    return "DefeatEventDetails(defeatEventType="
        + taskType
        + ", enemyType="
        + enemyType
        + ", toDefeat="
        + toDefeat
        + ")";
  }
}
