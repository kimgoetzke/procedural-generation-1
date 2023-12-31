package com.hindsight.king_of_castrop_rauxel.web;

import com.hindsight.king_of_castrop_rauxel.web.dto.QuestDto;
import com.hindsight.king_of_castrop_rauxel.web.dto.WebRequest;
import com.hindsight.king_of_castrop_rauxel.web.dto.WebResponse;
import com.hindsight.king_of_castrop_rauxel.web.exception.GenericWebException;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class Controller {

  private final ApplicationContext ctx;
  private final PlayerRepository playerRepository;
  private final List<WebGame> activeGames = new ArrayList<>();

  @GetMapping("/api/play")
  public ResponseEntity<WebResponse> start(Authentication auth) {
    log.info("GET /api/play >> Start game for: {}", auth.getName());
    throwIfAlreadyActive(auth);
    var webGame = ctx.getBean(WebGame.class);
    var res = webGame.startGame(auth.getName());
    activeGames.add(webGame);
    return ResponseEntity.ok(res);
  }

  @GetMapping("/api/play/{playerId}")
  public ResponseEntity<WebResponse> resume(@PathVariable String playerId, Authentication auth) {
    log.info("GET /api/play/{} >> Resume game for: {}", playerId, auth.getName());
    var player = playerRepository.findById(playerId).orElseThrow(() -> playerNotFound(playerId));
    var activeGame = getGame(player.getId(), auth);
    if (activeGame != null) {
      return ResponseEntity.ok(activeGame.getCurrentGame());
    }
    var webGame = ctx.getBean(WebGame.class);
    var res = webGame.resumeGame(player);
    activeGames.add(webGame);
    return ResponseEntity.ok(res);
  }

  @PostMapping("/api/play")
  public ResponseEntity<WebResponse> play(@Valid @RequestBody WebRequest req, Authentication auth) {
    log.info("POST /api/play >> Process choice '{}' for: {}", req.getChoice(), req.getPlayerId());
    var webGame = getGameOrThrow(req.getPlayerId(), auth);
    var res = webGame.playGame(req.getChoice());
    if (res.viewType() == WebResponse.WebViewType.GAME_OVER) {
      var player = webGame.getPlayer();
      log.info("Game over for '{}' - removing active game and player", player.getName());
      playerRepository.deleteById(player.getId());
      activeGames.remove(webGame);
    }
    return ResponseEntity.ok(res);
  }

  @GetMapping("/api/play/{playerId}/quest-log")
  public ResponseEntity<List<QuestDto>> play(@PathVariable String playerId, Authentication auth) {
    log.info("GET /api/play/quest-log >> Get quest log for: {}", playerId);
    var webGame = getGameOrThrow(playerId, auth);
    var res = webGame.getQuests();
    return ResponseEntity.ok(res);
  }

  private WebGame getGameOrThrow(String playerId, Authentication auth) {
    var game = getGame(playerId, auth);
    throwIfGameNotFound(playerId, game);
    return game;
  }

  private WebGame getGame(String playerId, Authentication auth) {
    var game = activeGames.stream().filter(g -> g.getPlayer().getId().equals(playerId)).findFirst();
    game.ifPresent(webGame -> throwIfForbiddenAccess(playerId, webGame, auth.getName()));
    return game.orElse(null);
  }

  private GenericWebException playerNotFound(String playerId) {
    return new GenericWebException(
        "Player '%s' does not exist".formatted(playerId), HttpStatus.NOT_FOUND);
  }

  private void throwIfAlreadyActive(Authentication auth) {
    if (playerRepository.findByName(auth.getName()) != null) {
      throw new GenericWebException(
          "User '%s' already has an active game".formatted(auth.getName()), HttpStatus.CONFLICT);
    }
  }

  private static void throwIfGameNotFound(String playerId, WebGame game) {
    if (game == null) {
      throw new GenericWebException(
          "Player '%s' has no active game".formatted(playerId), HttpStatus.NOT_FOUND);
    }
  }

  private static void throwIfForbiddenAccess(String playerId, WebGame game, String user) {
    if (!game.getPlayer().getName().equals(user)) {
      log.warn("User '{}' blocked from accessing game of player '{}'", user, playerId);
      throw new GenericWebException("Action not permitted", HttpStatus.FORBIDDEN);
    }
  }
}
