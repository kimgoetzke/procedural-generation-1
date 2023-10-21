package com.hindsight.king_of_castrop_rauxel.event;

import static com.hindsight.king_of_castrop_rauxel.cli.CliComponent.*;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class Rewards {

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

  // TODO: Only show what's there
  public void print() {
    System.out.printf(
        "Gold: %s%s%s, Experience: %s%s%s%n",
        FMT.YELLOW_BOLD, getGold(), FMT.RESET, FMT.BLUE_BOLD, getExperience(), FMT.RESET);
  }
}
