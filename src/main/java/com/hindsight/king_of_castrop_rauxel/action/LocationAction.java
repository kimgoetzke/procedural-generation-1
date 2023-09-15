package com.hindsight.king_of_castrop_rauxel.action;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.location.Location;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LocationAction implements Action {
  private int number;
  private String name;
  private Location location;

  @Override
  public boolean execute(Player player) {
    player.setCurrentLocation(location);
    return true;
  }

  @Override
  public String print() {
    return "[%s] %s%n".formatted(number, name);
  }
}
