# How to use the API

Endpoints require basic auth. Use `player1` / `password` or `player2` / `password`.

Please see [Procedural Generation 1 Frontend](https://github.com/kimgoetzke/procedural-generation-1-front-end) for a
sample web interface using this API.

## Limitations

1. Authentication has not been implemented yet. Currently, the game uses hardcoded credentials
   for `player1` & `player2`.
2. The API does not handle player deaths yet.
3. There are no API-specific acceptance/integration tests yet.

## Endpoints

### Start new game with `GET` `/api/play`

- Requires basic auth
- Returns a JSON `WebResponse` containing the `List<ActionResponseDto>` and `PlayerDto`
- Example request:

```
curl -u player1:password http://localhost:8080/api/play
```

### Resume active game with `GET` `/api/play/{playerId}`

- Requires basic auth
- Returns a JSON `WebResponse` containing the `viewType` to be rendered and the relevant DTO(s)
- Example request:

```
curl -u player1:password http://localhost:8080/api/play/PLA~PLAYER1@1277912753
```

### Play active game with `POST` `/api/play`

- Requires basic auth
- Returns a JSON `WebResponse` containing the `viewType` to be rendered and the relevant DTO(s)
- Requires JSON body containing the `playerId` and `choice`
- Example request:

```
curl -u player1:password -X POST http://localhost:8080/api/play -H "Content-Type: application/json" -d '{"playerId": "PLA~PLAYER1@1277912753", "choice": "1"}'
```

- Example body:

```json
{
  "playerId": "PLA~PLAYER1@1277912753",
  "choice": "1"
}
```

### Get active quests with `GET` `/api/play/{playerId}/quest-log`

- Requires basic auth
- Returns a JSON `List<QuestDto>`
- Example request:

```
curl -u player1:password http://localhost:8080/api/play/PLA~PLAYER1@1277912753/quest-log
```

- Example response:

```json
[
  {
    "about": "A dialogue",
    "eventType": "DIALOGUE",
    "eventState": "DECLINED",
    "questGiver": {
      "name": "Ashur-nasir-pal Hapi",
      "location": "Thyrvera",
      "poi": "Cathedral"
    },
    "questTarget": null
  }
]
```

### Example `WebResponse`

```json
{
  "viewType": "DEFAULT",
  "actions": [
    {
      "index": 1,
      "name": "Go to... (4 point(s) of interest)"
    },
    {
      "index": 2,
      "name": "Travel to Hylasoria (102 km south-east, unvisited) (Location)"
    },
    {
      "index": 3,
      "name": "Travel to Valthia (102 km south-west, unvisited) (Location)"
    },
    {
      "index": 4,
      "name": "Show debug menu"
    }
  ],
  "encounterSummary": null,
  "interactions": null,
  "player": {
    "id": "PLA~PLAYER1@1277912753",
    "name": "player1",
    "locationName": "Thyrvera",
    "poiName": "Thyrvera Field",
    "x": 12779,
    "y": 12753,
    "gold": 100,
    "minDamage": 1,
    "maxDamage": 4,
    "health": 100,
    "maxHealth": 100,
    "experience": 0,
    "level": 1,
    "previousState": "AT_POI",
    "currentState": "AT_POI"
  }
}
```

## Postman collection

This folder contains a Postman collection for the API. Import it into Postman to use it with `player1`.
