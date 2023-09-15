package com.hindsight.king_of_castrop_rauxel.action;

import com.hindsight.king_of_castrop_rauxel.location.Location;
import com.hindsight.king_of_castrop_rauxel.location.Settlement;
import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ActionTemplate {

  public static List<Action> defaultLocationActions(Location settlement) {
    List<Action> actions = new ArrayList<>();
    actions.add(
        LocationAction.builder()
            .number(1)
            .name(
                "Explore any of the %s points of interest in %s"
                    .formatted(settlement.getPointsOfInterest(), settlement.getName()))
            .location(settlement)
            .build());
    var neighbours = settlement.getNeighbours().stream().toList();
    for (Location neighbour : neighbours) {
      actions.add(
          new LocationAction(
              actions.size() + 1, "Go to %s".formatted(neighbour.getName()), neighbour));
    }
    actions.add(new ExitAction(actions.size() + 1, "Exit game"));
    return actions;
  }

  public static List<Action> defaultPoiActions(Settlement settlement) {
    List<Action> actions = new ArrayList<>();
    actions.add(new LocationAction(1, "Stay in " + settlement.getName(), settlement));
    for (Location n : settlement.getNeighbours()) {
      actions.add(new LocationAction(actions.size() + 1, "Go to " + n.getName(), n));
    }
    return actions;
  }
}
