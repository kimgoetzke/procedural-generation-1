package com.hindsight.king_of_castrop_rauxel.action;

import static com.hindsight.king_of_castrop_rauxel.characters.Player.PlayerState.*;

import com.hindsight.king_of_castrop_rauxel.action.debug.DebugActionFactory;
import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.configuration.EnvironmentResolver;
import com.hindsight.king_of_castrop_rauxel.location.Location;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE, onConstructor = @__(@Autowired))
public class ActionHandler {

  private final EnvironmentResolver environmentResolver;
  private final DebugActionFactory debug;

  private void prepend(List<Action> actions) {
    actions.clear();
    if (environmentResolver.isDev()) {
      actions.add(new StateAction(1, "Show debug menu", DEBUG));
    }
  }

  private void append(List<Action> actions) {
    if (environmentResolver.isCli()) {
      actions.add(new ExitAction(actions.size() + 1, "Exit game"));
    }
  }

  public void getDefaultPoiActions(Player player, List<Action> actions) {
    prepend(actions);
    var currentLocation = player.getCurrentLocation();
    actions.add(
        new StateAction(
            actions.size() + 1,
            "Explore any of the %s point(s) of interest"
                .formatted(currentLocation.getPointsOfInterest().size() - 1),
            CHOOSE_POI));
    var neighbours = currentLocation.getNeighbours().stream().toList();
    addLocationActions(neighbours, actions, currentLocation, player);
    append(actions);
  }

  public void getAllPoiActions(Player player, List<Action> actions) {
    prepend(actions);
    var poi = player.getCurrentPoi();
    var location = player.getCurrentLocation();
    var defaultAction =
        new LocationAction(actions.size() + 1, "Stay at " + poi.getName(), location);
    actions.add(defaultAction);
    addAllActions(location.getAvailableActions(), actions, defaultAction);
    append(actions);
  }

  public void getThisPoiActions(Player player, List<Action> actions) {
    prepend(actions);
    var poi = player.getCurrentPoi();
    var location = player.getCurrentLocation();
    var defaultAction =
        new LocationAction(
            actions.size() + 1, "Go back to " + location.getDefaultPoi().getName(), location);
    actions.add(defaultAction);
    addAllActions(poi.getAvailableActions(), actions, defaultAction);
    append(actions);
  }

  public void getDialogueActions(Player player, List<Action> actions) {
    var eventActions = player.getCurrentEvent().getNextInteraction().actions();
    actions.addAll(eventActions);
  }

  public void getDebugActions(Player player, List<Action> actions) {
    prepend(actions);
    actions.remove(0);
    actions.add(new LocationAction(actions.size() + 1, "Resume game", player.getCurrentLocation()));
    actions.add(debug.create(actions.size() + 1, "Log memory usage", debug::logMemoryStats));
    actions.add(debug.create(actions.size() + 1, "Log all locations", debug::logVertices));
    actions.add(
        debug.create(
            actions.size() + 1,
            "Log locations inside trigger zone",
            () -> debug.logLocationsInsideTriggerZone(player)));
    actions.add(
        debug.create(
            actions.size() + 1, "Log visited locations", () -> debug.logVisitedLocations(player)));
    actions.add(
        debug.create(actions.size() + 1, "Log graph connectivity", debug::printConnectivity));
    actions.add(debug.create(actions.size() + 1, "Log graph edges & distances", debug::logGraph));
    actions.add(debug.create(actions.size() + 1, "Log close chunks", debug::logWorld));
    actions.add(debug.create(actions.size() + 1, "Visualise plane", debug::printPlane));
    append(actions);
  }

  public List<Action> getEmpty() {
    return new ArrayList<>();
  }

  private static void addLocationActions(
      List<Location> from, List<Action> to, Location currentLocation, Player player) {
    for (var neighbour : from) {
      to.add(
          new LocationAction(
              to.size() + 1,
              "Travel to %s (%s km %s%s)"
                  .formatted(
                      neighbour.getName(),
                      neighbour.distanceTo(currentLocation),
                      neighbour
                          .getCardinalDirection(player.getCoordinates().getChunk())
                          .getName()
                          .toLowerCase(),
                      player.getVisitedLocations().stream().anyMatch(a -> a.equals(neighbour))
                          ? ""
                          : ", unvisited"),
              neighbour));
    }
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
}
