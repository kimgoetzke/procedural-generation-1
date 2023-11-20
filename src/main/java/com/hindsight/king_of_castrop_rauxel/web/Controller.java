package com.hindsight.king_of_castrop_rauxel.web;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class Controller {

  private final WebGame webGame;

  @GetMapping("/api/start")
  public ResponseEntity<PlayerDto> start(Authentication authentication) {
    log.info("Request received for: {}", authentication.getPrincipal());
    var player = webGame.getPlayer(authentication.getName());
    return ResponseEntity.ok(player);
  }

  @GetMapping("/api/play/{playerId}")
  public ResponseEntity<List<Action>> play(@PathVariable String playerId) {
    log.info("Request received for: {}", playerId);
    var actions = webGame.getInitialActions(playerId);
    return ResponseEntity.ok(actions);
  }

  @GetMapping("/login")
  public String login() {
    log.info("Request received");
    return "Success";
  }
}
