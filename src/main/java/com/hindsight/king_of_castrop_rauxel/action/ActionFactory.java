package com.hindsight.king_of_castrop_rauxel.action;

import com.hindsight.king_of_castrop_rauxel.character.Player;
import com.hindsight.king_of_castrop_rauxel.configuration.EnvironmentResolver;
import com.hindsight.king_of_castrop_rauxel.encounter.EncounterSequence;
import com.hindsight.king_of_castrop_rauxel.location.Location;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ActionFactory {

  private final EnvironmentResolver environmentResolver;

  public <T> PrintAction<T> printAction(int index, String name, String header, List<T> toPrint) {
    return PrintAction.<T>builder()
        .environment(environmentResolver.getEnvironment())
        .index(index)
        .name(name)
        .header(header)
        .toPrint(toPrint)
        .build();
  }

  public StateAction stateAction(int index, String name, Player.State nextState) {
    return StateAction.builder()
        .environment(environmentResolver.getEnvironment())
        .index(index)
        .name(name)
        .nextState(nextState)
        .build();
  }

  public PoiAction poiAction(int index, String name, PointOfInterest poi) {
    return PoiAction.builder()
        .environment(environmentResolver.getEnvironment())
        .index(index)
        .name(name)
        .poi(poi)
        .build();
  }

  public LocationAction locationAction(int index, String name, Location location) {
    return LocationAction.builder()
        .environment(environmentResolver.getEnvironment())
        .index(index)
        .name(name)
        .location(location)
        .build();
  }

  public CombatAction combatAction(int index, String name, EncounterSequence sequence) {
    return CombatAction.builder()
        .environment(environmentResolver.getEnvironment())
        .index(index)
        .name(name)
        .sequence(sequence)
        .build();
  }

  public ExitAction exitAction(int index, String name) {
    return ExitAction.builder()
        .environment(environmentResolver.getEnvironment())
        .index(index)
        .name(name)
        .build();
  }
}
