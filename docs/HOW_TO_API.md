# How to use the API

Endpoints require basic auth. Use `player1` / `password` or `player2` / `password`.

## Endpoints

### `GET` `/api/play`

- Requires basic auth
- Returns a JSON `WebResponse` containing the `List<ActionResponseDto>` and `PlayerDto`
- Example request:

```
curl -u player1:password http://localhost:8080/api/play
```

### `POST` `/api/play`

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

## Postman collection

This folder contains a Postman collection for the API. Import it into Postman to use it with `player1`.