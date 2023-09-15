package com.hindsight.king_of_castrop_rauxel.action;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PoiAction implements Action {
  private int number;
  private String name;
  private PointOfInterest location;

  @Override
  public boolean execute(Player player) {
    player.setCurrentPointOfInterest(location);
    return true;
  }
}
