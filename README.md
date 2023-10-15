# Procedural Generation Project 1

This project was my first attempt to procedurally generate, well, anything really. I didn't know anything about this topic but  ended up with an old-school text-based adventure game world where the player can travel between locations, interact with non-player characters, and engage in combat.

### Features
#### Procedural generation

- All objects below are generated procedurally, using a seeded `Random` object and handled by a `WorldHandler` instance
- The core object is `World` which holds `Chunk[][]`, with the player starting in the centre chunk
- Each `Chunk` holds an `int[][]`:
  - Based on `int density`, a number of `Location`s are placed in the `Chunk`
  - `Location`s are connected using various, configurable strategies which result in the `WorldHandler`s `Graph<Location>`
- The only `Location` implemented at this stage is called `Settlement`:
  - The most important feature of a `Location` is its `Size` which determines the`PointOfInterest` count and, for a `Settlement`, also the `Inhabitant` count
  - Each `Location` holds a `List<PointOfInterest>` which the player can visit
- The key object players interact with/within are `PointOfInterest` (**POI**):
  - Currently implemented are the following `Type`s: `ENTRANCE`, `MAIN_SQUARE`, `SHOP`, `QUEST_LOCATION` and `DUNGEON`
  - A POIs features are determined by its `Type` 
  - At a POI, the player can engage in dialogues with non-player characters (**NPC**) other events (e.g. "delivery" or "kill" quests), engage in combat, or take other actions
- Each object of each layer (i.e. `World`, `Chunk`, `Location` and `PointOfInterest`) can be located using `Coordinate`
- The web of connections and the distance between each (both of which stored in `Graph<Location>`) play an important role including the increasing difficulty of the events/danger of travelling long distances

#### Player loop

- When playing the game via a CLI, the `Player`'s `State` determines the player loop that is being executed
- Each state (e.g. `PoiLoop` or `CombatLoop`) inherits from `AbstractLoop`
- A loop follows a simple sequence such as this example from the `DialogueLoop`:
```java
public class DialogueLoop extends AbstractLoop {
  // ...
  @Override
  public void execute(List<Action> actions) {
    printInteraction(); // Shows the current `Interaction` from the NPCs `Dialogue` 
    prepareActions(actions); // Reads the available `Action`s for current `Dialogue` from `Event`
    printActions(actions); // Shows the above as a `List<Action>`
    takeAction(actions); // Executes the selected `Action`
    postProcess(); // Handles side effects of the outcome e.g. updating the `Event`
  }
  // ...
}
```
- Almost every loop will `printActions()` and allow the player to `takeAction()` 
- The `Action` interface is the way through which the `Player` affects the world - examples:
  - Move to a different `Location` using `LocationAction` 
  - Move to a different `PointOfInterest` using `PoiAction`
  - Start with an `EventAction`
  - Changing the `Player`s `State` using `StateAction`

#### Other technical features

- Folder scanning (`FolderReader`) to read available event file names by category (e.g. events) which works inside a JAR and when running via an IDE
- Processing of Yaml files (`YamlReader`) which allows customising event `Participant`s based on their role (e.g. `eventGiver` or `eventTarget`), `List<Dialogue>` for each based on event status, `List<Reward>`, etc.
- Processing of Txt files (`TxtReader`) which is used to generate `Location`, `PointOfInterest` and `Npc` names
- Processing of `String` placeholders in Yaml or Txt files (`PlaceholderProcessor`) e.g. `&L` for location name or `&TOF` for the target owner's first name through which events are tailored to the current places and characters involved

### Technologies used

- Core: Java 19, Spring Boot 3 + Lombok, Gradle
- `guava` for String manipulation
- `snakeyaml` for Yaml processing
- `google-java-format` for formatting

### Possible next features to implement

- **(Interface)**: Implement Restful API and a web interface as alternative for CLI-based player loop 
- **(Procedural generation)**: Implement biomes which: 
      - Determine difficulty of events, attitude towards player, etc. based on environmental factors
      - Determine object characteristics such as names and types of events
- **(Game design)**: Come up with a key objective for the player and an actual (i.e. fun) game loop
- **(Game design)**: Create more `Location` types such as `Castle`, `Dungeon` or `Cave`
- **(Game design)**: Create amenities (`PointOfInterest`) with specific functions i.e. shops
- **(Game design)**: Implement player equipment, inventory, and item drops
- **(Game design)**: Implement a trade/currency system and the ability to buy/sell equipment

### Notes

This project uses `google-java-format`. See https://github.com/google/google-java-format for more details on how to set it up and use it.

