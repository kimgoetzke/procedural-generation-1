package com.hindsight.king_of_castrop_rauxel.action;

import static com.hindsight.king_of_castrop_rauxel.characters.Player.*;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.cli.ProgressBar;
import com.hindsight.king_of_castrop_rauxel.location.Location;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class LocationAction implements Action {

  @Setter private int index;
  private String name;
  private static final State NEXT_STATE = State.AT_DEFAULT_POI;
  private Location location;

  @Override
  public void execute(Player player) {
    var poiLeaving = player.getCurrentPoi();
    if (location.isLoaded()) {
      executeAction(player, location.getDefaultPoi());
      return;
    }
    generateLocationThenExecuteAction(player, poiLeaving);
  }

  private void generateLocationThenExecuteAction(Player player, PointOfInterest poiLeaving) {
    var locationFuture = generateLocation();
    ProgressBar.displayProgress(player.getCurrentLocation(), location);
    try {
      locationFuture.get();
      executeAction(player, location.getDefaultPoi());
    } catch (ExecutionException | InterruptedException e) {
      Thread.currentThread().interrupt();
      System.out.printf(
          "%nSeems like you didn't know the way because you ended up where you started.%n");
      executeAction(player, poiLeaving);
    }
  }

  private Future<?> generateLocation() {
    try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
      return executor.submit(() -> location.load());
    }
  }

  private void executeAction(Player player, PointOfInterest poiVisiting) {
    System.out.println();
    setPlayerState(player);
    player.setCurrentPoi(poiVisiting);
  }

  @Override
  public State getNextState() {
    return NEXT_STATE;
  }
}
