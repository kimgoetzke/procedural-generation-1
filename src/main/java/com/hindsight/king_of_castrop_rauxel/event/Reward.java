package com.hindsight.king_of_castrop_rauxel.event;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.cli.CliComponent;
import com.hindsight.king_of_castrop_rauxel.world.Randomisable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

import static com.hindsight.king_of_castrop_rauxel.cli.CliComponent.*;

@Slf4j
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
    isLoaded = true;
  }

  public void give(Player player) {
    if (!isLoaded) {
      log.warn("Reward has not been loaded. Loading now using unseeded Random()...");
      load(new Random());
    }
    switch (type) {
      case GOLD -> player.changeGoldBy(value);
      case EXPERIENCE -> player.addExperience(value);
    }
  }

  public enum Type {
    GOLD,
    EXPERIENCE
  }

  @Override
  public String toString() {
    var colour = CliComponent.colourFrom(type).toString();
    return colour + value + FMT.RESET + " " + type.toString().toLowerCase();
  }
}
