package com.hindsight.king_of_castrop_rauxel.web.dto;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.encounter.web.EncounterSummaryDto;
import java.util.List;

public record WebResponse(
    WebViewType viewType,
    List<ActionResponseDto> actions,
    EncounterSummaryDto encounterSummary,
    List<String> interactions,
    PlayerDto player) {

  public WebResponse(List<Action> actions, PlayerDto player) {
    this(
        WebViewType.DEFAULT,
        actions.stream().map(ActionResponseDto::from).toList(),
        null,
        null,
        player);
  }

  public WebResponse(
      List<Action> actions,
      EncounterSummaryDto encounterSummary,
      PlayerDto player,
      WebViewType viewType) {
    this(
        viewType,
        actions.stream().map(ActionResponseDto::from).toList(),
        encounterSummary,
        null,
        player);
  }

  public WebResponse(List<Action> actions, List<String> interactions, PlayerDto player) {
    this(
        WebViewType.DIALOGUE,
        actions.stream().map(ActionResponseDto::from).toList(),
        null,
        interactions,
        player);
  }

  public enum WebViewType {
    DEFAULT,
    ENCOUNTER_SUMMARY,
    DIALOGUE,
    GAME_OVER
  }
}
