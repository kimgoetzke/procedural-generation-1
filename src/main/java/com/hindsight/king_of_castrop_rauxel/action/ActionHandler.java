package com.hindsight.king_of_castrop_rauxel.action;

import static com.hindsight.king_of_castrop_rauxel.characters.Player.State.*;

import com.hindsight.king_of_castrop_rauxel.action.debug.DebugActionFactory;
import com.hindsight.king_of_castrop_rauxel.action.debug.Debuggable;
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

  private void prepend(List<Action> actions) {
    actions.clear();
  }

  private void append(List<Action> actions) {
    if (environmentResolver.isDev()) {
      actions.add(new StateAction(index(actions), "Show debug menu", DEBUGGING));
    }
    if (environmentResolver.isCli()) {
      actions.add(new ExitAction(index(actions), "Exit game"));
    }
  }

  public void getChoosePoiActions(Player player, List<Action> actions) {
    prepend(actions);
    var poi = player.getCurrentPoi();
    var stayHereAction = new PoiAction(index(actions), "Stay at " + poi.getName(), poi);
    actions.add(stayHereAction);
    addAllActionsFrom(player.getCurrentLocation().getAvailableActions(), actions, stayHereAction);
    append(actions);
  }

  public void getThisPoiActions(Player player, List<Action> actions) {
    prepend(actions);
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
    prepend(actions);
    var eventActions = player.getCurrentEvent().getCurrentActions();
    addAllActionsFrom(eventActions, actions);
  }

  public void getCombatActions(Player player, List<Action> actions) {
    prepend(actions);
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
    prepend(actions);
    var triggerZone = (Debuggable) () -> debug.logLocationsInsideTriggerZone(player);
    var visitedLocs = (Debuggable) () -> debug.logVisitedLocations(player);
    actions.add(new LocationAction(index(actions), "Resume game", player.getCurrentLocation()));
    actions.add(debug.create(index(actions), "Log memory usage", debug::logMemoryStats));
    actions.add(debug.create(index(actions), "Log all locations", debug::logVertices));
    actions.add(debug.create(index(actions), "Log locations inside trigger zone", triggerZone));
    var visitedLocsAction = debug.create(index(actions), "Log visited locations", visitedLocs);
    actions.add(visitedLocsAction);
    actions.add(debug.create(index(actions), "Log graph connectivity", debug::printConnectivity));
    actions.add(debug.create(index(actions), "Log graph edges & distances", debug::logGraph));
    actions.add(debug.create(index(actions), "Log close chunks", debug::logWorld));
    actions.add(debug.create(index(actions), "Log visualised plane", debug::logPlane));
    actions.add(debug.create(index(actions), "Log chunk target levels", debug::logWorldLevels));
    append(actions);
  }

  public void getNone(List<Action> actions) {
    actions.clear();
  }

  private static int index(List<Action> actions) {
    return actions.size() + 1;
  }

  private static void addGoToPoiAction(List<Action> actions, Location currentLocation) {
    var poisCount = currentLocation.getPointsOfInterest().size() - 1;
    actions.add(new StateAction(index(actions), getGoToActionName(poisCount), CHOOSING_POI));
  }

  private static String getGoToActionName(int poisCount) {
    var labelText = "%s point(s) of interest".formatted(poisCount);
    var formattedLabel = CliComponent.label(labelText, CliComponent.FMT.BLUE);
    return "Go to...%s".formatted(formattedLabel);
  }

  private static void addLocationActions(List<Action> to, Location currentLocation, Player player) {
    var from = currentLocation.getNeighbours().stream().toList();
    for (var n : from) {
      var action =
          new LocationAction(index(to), getLocationActionName(currentLocation, player, n), n);
      to.add(action);
    }
  }

  private static String getLocationActionName(Location currentLocation, Player player, Location l) {
    var hasBeenVisited = player.getVisitedLocations().stream().anyMatch(a -> a.equals(l));
    var visitedText = hasBeenVisited ? "" : ", unvisited";
    return "Travel to %s (%s km %s%s)%s"
        .formatted(
            l.getName(),
            l.distanceTo(currentLocation),
            l.getCardinalDirection(player.getCoordinates().getChunk()).getName().toLowerCase(),
            visitedText,
            CliComponent.label(CliComponent.Type.LOCATION));
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
