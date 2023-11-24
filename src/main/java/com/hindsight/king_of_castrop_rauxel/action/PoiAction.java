package com.hindsight.king_of_castrop_rauxel.action;

import static com.hindsight.king_of_castrop_rauxel.character.Player.*;

import com.hindsight.king_of_castrop_rauxel.character.Player;
import com.hindsight.king_of_castrop_rauxel.configuration.EnvironmentResolver;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * This action changes the player's current POI. It never changes the location. However, it does
 * change the player's state to AT_POI, so that the player sees the POI's actions next. It is NOT
 * used for any actions at the POI itself (unless changing POI is part of a quest).
 */
@Getter
@Builder
public class PoiAction implements Action {

  @Setter private EnvironmentResolver.Environment environment;
  @Setter private int index;
  @Setter private String name;
  private PointOfInterest poi;

  @Override
  public void execute(Player player) {
    nextState(player);
    player.setCurrentPoi(poi);
  }

  @Override
  public State getNextState() {
    return State.AT_POI;
  }
}
