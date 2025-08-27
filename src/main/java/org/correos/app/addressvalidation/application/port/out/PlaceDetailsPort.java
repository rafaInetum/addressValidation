package org.correos.app.addressvalidation.application.port.out;
import java.util.List;


public interface PlaceDetailsPort {
    List<String> getDetailedPlaces(List<String> placeIds);
}
