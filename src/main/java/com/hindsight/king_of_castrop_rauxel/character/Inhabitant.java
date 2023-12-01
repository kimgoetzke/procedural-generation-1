package com.hindsight.king_of_castrop_rauxel.character;

import com.hindsight.king_of_castrop_rauxel.event.Event;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import com.hindsight.king_of_castrop_rauxel.utils.Generators;
import com.hindsight.king_of_castrop_rauxel.world.IdBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode(exclude = {"generators", "primaryEvent", "secondaryEvents"})
public class Inhabitant implements Npc {

  private final Generators generators;
  private final Random random;

  @Getter private String id;
  @Getter private String firstName;
  @Getter private String lastName;
  @Getter private String fullName;
  @Getter private PointOfInterest home;
  @Getter private Event primaryEvent;
  @Getter private final List<Event> secondaryEvents = new ArrayList<>();

  public Inhabitant(Random random, Generators generators) {
    this.random = random;
    this.generators = generators;
    load();
    logResult();
  }

  @Override
  public void load() {
    id = IdBuilder.idFrom(this.getClass());
    firstName = generators.nameGenerator().npcFirstNameFrom(Inhabitant.class);
    lastName = generators.nameGenerator().npcLastNameFrom(Inhabitant.class);
    fullName = firstName + " " + lastName;
  }

  @Override
  public String getName() {
    return firstName + " " + lastName;
  }

  /** Set or resets (i.e. sets to null) the home of this inhabitant. */
  @Override
  public void setHome(PointOfInterest home) {
    this.home = home;
    log.info("Set home of '{}' to: {}", this.fullName, home);
  }

  @Override
  public void loadPrimaryEvent() {
    primaryEvent = generators.eventGenerator().generate(this);
    log.info("Loaded primary event for '{}': {}", this.fullName, primaryEvent.getEventDetails());
  }

  @Override
  public void addSecondaryEvent(Event event) {
    if (event.equals(primaryEvent)) {
      return;
    }
    secondaryEvents.add(event);
  }

  @Override
  public void logResult() {
    log.debug("Generated: {}", this);
  }

  @Override
  public String toString() {
    return fullName
        + "(id="
        + id
        + ", home="
        + home.getName()
        + ", event="
        + primaryEvent
        + ", secondaryEvents="
        + secondaryEvents
        + ')';
  }
}
