package com.hindsight.king_of_castrop_rauxel.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebRequest {

  @NotBlank(message = "playerId is a mandatory field")
  private String playerId;

  private int choice;
}
