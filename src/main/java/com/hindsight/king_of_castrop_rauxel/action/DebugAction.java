package com.hindsight.king_of_castrop_rauxel.action;

import static com.hindsight.king_of_castrop_rauxel.characters.Player.*;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Builder
public class DebugAction<T> implements Action {

  @Setter private int index;
  private String name;
  private static final State NEXT_STATE = State.AT_DEFAULT_POI;
  private Class<T> clazz;

  @Override
  public void execute(Player player, List<Action> actions) {
    setPlayerState(player);
    log.info("Debug action triggered for class: {}", clazz.getSimpleName());
  }

  public State getNextState() {
    return NEXT_STATE;
  }
}
