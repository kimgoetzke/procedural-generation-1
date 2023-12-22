# Procedural Generation Project 1

This project was my first attempt to procedurally generate, well, anything really. I didn't know anything about this
topic and, to keep things simple, I ended up creating an old-school text-based adventure game world where the player can
travel between locations, interact with non-player characters, complete quests, and engage in combat.

![WindowsTerminal_CD6QOmI0ST](https://github.com/kimgoetzke/procedural-generation-1/assets/120580433/6c5f9829-5e3e-468c-8150-4c2be18d3c3b)

_While this is theoretically a playable game, this is a learning project and is mostly about basic procedural
generation. It does not have a real game loop or enough content to be fun. For an actual game, check
out [this project](https://github.com/kimgoetzke/game-no-mans-gun)._

## Summary

- Procedurally generated world with over 12,500 unique settlements of various sizes
- Each containing between 1 and 34 visitable points of interest (shops, quest locations, and dungeons), as well as
  interactable non-player characters (**NPCs**)
- Thousands of (reasonably bad) names are generated at runtime for the above objects using various strategies such as
  constructing names from syllables for locations, or reading from specific files for points of interest (
  e.g. `{class}-{type}-{size}`)
- A handful of quest and dialogue templates are randomly assigned to NPCs and then tailored to the generated
  locations/characters
- By completing quests, the player gains experience, levels up, and gains gold which can be used to buy items in
  shops
- The further away the player travels from the centre of the world, enemies change and become stronger (up to tier
  6 for which the player requires level 60+ to stand a chance)
- Even after visiting thousands of locations, the game's memory utilisation should remain well below 400 MB at all times

![explorer_tlGxDPVSs2_cropped](https://github.com/kimgoetzke/procedural-generation-1/assets/120580433/d4e57b2c-5805-43a8-a1d4-edbf33c184bb)

## Features

### Procedural generation

- All objects below are generated procedurally, using a seeded `Random` object and handled by a `WorldHandler`
  instance
- The `seed` for each object is based on its global coordinates which allows (re-)generating objects when they are
  required and disposing of them when they are not
- The core object is `World` which holds `Chunk[][]`, with the player starting in the centre chunk
- Each `Chunk` holds a `Location[][]`:
    - Based on `int density`, a number of `Location`s are placed in the `Chunk`
    - `Location`s are connected using configurable strategies which result in the world's `Graph`
- The `Graph` contains the network of connections between locations and their distances
    - Each `Vertex` in the `Graph` holds a set of `Edge`s and a `LocationDto`
    - The `LocationDto` stores the `Coordinates` of the `Location` it represents, its neighbours, ID, name, and class
      name
    - This is why a `Location` itself can be unloaded and garbage collected and re-generated when required
- A `Location` (interface) contains reference to neighbouring locations, points of interest inside it, and its location
  within the chunk and world
    - The only `Location` available at this stage is a `Settlement`
    - For generation, the most important feature of a `Location` is its `Size` which determines the `PointOfInterest`
      count and, for a `Settlement`, also the `Inhabitant` count
    - Each `Location` holds a `List<PointOfInterest>` which the player can visit
- The key object a player interacts with is a `PointOfInterest` (**POI**):
    - A POIs features are determined by its `Type`
    - Currently implemented are the following `Type`s: `ENTRANCE`, `MAIN_SQUARE`, `SHOP`, `QUEST_LOCATION` and `DUNGEON`
    - At a POI, the player can engage in dialogues with NPCs other events (e.g. "delivery"
      or "kill" quests), engage in combat, or take other actions
- Each object of each layer (i.e. `World` -> `Chunk` -> `Location` -> `PointOfInterest`) can be located
  using `Coordinates`
- The web of connections and the distance between each (both of which stored in the `Graph`)
  play an important role e.g. where a player can travel to and how long it takes

### CLI player loop

- When playing the game via a CLI, the `Player`'s `State` determines the player loop that is being executed
- Each state (e.g. `PoiLoop` or `CombatLoop`) inherits from `AbstractLoop`
- A loop follows a simple sequence such as this example from the `DialogueLoop`:

```java
public class DialogueLoop extends AbstractLoop {

  @Override
  public void execute(List<Action> actions) {
    printInteraction(); // Shows the current `Interaction` from the NPCs `Dialogue`
    prepareActions(actions); // Reads the available `Action`s for current `Dialogue` from `Event`
    promptPlayer(actions); // Shows the above as a `List<Action>` & executes the selection `Action`
    postProcess(); // Handles side effects of the outcome e.g. updating the `Event`
  }
}
```

- Almost every loop will `prepareActions()` and `promptPlayer()`
- The `Action` interface is the way through which the `Player` affects the world - examples:
    - Move to a different `Location` using `LocationAction`
    - Move to a different `PointOfInterest` using `PoiAction`
    - Start with an `EventAction`
    - Changing the `Player`s `State` using `StateAction`

### Web API

- The entire game can also be played via a web API - see [How to use the API](docs/HOW_TO_API.md) for details
- You can also find a web interface
  here: [Procedural Generation 1 Frontend](https://github.com/kimgoetzke/procedural-generation-1-front-end)

### Other technical features

- Folder scanning (`FolderReader`) to read available event file names by category (e.g. events) which works inside a JAR
  and when running via an IDE
- Processing of Yaml files (`YamlReader`) which allows customising event `Participant`s based on their role (
  e.g. `eventGiver` or `eventTarget`), `List<Dialogue>` for each based on event status, `List<Reward>`, etc.
- Processing of Txt files (`TxtReader`) which is used to generate `Location`, `PointOfInterest` and `Npc` names
- Processing of `String` placeholders in Yaml or Txt files (`PlaceholderProcessor`) e.g. `&L` for location name
  or `&TOF` for the target owner's first name through which events are tailored to the current places and characters
  involved

## Technologies used

- Core: Java 19, Spring Boot 3 + Lombok, Gradle
- `guava` for String manipulation
- `snakeyaml` for Yaml processing
- `consoleui` by Andreas Wegmann for CLI-based user interface (only in JAR): https://github.com/awegmann/consoleui

This project uses `google-java-format`. See https://github.com/google/google-java-format for more details on how to set
it up and use it.

## How to use

This project's primary mode of interaction is the CLI. It also exposes an API. However, due to the lack of a web
interface, it is rather tedious to use.

### CLI: Jar

Clone and build project. Then run JAR with `cli-prod` profile:

```shell
cd build\libs 
java -jar -D"spring.profiles.active"=cli-prod procedural_generation_1-0.4.jar
```

### CLI: IDE

Clone project and run `Application.main` with `-Dspring.profiles.active=cli-prod`. During development, use
`-Dspring.profiles.active=cli-dev` to see logs.

### Web API

See [How to use the API](docs/HOW_TO_API.md). A Postman collection is available in `/docs`. A web interface can be found
in [this repository](https://github.com/kimgoetzke/procedural-generation-1-front-end).

## Other notes

### Configuration

Much of the game can be configured in the `application-world.yml` file. This includes, but is not limited to, the
following:

- World properties
    - World size (determining the number of chunks)
    - Retention zone (i.e. distance from the current chunk beyond which chunks are automatically unloaded)
- Chunk properties
    - Chunk size (in km)
    - Density range (i.e. number of locations per chunk)
    - Minimum distance between chunks
    - Maximum distance within which chunks are connected by default
    - Distance to chunk border at which the next chunk is loaded (if not already loaded)
- Settlement properties
    - Sizes and their respective characteristics (e.g. range of inhabitants, range of points of interest)
    - Dungeon properties
        - Min vs max possible encounters per dungeon
        - Min vs max possible enemies per encounter
        - Types of enemies (per tier) and property ranges for each (health, damage, etc.)

### More documentation

- [How to create event YAML files](docs/HOW_TO_YAML_EVENTS.md)
- [How to use the API](docs/HOW_TO_API.md)

### Random ideas for further development

- **User interface**:
    - Implement a web interface to use the API
    - Add visual mini-map for both CLI (using ASCII art) and web
- **Procedural generation**:
    - Implement biomes which:
        - Determine difficulty of events, attitude towards player, etc. based on environmental factors
        - Determine object characteristics such as names and types of events
    - Auto-generate items for shops by tier
    - Turn dungeons into generated mazes where the player can go from room to room
- **Code**:
    - Make all key classes Spring beans (e.g. `Settlement`, `Amenity` but also `CliComponent`)
    - Add user management and authentication
    - Allow saving and loading of games
    - Allow for an infinite world and store `Graph` in database so that all objects can be placed in the same world
      which is required for multiplayer
    - Polish a number of little things like using either only `appProperties.getEnvironment()` or `EnvironmentResolver`
      instead of both and creating `IoHandler` interface to handle all input/output (instead of `CliComponent`) and have
      implementations for web and CLI, etc.
- **Game design**:
    - Come up with a key objective for the player and an actual (i.e. fun) game loop
    - Introduce the possibility for the player to be attacked while travelling
    - Add more content esp. more quests
    - Implement player equipment, inventory, and item drops
- **Multiplayer**:
    - Allow multiple players to play in the same world and interact with each other
