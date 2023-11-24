package com.hindsight.king_of_castrop_rauxel.action;

import static com.hindsight.king_of_castrop_rauxel.character.Player.*;

import com.hindsight.king_of_castrop_rauxel.configuration.EnvironmentResolver;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * All actions change the player's state and something else. This action only changes the state of
 * the player. For example, changing the state to DEBUG will show the debug menu. Changing the state
 * to CHOOSE_POI will show the list of POIs in the current location. This action never changes the
 * player's current location or POI.
 */
@Slf4j
@Getter
@Setter
@Builder
public class StateAction implements Action {

  @Setter private EnvironmentResolver.Environment environment;
  private int index;
  private String name;
  private State nextState;
}
