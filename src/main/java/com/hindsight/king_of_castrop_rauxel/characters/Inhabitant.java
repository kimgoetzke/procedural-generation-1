package com.hindsight.king_of_castrop_rauxel.characters;

import com.hindsight.king_of_castrop_rauxel.event.Event;
import com.hindsight.king_of_castrop_rauxel.location.PointOfInterest;
import com.hindsight.king_of_castrop_rauxel.utils.EventGenerator;
import com.hindsight.king_of_castrop_rauxel.utils.Generators;
import com.hindsight.king_of_castrop_rauxel.utils.NameGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode(exclude = {"nameGenerator", "eventGenerator", "primaryEvent", "secondaryEvents"})
public class Inhabitant implements Npc {

  private final NameGenerator nameGenerator;
  private final EventGenerator eventGenerator;
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
    this.nameGenerator = generators.nameGenerator();
    this.eventGenerator = generators.eventGenerator();
    load();
    logResult();
  }

  @Override
  public void addSecondaryEvent(Event event) {
    if (event.equals(primaryEvent)) {
      return;
    }
    secondaryEvents.add(event);
  }

  @Override
  public void load() {
    id = "NPC~" + UUID.randomUUID();
    firstName = nameGenerator.npcFirstNameFrom(Inhabitant.class);
    lastName = nameGenerator.npcLastNameFrom(Inhabitant.class);
    fullName = firstName + " " + lastName;
  }

  @Override
  public String getName() {
    return firstName + " " + lastName;
  }

  @Override
  public void setHome(PointOfInterest home) {
    this.home = home;
    log.info("Set home of '{}' to: {}", this.fullName, home);
  }

  @Override
  public void loadPrimaryEvent() {
    var randomInt = random.nextInt(Event.Type.values().length);
    var eventCandidate =
        switch (randomInt) {
          case 0 -> eventGenerator.singleStepDialogue(this);
          case 1 -> eventGenerator.multiStepDialogue(this);
          case 2 -> eventGenerator.deliveryEvent(this);
          default -> throw new IllegalStateException(
              "You forgot to implement every event type in the Inhabitant class: " + randomInt);
        };
    primaryEvent =
        eventCandidate == null ? eventGenerator.singleStepDialogue(this) : eventCandidate;
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
