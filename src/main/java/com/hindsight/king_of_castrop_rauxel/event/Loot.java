package com.hindsight.king_of_castrop_rauxel.event;

import com.hindsight.king_of_castrop_rauxel.character.Player;
import com.hindsight.king_of_castrop_rauxel.cli.CliComponent.FMT;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Loot {

  private final List<Reward> list = new ArrayList<>();

  public Loot(List<Reward> rewards) {
    list.addAll(rewards);
  }

  public void add(Reward reward) {
    list.add(reward);
  }

  public void add(Loot loot) {
    list.addAll(loot.getList());
  }

  public Loot experience(int experience) {
    list.add(new Reward(Reward.Type.EXPERIENCE, experience));
    return this;
  }

  public Loot gold(int gold) {
    list.add(new Reward(Reward.Type.GOLD, gold));
    return this;
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
