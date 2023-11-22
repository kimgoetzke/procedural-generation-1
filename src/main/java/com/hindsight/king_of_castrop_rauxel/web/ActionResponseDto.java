package com.hindsight.king_of_castrop_rauxel.web;

import com.hindsight.king_of_castrop_rauxel.action.Action;

public record ActionResponseDto(int index, String name) {

  public static ActionResponseDto from(Action action) {
    return new ActionResponseDto(action.getIndex(), action.getName());
  }
}
