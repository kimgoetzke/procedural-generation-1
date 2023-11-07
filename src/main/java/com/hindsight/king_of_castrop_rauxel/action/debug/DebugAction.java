package com.hindsight.king_of_castrop_rauxel.action.debug;

import static com.hindsight.king_of_castrop_rauxel.characters.Player.*;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.graphs.Graph;
import com.hindsight.king_of_castrop_rauxel.world.World;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Builder
public class DebugAction implements Action {

  @Setter private int index;
  @Setter private String name;
  private Runnable runnable;
  private Graph map;
  private World world;

  @Override
  public void execute(Player player) {
    nextState(player);
    runnable.run();
  }

  public State getNextState() {
    return State.DEBUGGING;
  }
}
