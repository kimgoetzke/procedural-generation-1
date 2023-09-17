package com.hindsight.king_of_castrop_rauxel.action;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.cli.ProgressBar;
import com.hindsight.king_of_castrop_rauxel.location.Location;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
    if (location.isGenerated()) {
      concludeAction(player, location.getDefaultPoi());
      return;
    }
    generateSettlementAndConcludeAction(player);
  }

  private void generateSettlementAndConcludeAction(Player player) {
    var poiLeft = player.getCurrentPoi();
    var settlementFuture = generateSettlementOnSeparateThread();
    ProgressBar.displayProgress(player.getCurrentLocation(), location);
    try {
      settlementFuture.get();
      concludeAction(player, location.getDefaultPoi());
    } catch (ExecutionException | InterruptedException e) {
      Thread.currentThread().interrupt();
      System.out.printf(
          "%nSeems like you didn't know the way because you ended up where you started.%n");
      concludeAction(player, poiLeft);
    }
  }

  private void concludeAction(Player player, PointOfInterest poi) {
    setPlayerState(player);
    player.setCurrentPoi(poi);
  }

  private Future<?> generateSettlementOnSeparateThread() {
    try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
      return executor.submit(() -> location.generate());
    }
  }

  public State getNextState() {
    return NEXT_STATE;
  }
}
