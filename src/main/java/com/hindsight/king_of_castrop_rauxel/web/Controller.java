package com.hindsight.king_of_castrop_rauxel.web;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.hindsight.king_of_castrop_rauxel.web.dto.ActionRequestDto;
import com.hindsight.king_of_castrop_rauxel.web.dto.ActionResponsesDto;
import com.hindsight.king_of_castrop_rauxel.web.dto.PlayerDto;
import com.hindsight.king_of_castrop_rauxel.web.dto.WebResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
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

  @GetMapping("/api/start")
  public ResponseEntity<PlayerDto> start(Authentication authentication) {
    log.info("Request received for: {}", authentication.getPrincipal());
    var webGame = ctx.getBean(WebGame.class);
    var player = webGame.getPlayer(authentication.getName());
    activeGames.add(webGame);
    return ResponseEntity.ok(player);
  }

  @GetMapping("/api/play/{playerId}")
  public ResponseEntity<ActionResponsesDto> play(
      @PathVariable String playerId, Authentication authentication) {
    log.info("Request received for '{}' to get initial actions", playerId);
    var webGame = getGameOrThrow(playerId, authentication);
    var actions = webGame.getInitialActions();
    return ResponseEntity.ok(actions);
  }

  @PostMapping("/api/play")
  public ResponseEntity<WebResponseDto> play(
      @Valid @RequestBody ActionRequestDto actionRequest, Authentication authentication) {
    log.info(
        "Request received for '{}' to process choice '{}'",
        actionRequest.getPlayerId(),
        actionRequest.getChoice());
    var webGame = getGameOrThrow(actionRequest.getPlayerId(), authentication);
    var actions = webGame.processAction(actionRequest.getChoice());
    return ResponseEntity.ok(actions);
  }

  private WebGame getGameOrThrow(String playerId, Authentication authentication) {
    var user = authentication.getName();
    var game =
        activeGames.stream()
            .filter(g -> g.getPlayer().getId().equals(playerId))
            .findFirst()
            .orElseThrow(userNotFound(playerId));
    if (!game.getPlayer().getName().equals(user)) {
      log.info("User '{}' blocked from accessing game of player '{}'", user, playerId);
      throw new IllegalStateException("This action is not permitted");
    }
    return game;
  }

  @GetMapping("/login")
  public String login() {
    log.info("Login request received");
    return "Success";
  }

  private Supplier<IllegalArgumentException> userNotFound(String playerId) {
    log.info("User authenticated but player '{}' not found", playerId);
    return () -> new IllegalArgumentException("Player '%s' does not exist".formatted(playerId));
  }
}
