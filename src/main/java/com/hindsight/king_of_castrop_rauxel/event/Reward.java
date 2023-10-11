package com.hindsight.king_of_castrop_rauxel.event;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import lombok.Builder;

import static com.hindsight.king_of_castrop_rauxel.cli.CliComponent.*;

@Builder
public class Reward {

  private Type type;
  private int minValue;
  private int maxValue;
  private int value;

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
