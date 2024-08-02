package com.ll.travelmate.travelroute;

import com.ll.travelmate.travel.Travel;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TravelRouteService {
    private final TravelRouteRepository travelRouteRepository;
    @Transactional
    public TravelRoute addTravelRoute(Travel travel, TravelRouteDto travelRouteDto) {
        TravelRoute travelRoute = new TravelRoute();

        travelRoute.setTravel(travel);
        travelRoute.setVertex(travelRouteDto.getVertex());
        travelRoute.setLatitude(travelRouteDto.getLatitude());
        travelRoute.setLongitude(travelRouteDto.getLongitude());

        travelRouteRepository.save(travelRoute);

        return travelRoute;
    }

    @Transactional
    public void deleteTravelRoute(TravelRoute travelRoute) {
        Optional<TravelRoute> travelRouteOptional = travelRouteRepository.findById(travelRoute.getTravelRouteId());
        if (travelRouteOptional.isEmpty())
            return;
        travelRouteRepository.delete(travelRouteOptional.get());
    }

    public static TravelRouteDto convertToDto(TravelRoute travelRoute) {
        TravelRouteDto travelRouteDto = new TravelRouteDto();
        travelRouteDto.setTravelRouteId(travelRoute.getTravelRouteId());
        travelRouteDto.setTravelId(travelRoute.getTravel() != null ? travelRoute.getTravel().getTravelId() : null);
        travelRouteDto.setVertex(travelRoute.getVertex());
        travelRouteDto.setLatitude(travelRoute.getLatitude());
        travelRouteDto.setLongitude(travelRoute.getLongitude());

        return travelRouteDto;
    }
}
