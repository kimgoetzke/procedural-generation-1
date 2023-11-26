package com.hindsight.king_of_castrop_rauxel.web.dto;

import com.hindsight.king_of_castrop_rauxel.action.Action;
import com.hindsight.king_of_castrop_rauxel.encounter.web.EncounterSummaryDto;
import java.util.List;

public record WebResponse(
    WebViewType viewType,
    List<ActionResponseDto> actions,
    EncounterSummaryDto encounterSummary,
    PlayerDto player) {

  public WebResponse(List<Action> actions, PlayerDto player) {
    this(WebViewType.DEFAULT, actions.stream().map(ActionResponseDto::from).toList(), null, player);
  }

  public WebResponse(List<Action> actions, EncounterSummaryDto encounterSummary, PlayerDto player) {
    this(
        WebViewType.ENCOUNTER_SUMMARY,
        actions.stream().map(ActionResponseDto::from).toList(),
        encounterSummary,
        player);
  }

  public enum WebViewType {
    START,
    DEFAULT,
    ENCOUNTER_SUMMARY
  }
}
