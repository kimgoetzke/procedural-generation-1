package com.hindsight.king_of_castrop_rauxel.utils;

import com.hindsight.king_of_castrop_rauxel.event.DefeatEventDetails;
import com.hindsight.king_of_castrop_rauxel.event.Dialogue;
import com.hindsight.king_of_castrop_rauxel.event.EventDetails;
import com.hindsight.king_of_castrop_rauxel.event.Role;
import java.util.List;
import java.util.Map;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {

  EventDetails eventDetails;
  DefeatEventDetails defeatDetails;
  Map<Role, List<Dialogue>> participantData;
}
