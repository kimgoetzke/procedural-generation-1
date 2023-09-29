package com.hindsight.king_of_castrop_rauxel.action.poi;

import static com.hindsight.king_of_castrop_rauxel.characters.Player.*;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * This action changes the player's current POI. It never changes the location. However, it does
 * change the player's state to AT_SPECIFIC_POI, so that the player sees the POI's actions next. It
 * is NOT used for any actions at the POI itself (unless changing POI is part of a quest).
 */
@Getter
@Builder
public class PoiAction implements Action {

  @Setter private int index;
  private String name;
  private static final State NEXT_STATE = State.AT_SPECIFIC_POI;
  private PointOfInterest poi;

  @Override
  public void execute(Player player) {
    player.setState(NEXT_STATE);
    player.setCurrentPoi(poi);
  }

  public State getNextState() {
    return NEXT_STATE;
  }
}
