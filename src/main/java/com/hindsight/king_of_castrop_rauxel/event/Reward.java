package com.hindsight.king_of_castrop_rauxel.event;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.world.Randomisable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Random;

import static com.hindsight.king_of_castrop_rauxel.cli.CliComponent.*;

@Getter
@Setter
@NoArgsConstructor
public class Reward implements Randomisable {

  private Type type;
  private int value;
  private int minValue;
  private int maxValue;

  /** Used to create a reward with a specific value e.g. Player.getReward(). */
  public Reward(Type type, int actualValue) {
    this.type = type;
    this.minValue = actualValue;
    this.maxValue = actualValue;
    this.value = actualValue;
  }

  @Override
  public void load(Random random) {
    this.value = random.nextInt(maxValue - minValue + 1) + minValue;
  }

  public void give(Player player) {
    switch (type) {
      case GOLD -> player.addGold(value);
      case EXPERIENCE -> player.addExperience(value);
    }
  }

  public enum Type {
    GOLD,
    EXPERIENCE
  }

  @Override
  public String toString() {
    return switch (type) {
      case GOLD, EXPERIENCE -> FMT.YELLOW_BOLD
          + String.valueOf(value)
          + FMT.RESET
          + " "
          + type.toString().toLowerCase();
    };
  }
}
