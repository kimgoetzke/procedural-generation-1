package com.hindsight.king_of_castrop_rauxel.action;

import static com.hindsight.king_of_castrop_rauxel.characters.Player.State.*;

import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.location.Location;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE, onConstructor = @__(@Autowired))
public class ActionHandler {

  private final DebugActionFactory debug;

  public void getDefaultPoiActions(Player player, List<Action> actions) {
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
              "Travel to %s (%s, %s)"
                  .formatted(
                      neighbour.getName(),
                      neighbour.distanceTo(location),
                      neighbour.getCardinalDirection(player.getCurrentCoordinates()).toString()),
              neighbour));
    }
    actions.add(new ExitAction(actions.size() + 1, "Exit game"));
  }

  public void getAllPoiActions(Player player, List<Action> actions) {
    prepare(actions);
    var poi = player.getCurrentPoi();
    var location = player.getCurrentLocation();
    var defaultAction =
        new LocationAction(actions.size() + 1, "Stay at " + poi.getName(), location);
    actions.add(defaultAction);
    addAllActions(location.getAvailableActions(), actions, defaultAction);
  }

  public void getThisPoiActions(Player player, List<Action> actions) {
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

  public List<Action> getEmpty() {
    return new ArrayList<>();
  }

  private void prepare(List<Action> actions) {
    actions.clear();
    actions.add(new StateAction(1, "Show debug menu", DEBUG));
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

  public void getDebugActions(Player player, List<Action> actions) {
    prepare(actions);
    actions.remove(0);
    actions.add(new LocationAction(actions.size() + 1, "Resume game", player.getCurrentLocation()));
    actions.add(debug.create(actions.size() + 1, "Show memory usage", debug::printMemoryUsage));
    actions.add(debug.create(actions.size() + 1, "Show full graph", debug::printGraph));
    actions.add(
        debug.create(actions.size() + 1, "Show graph connectivity", debug::printConnectivity));
    actions.add(debug.create(actions.size() + 1, "Show close chunks", debug::printWorld));
    actions.add(new ExitAction(actions.size() + 1, "Exit game"));
  }
}
