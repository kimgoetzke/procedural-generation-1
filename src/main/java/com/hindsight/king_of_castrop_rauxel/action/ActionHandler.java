package com.hindsight.king_of_castrop_rauxel.action;

import static com.hindsight.king_of_castrop_rauxel.character.Player.State.*;

import com.hindsight.king_of_castrop_rauxel.action.debug.DebugActionFactory;
import com.hindsight.king_of_castrop_rauxel.character.Player;
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
  private final ActionFactory af;
  private final DebugActionFactory daf;

  private void prepend(List<Action> actions) {
    actions.clear();
  }

  private void append(List<Action> actions) {
    if (environmentResolver.isDev()) {
      actions.add(af.stateAction(index(actions), "Show debug menu", DEBUGGING));
    }
    if (environmentResolver.isCli()) {
      actions.add(af.stateAction(index(actions), "Open menu", IN_MENU));
    }
  }

  public void getChoosePoiActions(Player player, List<Action> actions) {
    prepend(actions);
    var poi = player.getCurrentPoi();
    var stayHereAction = af.poiAction(index(actions), "Stay at " + poi.getName(), poi);
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
    if (eventActions.isEmpty() && environmentResolver.isNotCli()) {
      actions.add(af.stateAction(index(actions), "End conversation", AT_POI));
    }
    addAllActionsFrom(eventActions, actions);
  }

  public void getCombatActions(Player player, List<Action> actions) {
    prepend(actions);
    if (player.getCurrentPoi() instanceof Dungeon dungeon) {
      var sequence = dungeon.getSequence();
      if (sequence.isInProgress()) {
        actions.add(af.combatAction(index(actions), "Press on", sequence));
        actions.add(af.stateAction(index(actions), "Retreat (for now)", AT_POI));
      } else {
        actions.add(af.stateAction(index(actions), "Return victoriously", AT_POI));
      }
    }
  }

  // TODO: Make sure that Event has a good toString() method
  public void getCliMenuActions(Player player, List<Action> actions) {
    prepend(actions);
    actions.add(af.stateAction(index(actions), "Resume game", AT_POI));
    var header = "Currently active quests:";
    var activeQuests = player.getActiveEvents();
    actions.add(af.printAction(index(actions), "View active quests", header, activeQuests));
    actions.add(af.exitAction(index(actions), "Exit game"));
  }

  public void getDebugActions(Player player, List<Action> actions) {
    prepend(actions);
    var triggerZone = (Runnable) () -> daf.logLocationsInsideTriggerZone(player);
    var visitedLocs = (Runnable) () -> daf.logVisitedLocations(player);
    actions.add(af.locationAction(index(actions), "Resume game", player.getCurrentLocation()));
    actions.add(daf.create(index(actions), "Add 1000 gold", () -> daf.addGold(player)));
    actions.add(daf.create(index(actions), "Log memory usage", daf::logMemoryStats));
    actions.add(daf.create(index(actions), "Log all locations", daf::logVertices));
    actions.add(daf.create(index(actions), "Log locations inside trigger zone", triggerZone));
    var visitedLocsAction = daf.create(index(actions), "Log visited locations", visitedLocs);
    actions.add(visitedLocsAction);
    actions.add(daf.create(index(actions), "Log graph connectivity", daf::printConnectivity));
    actions.add(daf.create(index(actions), "Log graph edges & distances", daf::logGraph));
    actions.add(daf.create(index(actions), "Log close chunks", daf::logWorld));
    actions.add(daf.create(index(actions), "Log visualised plane", daf::logPlane));
    actions.add(daf.create(index(actions), "Log chunk target levels", daf::logWorldLevels));
    append(actions);
  }

  public void getNone(List<Action> actions) {
    actions.clear();
  }

  private static int index(List<Action> actions) {
    return actions.size() + 1;
  }

  private void addGoToPoiAction(List<Action> actions, Location currentLocation) {
    var poisCount = currentLocation.getPointsOfInterest().size() - 1;
    actions.add(af.stateAction(index(actions), getGoToActionName(poisCount), CHOOSING_POI));
  }

  private static String getGoToActionName(int poisCount) {
    var labelText = "%s point(s) of interest".formatted(poisCount);
    var formattedLabel = CliComponent.label(labelText, CliComponent.FMT.BLUE);
    return "Go to...%s".formatted(formattedLabel);
  }

  private void addLocationActions(List<Action> to, Location currentLocation, Player player) {
    var from = currentLocation.getNeighbours().stream().toList();
    for (var n : from) {
      var action =
          af.locationAction(index(to), getLocationActionName(currentLocation, player, n), n);
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
