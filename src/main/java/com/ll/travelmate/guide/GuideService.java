package com.ll.travelmate.guide;

import com.ll.travelmate.user.TravelUser;
import com.ll.travelmate.user.TravelUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GuideService {
    private final TravelUserRepository travelUserRepository;
    private final GuideRepository guideRepository;

    @Transactional
    public GuideDto readGuide(Long travelUserId) {
        Optional<TravelUser> travelUserOptional = travelUserRepository.findById(travelUserId);
        Guide guide = travelUserOptional.get().getGuide();

        return convertToDto(guide);
    }

    public GuideDto convertToDto(Guide guide) {
        GuideDto dto = new GuideDto();

        dto.setGuideId(guide.getGuideId());
        dto.setRating(guide.getRating());
        dto.setName(guide.getName());
        dto.setGender(guide.getGender());
        dto.setAge(guide.getAge());
        dto.setArea(guide.getArea());

        return dto;
    }
}
