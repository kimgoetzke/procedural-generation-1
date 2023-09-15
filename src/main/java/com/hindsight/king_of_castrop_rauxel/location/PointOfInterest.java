package com.hindsight.king_of_castrop_rauxel.location;

import com.hindsight.king_of_castrop_rauxel.action.LocationAction;
import java.util.List;

import static com.hindsight.king_of_castrop_rauxel.location.AbstractAmenity.*;

public interface PointOfInterest {

  String getName();

  String getDescription();

  PoiType getType();

  List<LocationAction> getAvailableActions();

  String getSummary(); // TODO: Replace with objects so that it can be used via API
}
