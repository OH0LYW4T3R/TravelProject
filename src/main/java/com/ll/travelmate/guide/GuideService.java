package com.ll.travelmate.guide;

import com.ll.travelmate.guidereview.GuideReview;
import com.ll.travelmate.guidereview.GuideReviewDto;
import com.ll.travelmate.guidereview.GuideReviewService;
import com.ll.travelmate.user.TravelUser;
import com.ll.travelmate.user.TravelUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GuideService {
    private final TravelUserRepository travelUserRepository;
    private final GuideRepository guideRepository;
    private final GuideReviewService guideReviewService;

    @Transactional
    public GuideDto readGuide(Long travelUserId) {
        Optional<TravelUser> travelUserOptional = travelUserRepository.findById(travelUserId);
        Guide guide = travelUserOptional.get().getGuide();

        return convertToDto(guide);
    }

    @Transactional
    public GuideDto readOtherGuide(Long id) {
        Optional<Guide> guide = guideRepository.findById(id);

        return guide.map(GuideService::convertToDto).orElse(null);

    }

    @Transactional
    public List<GuideReviewDto> readMyReview(Long travelUserId) {
        Optional<TravelUser> travelUserOptional = travelUserRepository.findById(travelUserId);
        Guide guide = travelUserOptional.get().getGuide();
        List<GuideReview> guideReviews = guide.getGuideReviews();
        List<GuideReviewDto> guideReviewDtos = new ArrayList<>();

        for (GuideReview guideReview : guideReviews) {
            guideReviewDtos.add(guideReviewService.convertToDtoReview(guideReview));
        }

        return guideReviewDtos;
    }

    @Transactional
    public List<GuideReviewDto> readOtherReview(Long id) {
        Optional<Guide> guide = guideRepository.findById(id);

        if (guide.isEmpty())
            return null;

        List<GuideReview> guideReviews = guide.get().getGuideReviews();
        List<GuideReviewDto> guideReviewDtos = new ArrayList<>();

        for (GuideReview guideReview : guideReviews) {
            guideReviewDtos.add(guideReviewService.convertToDtoReview(guideReview));
        }

        return guideReviewDtos;
    }

    public static GuideDto convertToDto(Guide guide) {
        GuideDto dto = new GuideDto();

        dto.setGuideId(guide.getGuideId());
        dto.setRating(guide.getRating());
        dto.setName(guide.getName());
        dto.setGender(guide.getGender());
        dto.setAge(guide.getAge());
        dto.setArea(guide.getArea());
        dto.setTotalValue(guide.getTotalValue());
        dto.setReviewCounter(guide.getReviewCounter());

        return dto;
    }
}
