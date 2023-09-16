package com.hindsight.king_of_castrop_rauxel.action;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.location.Location;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.hindsight.king_of_castrop_rauxel.characters.Player.*;

@Getter
@Builder
public class LocationAction implements Action {

  @Setter private int index;
  private String name;
  private static final State NEXT_STATE = State.AT_DEFAULT_POI;
  private Location location;

  @Override
  public void execute(Player player, List<Action> actions) {
    setPlayerState(player);
    player.setCurrentLocation(location);
    player.setCurrentPoi(location.getDefaultPoi());
  }

  public State getNextState() {
    return NEXT_STATE;
  }
}
