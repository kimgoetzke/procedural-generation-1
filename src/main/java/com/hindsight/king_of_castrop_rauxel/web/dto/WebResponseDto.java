package com.hindsight.king_of_castrop_rauxel.web.dto;

import com.hindsight.king_of_castrop_rauxel.encounter.web.EncounterSummaryDto;

public record WebResponseDto(
    WebViewType viewType,
    ActionResponsesDto actions,
    EncounterSummaryDto encounterSummary,
    PlayerDto player) {

  public WebResponseDto(PlayerDto player) {
    this(WebViewType.START, null, null, player);
  }

  public WebResponseDto(ActionResponsesDto actions) {
    this(WebViewType.DEFAULT, actions, null, null);
  }

  public WebResponseDto(ActionResponsesDto actions, PlayerDto player) {
    this(WebViewType.DEFAULT, actions, null, player);
  }

  public WebResponseDto(
      ActionResponsesDto actions, EncounterSummaryDto encounterSummary, PlayerDto player) {
    this(WebViewType.ENCOUNTER_SUMMARY, actions, encounterSummary, player);
  }

  public enum WebViewType {
    START,
    DEFAULT,
    ENCOUNTER_SUMMARY
  }
}
