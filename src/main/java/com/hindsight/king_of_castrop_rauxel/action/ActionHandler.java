package com.hindsight.king_of_castrop_rauxel.action;

import static com.hindsight.king_of_castrop_rauxel.characters.Player.State.*;

import com.hindsight.king_of_castrop_rauxel.action.debug.DebugActionFactory;
import com.hindsight.king_of_castrop_rauxel.characters.Player;
import com.hindsight.king_of_castrop_rauxel.cli.CliComponent;
import com.hindsight.king_of_castrop_rauxel.configuration.EnvironmentResolver;
import com.hindsight.king_of_castrop_rauxel.location.Dungeon;
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

  private void prepend(List<Action> actions, boolean showDebugMenu) {
    actions.clear();
    if (environmentResolver.isDev() && showDebugMenu) {
      actions.add(new StateAction(1, "Show debug menu", DEBUGGING));
    }
  }

  private void append(List<Action> actions) {
    if (environmentResolver.isCli()) {
      actions.add(new ExitAction(index(actions), "Exit game"));
    }
  }

  public void getChoosePoiActions(Player player, List<Action> actions) {
    prepend(actions, true);
    var poi = player.getCurrentPoi();
    var location = player.getCurrentLocation();
    var stayHereAction = new PoiAction(index(actions), "Stay at " + poi.getName(), poi);
    actions.add(stayHereAction);
    addAllActionsFrom(location.getAvailableActions(), actions, stayHereAction);
    append(actions);
  }

  public void getThisPoiActions(Player player, List<Action> actions) {
    prepend(actions, true);
    var poi = player.getCurrentPoi();
    var currentLocation = player.getCurrentLocation();
    addGoToPoiAction(actions, currentLocation);
    if (poi == currentLocation.getDefaultPoi()) {
      addLocationActions(actions, currentLocation, player);
    }
    addAllActionsFrom(poi.getAvailableActions(), actions);
    append(actions);
  }

  public void getDialogueActions(Player player, List<Action> actions) {
    prepend(actions, false);
    var eventActions = player.getCurrentEvent().getCurrentActions();
    addAllActionsFrom(eventActions, actions);
  }

  public void getCombatActions(Player player, List<Action> actions) {
    prepend(actions, false);
    if (player.getCurrentPoi() instanceof Dungeon dungeon) {
      var sequence = dungeon.getSequence();
      if (sequence.isInProgress()) {
        actions.add(new CombatAction(index(actions), "Press on", sequence));
        actions.add(new StateAction(index(actions), "Retreat (for now)", AT_POI));
      } else {
        actions.add(new StateAction(index(actions), "Return victoriously", AT_POI));
      }
    }
  }

  public void getDebugActions(Player player, List<Action> actions) {
    prepend(actions, true);
    actions.remove(0);
    actions.add(new LocationAction(index(actions), "Resume game", player.getCurrentLocation()));
    actions.add(debug.create(index(actions), "Log memory usage", debug::logMemoryStats));
    actions.add(debug.create(index(actions), "Log all locations", debug::logVertices));
    actions.add(
        debug.create(
            index(actions),
            "Log locations inside trigger zone",
            () -> debug.logLocationsInsideTriggerZone(player)));
    actions.add(
        debug.create(
            index(actions), "Log visited locations", () -> debug.logVisitedLocations(player)));
    actions.add(debug.create(index(actions), "Log graph connectivity", debug::printConnectivity));
    actions.add(debug.create(index(actions), "Log graph edges & distances", debug::logGraph));
    actions.add(debug.create(index(actions), "Log close chunks", debug::logWorld));
    actions.add(debug.create(index(actions), "Print visualised plane", debug::printPlane));
    append(actions);
  }

  public void getNone(List<Action> actions) {
    actions.clear();
  }

  private static int index(List<Action> actions) {
    return actions.size() + 1;
  }

  private static void addGoToPoiAction(List<Action> actions, Location currentLocation) {
    actions.add(
        new StateAction(
            index(actions),
            "Go to...%s"
                .formatted(
                    CliComponent.label(
                        "%s point(s) of interest"
                            .formatted(currentLocation.getPointsOfInterest().size() - 1),
                        CliComponent.FMT.BLUE)),
            CHOOSING_POI));
  }

  private static void addLocationActions(List<Action> to, Location currentLocation, Player player) {
    var from = currentLocation.getNeighbours().stream().toList();
    for (var neighbour : from) {
      to.add(
          new LocationAction(
              index(to),
              "Travel to %s (%s km %s%s)%s"
                  .formatted(
                      neighbour.getName(),
                      neighbour.distanceTo(currentLocation),
                      neighbour
                          .getCardinalDirection(player.getCoordinates().getChunk())
                          .getName()
                          .toLowerCase(),
                      player.getVisitedLocations().stream().anyMatch(a -> a.equals(neighbour))
                          ? ""
                          : ", unvisited",
                      CliComponent.label("Location", CliComponent.FMT.BLUE)),
              neighbour));
    }
  }

  private static void addAllActionsFrom(List<Action> from, List<Action> to) {
    var adjustedActions = new ArrayList<>(from);
    for (var action : adjustedActions) {
      action.setIndex(index(to));
      to.add(action);
    }
  }

  private static void addAllActionsFrom(List<Action> from, List<Action> to, PoiAction except) {
    var adjustedActions = new ArrayList<>(from);
    for (var action : adjustedActions) {
      if (action.getName().contains(except.getPoi().getName())) {
        continue;
      }
      action.setIndex(index(to));
      to.add(action);
    }
  }
}
