package com.hindsight.king_of_castrop_rauxel.event;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.cli.CliComponent;
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
  private boolean isLoaded = false;

  /** Used to create a reward with a specific value e.g. Player.getReward(). */
  public Reward(Type type, int actualValue) {
    this.type = type;
    this.minValue = actualValue;
    this.maxValue = actualValue;
    this.value = actualValue;
    this.isLoaded = true;
  }

  @Override
  public void load(Random random) {
    this.value = random.nextInt(maxValue - minValue + 1) + minValue;
  }

  public void give(Player player) {
    if (!isLoaded) {
      load(new Random());
    }
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
    var colour = CliComponent.toColour(type).toString();
    return colour + value + FMT.RESET + " " + type.toString().toLowerCase();
  }
}
