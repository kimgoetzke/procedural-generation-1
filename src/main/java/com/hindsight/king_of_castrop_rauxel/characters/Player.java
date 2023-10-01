package com.hindsight.king_of_castrop_rauxel.characters;

import com.hindsight.king_of_castrop_rauxel.event.Event;
import com.hindsight.king_of_castrop_rauxel.location.Location;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import com.hindsight.king_of_castrop_rauxel.world.Coordinates;

import java.util.*;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

@Slf4j
@Getter
public class Player implements Visitor {

  private final String id;
  private final String name;
  private final Set<Location> visitedLocations = new LinkedHashSet<>();
  private final List<Event> events = new ArrayList<>();
  private final Coordinates coordinates;
  @Setter private int gold = 100;
  @Setter private int level;
  @Setter private int age = 15;
  @Setter private int activityPoints = 20;
  private PlayerState state = PlayerState.AT_DEFAULT_POI;
  private final CliSettings cli = new CliSettings();
  private Location currentLocation;
  private PointOfInterest currentPoi;
  @Setter private Event currentEvent;

  public enum PlayerState {
    AT_DEFAULT_POI,
    CHOOSE_POI,
    AT_SPECIFIC_POI,
    EVENT,
    DEBUG
  }

  public Player(
      String name, @NonNull Location currentLocation, Pair<Integer, Integer> worldCoords) {
    this.name = name;
    this.id = "PLA~" + UUID.randomUUID();
    this.coordinates = new Coordinates(worldCoords, currentLocation.getCoordinates().getChunk());
    this.currentLocation = currentLocation;
    this.currentPoi = currentLocation.getDefaultPoi();
    visitedLocations.add(currentLocation);
    currentLocation.addVisitor(this);
  }

  public void setCurrentPoi(PointOfInterest currentPoi) {
    var location = currentPoi.getParent();
    this.currentLocation = location;
    this.currentPoi = currentPoi;
    this.coordinates.setTo(location.getCoordinates().getGlobal());
    visitedLocations.add(location);
    location.addVisitor(this);
  }

  public void addEvent(Event event) {
    events.add(event);
  }

  public void setState(PlayerState state) {
    this.state = state;
    updateCliState();
  }

  public void updateCliState() {
    log.info("Updating CLI state to {}", state);
    switch (state) {
      case AT_DEFAULT_POI, AT_SPECIFIC_POI, CHOOSE_POI, DEBUG -> cli.reset();
      case EVENT -> {
        cli.setPrepareActions(true);
        cli.setPrintActions(true);
        cli.setTakeAction(true);
        cli.setPrintResponse(true);
        cli.setPostProcess(true);
      }
    }
  }

  @Setter
  public static final class CliSettings {
    private boolean printHeaders;
    private boolean prepareActions;
    private boolean printActions;
    private boolean takeAction;
    private boolean printResponse;
    private boolean postProcess;

    public CliSettings() {
      reset();
    }

    public boolean printHeaders() {
      return printHeaders;
    }

    public boolean prepareActions() {
      return prepareActions;
    }

    public boolean printActions() {
      return printActions;
    }

    public boolean takeAction() {
      return takeAction;
    }

    public boolean postProcess() {
      return postProcess;
    }

    public boolean printResponse() {
      return printResponse;
    }

    public void reset() {
      this.printHeaders = true;
      this.prepareActions = true;
      this.printActions = true;
      this.takeAction = true;
      this.printResponse = false;
      this.postProcess = true;
    }
  }
}
