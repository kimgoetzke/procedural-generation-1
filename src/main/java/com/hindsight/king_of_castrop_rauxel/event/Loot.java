package com.hindsight.king_of_castrop_rauxel.event;

import static com.hindsight.king_of_castrop_rauxel.cli.CliComponent.*;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class Loot {

  private final List<Reward> list = new ArrayList<>();

  public void add(Reward reward) {
    list.add(reward);
  }

  public void addAll(List<Reward> rewards) {
    list.addAll(rewards);
  }

  public void give(Player player) {
    list.forEach(reward -> reward.give(player));
  }

  public int getGold() {
    return list.stream()
        .filter(reward -> reward.getType() == Reward.Type.GOLD)
        .mapToInt(Reward::getValue)
        .sum();
  }

  public int getExperience() {
    return list.stream()
        .filter(reward -> reward.getType() == Reward.Type.EXPERIENCE)
        .mapToInt(Reward::getValue)
        .sum();
  }

  @Override
  public String toString() {
    var rewards = new StringBuilder();
    int gold = getGold();
    if (gold > 0) {
      rewards.append(FMT.YELLOW_BOLD).append(gold).append(FMT.RESET).append(" gold");
    }
    if (rewards.length() > 1) {
      rewards.append(" and ");
    }
    int exp = getExperience();
    if (exp > 0) {
      rewards.append(FMT.BLUE_BOLD).append(exp).append(FMT.RESET).append(" XP");
    }
    return rewards.toString();
  }
}
