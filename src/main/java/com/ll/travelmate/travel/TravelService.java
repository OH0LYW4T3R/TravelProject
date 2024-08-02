package com.ll.travelmate.travel;

import com.ll.travelmate.travelroute.TravelRoute;
import com.ll.travelmate.travelroute.TravelRouteDto;
import com.ll.travelmate.travelroute.TravelRouteService;
import com.ll.travelmate.user.TravelUser;
import com.ll.travelmate.user.TravelUserRepository;
import com.ll.travelmate.user.TravelUserService;
import com.ll.travelmate.util.ProductRefreshUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TravelService {
    private final TravelUserService travelUserService;
    private final TravelRouteService travelRouteService;
    private final TravelUserRepository travelUserRepository;
    private final TravelRepository travelRepository;

    @Transactional
    public TravelDto addTravel(Long travelUserId, TravelDto travelDto) {
        // 생성시 같이 가는 친구도 Travel을 만들어줘야함
        if (ProductRefreshUtil.startTimeCheck(travelDto.getTravelStartDate()))
            return null;
        
        Optional<TravelUser> travelUserOptional = travelUserRepository.findById(travelUserId);
        Travel travel = new Travel();

        travel.setTravelStartDate(travelDto.getTravelStartDate());
        travel.setTravelEndDate(travelDto.getTravelEndDate());
        travel.setLocation(travelDto.getLocation());
        travel.setLatitude(travelDto.getLatitude());
        travel.setLongitude(travelDto.getLongitude());
        travel.setTravelUser(travelUserOptional.get());
        if (travelDto.getMateUserDto() != null) {
            Optional<TravelUser> mateTravelUserOptional = travelUserRepository.findById(travelDto.getMateUserDto().getTravelUserId());
            mateTravelUserOptional.ifPresent(travel::setMateUser);
        }

        travelRepository.save(travel);

        for (TravelRouteDto travelRouteDto : travelDto.getTravelRoute()) {
            travel.getTravelRoute().add(travelRouteService.addTravelRoute(travel, travelRouteDto));
        }

        travelRepository.save(travel);

        return convertToDto(travel);
    }

    @Transactional
    public List<TravelDto> readAllTravel(Long travelUserId) {
        Optional<TravelUser> travelUserOptional = travelUserRepository.findById(travelUserId);
        List<Travel> travels = travelUserOptional.get().getTravels();
        List<TravelDto> travelDtos = new ArrayList<>();

        for (Travel travel : travels) {
            travelDtos.add(convertToDto(travel));
        }

        return travelDtos;
    }

    @Transactional
    public TravelDto readTravel(Long id) {
        Optional<Travel> travelOptional = travelRepository.findById(id);

        if (travelOptional.isEmpty())
            return null;

        Travel travel = travelOptional.get();

        return convertToDto(travel);
    }

    @Transactional
    public TravelDto updateTravel(Long travelUserId, TravelDto travelDto) {
        if(!Objects.equals(travelUserId, travelDto.getTravelUserId()))
            return null;

        Optional<Travel> travelOptional = travelRepository.findById(travelDto.getTravelId());

        if (travelOptional.isEmpty())
            return null;

        Travel travel = travelOptional.get();

        if (travelDto.getTravelStartDate() != null) {
            travel.setTravelStartDate(travelDto.getTravelStartDate());
        }
        if (travelDto.getTravelEndDate() != null) {
            travel.setTravelEndDate(travelDto.getTravelEndDate());
        }
        if (travelDto.getLocation() != null && !travelDto.getLocation().isEmpty()) {
            travel.setLocation(travelDto.getLocation());
        }
        if (travelDto.getLatitude() != null) {
            travel.setLatitude(travelDto.getLatitude());
        }
        if (travelDto.getLongitude() != null) {
            travel.setLongitude(travelDto.getLongitude());
        }

        // 새로운 TravelRoute 추가
        if (travelDto.getTravelRoute() != null) {
            for (TravelRoute travelRoute : travel.getTravelRoute()) {
                travelRouteService.deleteTravelRoute(travelRoute);
            }
            travel.setTravelRoute(new ArrayList<>());
            for (TravelRouteDto travelRouteDto : travelDto.getTravelRoute()) {
                travel.getTravelRoute().add(travelRouteService.addTravelRoute(travel, travelRouteDto));
            }
        }

        travelRepository.save(travel);

        return convertToDto(travel);
    }

    @Transactional
    public void deleteTravel(Long travelUserId, TravelDto travelDto) {
        if (!Objects.equals(travelUserId, travelDto.getTravelUserId()))
            return;
        Optional<Travel> travelOptional = travelRepository.findById(travelDto.getTravelId());

        if (travelOptional.isEmpty())
            return;

        for (TravelRoute travelRoute : travelOptional.get().getTravelRoute()) {
            travelRouteService.deleteTravelRoute(travelRoute);
        }

        travelRepository.delete(travelOptional.get());
    }

    public TravelDto convertToDto(Travel travel) {
        TravelDto travelDto = new TravelDto();
        travelDto.setTravelId(travel.getTravelId());
        travelDto.setTravelStartDate(travel.getTravelStartDate());
        travelDto.setTravelEndDate(travel.getTravelEndDate());
        travelDto.setLocation(travel.getLocation());
        travelDto.setLatitude(travel.getLatitude());
        travelDto.setLongitude(travel.getLongitude());
        travelDto.setTravelUserId(travel.getTravelUser().getTravelUserId());

        if (travel.getMateUser() != null) {
            travelDto.setMateUserDto(travelUserService.convertToDto(travel.getMateUser(), null));
        }


        List<TravelRouteDto> travelRouteDtos = new ArrayList<>();

        for (TravelRoute travelRoute : travel.getTravelRoute()) {
            travelRouteDtos.add(TravelRouteService.convertToDto(travelRoute));
        }
        travelDto.setTravelRoute(travelRouteDtos);

        return travelDto;
    }
}
