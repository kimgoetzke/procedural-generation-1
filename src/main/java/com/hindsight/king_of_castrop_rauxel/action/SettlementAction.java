package com.hindsight.king_of_castrop_rauxel.action;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.location.Location;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Builder
public class SettlementAction implements Action {
  private int number;
  private String name;
  private Location location;

  @Override
  public boolean execute(Player player) {
    log.debug("Action {} has not been implemented yet", getName());
    return false;
  }

  @Override
  public String print() {
    return "[%s] %s%n".formatted(number, name);
  }
}
