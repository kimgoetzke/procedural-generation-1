package com.hindsight.king_of_castrop_rauxel.action;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.location.Location;

import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import static com.hindsight.king_of_castrop_rauxel.characters.Player.State.*;

@Component
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ActionComponent {

  public static void defaultPoi(Player player, List<Action> actions) {
    prepare(actions);
    var location = player.getCurrentLocation();
    actions.add(
        new StateAction(
            actions.size() + 1,
            "Explore any of the %s points of interest"
                .formatted(location.getPointsOfInterest().size()),
            CHOOSE_POI));
    var neighbours = location.getNeighbours().stream().toList();
    for (Location neighbour : neighbours) {
      actions.add(
          new LocationAction(
              actions.size() + 1,
              "Travel to %s (%s)".formatted(neighbour.getName(), neighbour.distanceTo(location)),
              neighbour));
    }
    actions.add(new ExitAction(actions.size() + 1, "Exit game"));
  }

  public static void allPois(Player player, List<Action> actions) {
    prepare(actions);
    var poi = player.getCurrentPoi();
    var location = player.getCurrentLocation();
    var defaultAction =
        new LocationAction(actions.size() + 1, "Stay at " + poi.getName(), location);
    actions.add(defaultAction);
    addAllActions(location.getAvailableActions(), actions, defaultAction);
  }

  public static void thisPoi(Player player, List<Action> actions) {
    prepare(actions);
    var poi = player.getCurrentPoi();
    var currentLocation = player.getCurrentLocation();
    actions.add(
        new LocationAction(
            actions.size() + 1,
            "Go back to " + currentLocation.getDefaultPoi().getName(),
            currentLocation));
    actions.addAll(poi.getAvailableActions());
  }

  public static List<Action> empty() {
    return new ArrayList<>();
  }

  private static void prepare(List<Action> actions) {
    actions.clear();
    actions.add(new StateAction(1, "Show map", SHOW_MAP));
  }

  private static void addAllActions(List<Action> from, List<Action> to, LocationAction except) {
    var adjustedActions = new ArrayList<>(from);
    for (Action action : adjustedActions) {
      if (action.getName().contains(except.getLocation().getName())) {
        continue;
      }
      action.setIndex(to.size() + 1);
      to.add(action);
    }
  }

  public static void fallback(Player player, List<Action> actions) {
    prepare(actions);
    actions.add(
        new LocationAction(
            actions.size() + 1,
            "Back to " + player.getCurrentPoi().getName(),
            player.getCurrentLocation()));
    actions.add(new ExitAction(actions.size() + 1, "Exit game"));
  }
}
