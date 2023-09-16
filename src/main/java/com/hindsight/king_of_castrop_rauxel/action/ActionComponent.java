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

  public static List<Action> listForLocation(Player player) {
    var actions = new ArrayList<Action>();
    var location = player.getCurrentLocation();
    actions.add(
        StateAction.builder()
            .index(1)
            .name(
                "Explore any of the %s points of interest"
                    .formatted(location.getPointsOfInterest().size()))
            .nextState(INSIDE_LOCATION)
            .build());
    var neighbours = location.getNeighbours().stream().toList();
    for (Location neighbour : neighbours) {
      actions.add(
          new LocationAction(
              actions.size() + 1,
              "Leave %s and travel to %s".formatted(location.getName(), neighbour.getName()),
              neighbour));
    }
    actions.add(new ExitAction(actions.size() + 1, "Exit game"));
    return actions;
  }

  public static List<Action> listPois(Player player) {
    var actions = new ArrayList<Action>();
    var poi = player.getCurrentPoi();
    var location = player.getCurrentLocation();
    actions.add(new LocationAction(1, "Stay at " + poi.getName(), location));
    addAllActions(location.getAvailableActions(), actions);
    return actions;
  }

  public static List<Action> listForPoi(Player player) {
    var actions = new ArrayList<Action>();
    var poi = player.getCurrentPoi();
    var currentLocation = player.getCurrentLocation();
    actions.add(
        new LocationAction(
            1, "Go back to " + currentLocation.getDefaultPoi().getName(), currentLocation));
    actions.addAll(poi.getAvailableActions());
    return actions;
  }

  public static List<Action> emptyActions() {
    return new ArrayList<>();
  }

  private static void addAllActions(List<Action> from, List<Action> to) {
    var adjustedActions = new ArrayList<>(from);
    for (Action action : adjustedActions) {
      action.setIndex(to.size() + 1);
      to.add(action);
    }
  }
}
