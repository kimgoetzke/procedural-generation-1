package com.hindsight.king_of_castrop_rauxel.action;

import static com.hindsight.king_of_castrop_rauxel.character.Player.*;

import com.hindsight.king_of_castrop_rauxel.character.Player;
import com.hindsight.king_of_castrop_rauxel.cli.ProgressBar;
import com.hindsight.king_of_castrop_rauxel.configuration.EnvironmentResolver;
import com.hindsight.king_of_castrop_rauxel.location.Location;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * This action is used to change the player's current location by setting the currentPoi to the
 * defaultPoi of the new location. It also changes the player's state to AT_POI so that the player
 * can see the actions of the default POI next.
 */
@Getter
@Builder
public class LocationAction implements Action {

  @Setter private EnvironmentResolver.Environment environment;
  @Setter private int index;
  @Setter private String name;
  private Location location;

  @Override
  public void execute(Player player) {
    var thread = loadLocation();
    var speedModifier = player.getGameProperties().speedModifier();
    ProgressBar.displayProgress(player.getCurrentLocation(), location, speedModifier);
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
    nextState(player);
    player.setCurrentPoi(poiVisiting);
  }

  @Override
  public State getNextState() {
    return State.AT_POI;
  }
}
