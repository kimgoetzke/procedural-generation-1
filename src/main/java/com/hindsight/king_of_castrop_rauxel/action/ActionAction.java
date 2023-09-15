package com.hindsight.king_of_castrop_rauxel.action;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Builder
public class ActionAction implements Action {
  private int number;
  private String name;
  private List<Action> actions;

  @Override
  public boolean execute(Player player) {
    log.debug("Action {} has not been implemented yet", getName());
    return false;
  }
}
