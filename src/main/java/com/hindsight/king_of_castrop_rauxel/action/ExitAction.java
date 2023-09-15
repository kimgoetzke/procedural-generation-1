package com.hindsight.king_of_castrop_rauxel.action;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExitAction implements Action {
  private int number;
  private String name;

  @Override
  public boolean execute(Player player) {
    System.out.printf("Goodbye!%n");
    System.exit(0);
    return true;
  }

  @Override
  public String print() {
    return "[%s] %s%n".formatted(number, name);
  }
}
