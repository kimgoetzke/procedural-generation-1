package com.hindsight.king_of_castrop_rauxel.action;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Builder
public class StateAction implements Action {

  @Setter private int index;
  private String name;
  @Setter private Player.State nextState;

  @Override
  public void execute(Player player) {
    player.setState(nextState);
  }
}
