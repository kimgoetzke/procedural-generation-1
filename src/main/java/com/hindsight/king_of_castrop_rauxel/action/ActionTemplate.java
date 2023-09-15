package com.hindsight.king_of_castrop_rauxel.action;

import com.hindsight.king_of_castrop_rauxel.location.Location;
import com.hindsight.king_of_castrop_rauxel.location.Settlement;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ActionTemplate {

  public static List<Action> defaultSettlementActions(Settlement settlement) {
    List<Action> actions = new ArrayList<>();
    actions.add(new LocationAction(1, "Leave " + settlement.getName(), null));
    actions.add(
        new SettlementAction(
            2,
            "Visit one of the %s amenities in %s"
                .formatted(settlement.getAmenities().size(), settlement.getName()),
            null));
    actions.add(new ExitAction(99, "Exit game"));
    return actions;
  }

  public static List<Action> leaveSettlementActions(Settlement settlement) {
    List<Action> actions = new ArrayList<>();
    actions.add(new LocationAction(1, "Stay in " + settlement.getName(), settlement));
    for (Location n : settlement.getNeighbours()) {
      actions.add(new LocationAction(0, "Go to " + n.getName(), n));
    }
    return actions;
  }

  public static List<Action> insideSettlementActions(Settlement settlement) {
    List<Action> actions = new ArrayList<>();
    actions.add(new LocationAction(1, "Leave " + settlement.getName(), null));
    settlement
        .getAmenities()
        .forEach(
            a ->
                actions.add(
                    new LocationAction(1, "Go to %s (%s)".formatted(a.getName(), a.getType()), a)));
    return actions;
  }
}
