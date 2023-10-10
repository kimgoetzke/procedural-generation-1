package com.hindsight.king_of_castrop_rauxel.utils;

import com.hindsight.king_of_castrop_rauxel.event.Dialogue;
import com.hindsight.king_of_castrop_rauxel.event.EventDetails;
import com.hindsight.king_of_castrop_rauxel.event.Role;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public class EventDto {

  EventDetails eventDetails;
  Map<Role, List<Dialogue>> participantData;
}
