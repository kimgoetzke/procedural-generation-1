package com.hindsight.king_of_castrop_rauxel.action.debug;

import static com.hindsight.king_of_castrop_rauxel.character.Player.*;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.character.Player;
import com.hindsight.king_of_castrop_rauxel.configuration.EnvironmentResolver;
import com.hindsight.king_of_castrop_rauxel.graph.Graph;
import com.hindsight.king_of_castrop_rauxel.world.World;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Builder
public class DebugAction implements Action {

  @Setter private EnvironmentResolver.Environment environment;
  @Setter private int index;
  @Setter private String name;
  private Runnable runnable;
  private Graph graph;
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
