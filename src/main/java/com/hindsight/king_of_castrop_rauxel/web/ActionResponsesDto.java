package com.hindsight.king_of_castrop_rauxel.web;

import com.hindsight.king_of_castrop_rauxel.action.Action;

import java.util.List;

public record ActionResponsesDto(List<ActionResponseDto> actions) {

  public static ActionResponsesDto from(List<Action> actions) {
    return new ActionResponsesDto(actions.stream().map(ActionResponseDto::from).toList());
  }
}
