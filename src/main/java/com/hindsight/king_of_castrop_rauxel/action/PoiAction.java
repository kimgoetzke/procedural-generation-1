package com.hindsight.king_of_castrop_rauxel.action;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.hindsight.king_of_castrop_rauxel.characters.Player.*;

@Getter
@Builder
public class PoiAction implements Action {

  @Setter private int index;
  private String name;
  private static final State NEXT_STATE = State.AT_SPECIFIC_POI;
  private PointOfInterest poi;

  @Override
  public void execute(Player player, List<Action> actions) {
    player.setState(NEXT_STATE);
    player.setCurrentPoi(poi);
  }

  public State getNextState() {
    return NEXT_STATE;
  }
}
