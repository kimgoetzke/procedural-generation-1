package com.hindsight.king_of_castrop_rauxel.action;

import static com.hindsight.king_of_castrop_rauxel.characters.Player.*;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.cli.ProgressBar;
import com.hindsight.king_of_castrop_rauxel.location.Location;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
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
    var thread = loadLocation();
    ProgressBar.displayProgress(player.getCurrentLocation(), location);
    try {
      thread.join();
    } catch (InterruptedException e) {
      cancelAction(player, player.getCurrentPoi());
    }
    executeAction(player, location.getDefaultPoi());
  }

  private Thread loadLocation() {
    var thread =
        new Thread(
            () -> {
              if (!location.isLoaded()) {
                location.load();
              }
            });
    thread.start();
    return thread;
  }

  private void cancelAction(Player player, PointOfInterest poiLeaving) {
    System.out.printf(
        "%nSeems like you didn't know the way because you ended up where you started.%n");
    Thread.currentThread().interrupt();
    executeAction(player, poiLeaving);
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
