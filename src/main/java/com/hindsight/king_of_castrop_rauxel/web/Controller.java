package com.hindsight.king_of_castrop_rauxel.web;

import com.hindsight.king_of_castrop_rauxel.web.dto.WebRequest;
import com.hindsight.king_of_castrop_rauxel.web.dto.WebResponse;
import com.hindsight.king_of_castrop_rauxel.web.exception.GenericWebException;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
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
  private final List<WebGame> activeGames = new ArrayList<>();

  @GetMapping("/api/play")
  public ResponseEntity<WebResponse> start(Authentication auth) {
    log.info("[GET /api/start] Start game for: {}", auth.getName());
    var webGame = ctx.getBean(WebGame.class);
    var res = webGame.startGame(auth.getName());
    activeGames.add(webGame);
    return ResponseEntity.ok(res);
  }

  @PostMapping("/api/play")
  public ResponseEntity<WebResponse> play(@Valid @RequestBody WebRequest req, Authentication auth) {
    log.info("[POST /api/play] Process choice '{}' for: {}", req.getChoice(), req.getPlayerId());
    var webGame = getGameOrThrow(req.getPlayerId(), auth);
    var res = webGame.processAction(req.getChoice());
    return ResponseEntity.ok(res);
  }

  private WebGame getGameOrThrow(String playerId, Authentication auth) {
    var user = auth.getName();
    var game =
        activeGames.stream()
            .filter(g -> g.getPlayer().getId().equals(playerId))
            .findFirst()
            .orElseThrow(userNotFound(playerId));
    if (!game.getPlayer().getName().equals(user)) {
      log.warn("User '{}' blocked from accessing game of player '{}'", user, playerId);
      throw new GenericWebException("Action not permitted", HttpStatus.FORBIDDEN);
    }
    return game;
  }

  @GetMapping("/login")
  public String login() {
    log.info("Login request received");
    return "Success";
  }

  /** User is authenticated but playerId not found. */
  private Supplier<GenericWebException> userNotFound(String playerId) {
    return () -> new GenericWebException("Player '%s' does not exist".formatted(playerId));
  }
}
