# How to create event YAML files

This document describes how to create event YAML files. The project uses YAML files to define the content for events.
YAML files stored in `/src/main/resources/events` are read by `YamlReader` and loaded at runtime by the application.

## Structure

YAML files are read to `EventDto` objects. They require the following elements:

1. `eventDetails` -> `EventDetails`
2. `participantData` -> `Map<Role, List<Dialogue>>`

## Event details

The `eventDetails` element must contain the `eventType` which is parsed to `EventType` enum (see `Event` class).
All other fields of the class are optional. An `id` is generated automatically and should not be provided.

## Participant data

The `participantData` element must contain a `Map` of `Role` to `List<Dialogue>`. The `Role` is parsed to `Role` enum (
see `Role` class).

Example:

```yaml
participantData:
  EVENT_GIVER: # Role enum
    - !dialogue # Indicate mapping to Dialogue class
      state: AVAILABLE # Event.State enum
      interactions: # List<Interaction> class 
        - text: Hello! # An interaction; type String
          i: 0 # Optional, NOT parsed; type int; interaction number to make it easier to link actions to interactions
        - text: How are you?
          i: 1
          actions:
            - !action # Indicate mapping to Action class
              name: (Accept) Alright, I'll do it # Action name, type String
              eventState: NONE # Event.State enum
              nextInteraction: 1 # The next interaction to display after selection;
              # this example will lead to an infinite loop 
```

### Actions

- The dialogue of an event can only be exited through an action
- An action can change the state of the event (`eventState`) e.g. from `AVAILABLE` to `COMPLETED`
- An action can also change the state of the player (`playerState`) e.g. from `IN_DIALOGUE` to `AT_POI`, marking the end
  of the dialogue
- Both `eventState` and `playerState` can be changed in a single action
- If an action should only advance the dialogue to a different interaction, you must set `eventState` to `NONE` and set
  `nextInteraction` to the index of the next interaction

## Other notes

Due to the scope and purpose of this project, there is currently no pre-processing of the YAML files. This means that
the YAML files are read when they are assigned to an NPC. This means that any errors in the YAML files will only be
detected at that point.