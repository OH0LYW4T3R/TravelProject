package com.ll.travelmate.guidereview;

import com.ll.travelmate.cart.Cart;
import com.ll.travelmate.guide.Guide;
import com.ll.travelmate.guide.GuideRepository;
import com.ll.travelmate.guide.GuideService;
import com.ll.travelmate.guideproposal.GuideProposal;
import com.ll.travelmate.guideproposal.GuideProposalDto;
import com.ll.travelmate.guideproposal.GuideProposalRepository;
import com.ll.travelmate.guideproposal.PurchaseStatus;
import com.ll.travelmate.user.TravelUser;
import com.ll.travelmate.user.TravelUserRepository;
import com.ll.travelmate.user.TravelUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GuideReviewService {
    private final TravelUserRepository travelUserRepository;
    private final GuideProposalRepository guideProposalRepository;
    private final GuideReviewRepository guideReviewRepository;
    private final GuideRepository guideRepository;
    private final TravelUserService travelUserService;

    @Transactional
    public GuideReviewDto addReview(Long travelUserId, ReviewRequestDto reviewRequestDto) {
        GuideReviewDto guideReviewDto = reviewRequestDto.getGuideReviewDto();
        GuideProposalDto guideProposalDto = reviewRequestDto.getGuideProposalDto();
        Optional<TravelUser> travelUserOptional = travelUserRepository.findById(travelUserId);
        Cart cart = travelUserOptional.get().getCart();
        Optional<GuideProposal> guideProposalOptional = guideProposalRepository.findById(guideProposalDto.getGuideProposalId());

        if (guideProposalOptional.isEmpty())
            return null; // 없는 상품에 리뷰를 쓰려함

        Optional<GuideReview> guideReviewOptional = guideReviewRepository.findByGuideProposalIdAndTravelUser(guideProposalDto.getGuideProposalId(), travelUserOptional.get());
        if (guideReviewOptional.isPresent())
            return null; // 이미 리뷰를 쓴 상품

        if (!Objects.equals(guideProposalOptional.get().getCart().getCartId(), cart.getCartId()))
            return null; // 내상품이 아닌것에 리뷰를 쓰려함

        if (guideProposalOptional.get().getPurchaseStatus() != PurchaseStatus.travelCompleted)
            return null; // 여행 끝난게 아닌데 쓰려고 하면

        GuideReview guideReview = new GuideReview();
        guideReview.setGuide(guideProposalOptional.get().getGuide());
        guideReview.setGuideProposalId(guideProposalDto.getGuideProposalId());
        guideReview.setRating(guideReviewDto.getRating());
        guideReview.setComment(guideReviewDto.getComment());
        guideReview.setTravelUser(travelUserOptional.get());
        guideReviewRepository.save(guideReview);

        Guide guide = guideProposalOptional.get().getGuide();
        Double totalValue = guide.getTotalValue();
        Integer reviewCounter = guide.getReviewCounter();
        guide.setRating(calcRating(totalValue, guideReviewDto.getRating(), reviewCounter + 1));
        guide.setTotalValue(totalValue + guideReviewDto.getRating());
        guide.setReviewCounter(reviewCounter + 1);
        guideRepository.save(guide);

        return convertToDto(guideReview);
    }

    @Transactional
    public List<GuideReviewDto> readReview(Long travelUserId) {
        Optional<TravelUser> travelUserOptional = travelUserRepository.findById(travelUserId);
        List<GuideReview> guideReviews = guideReviewRepository.findByTravelUser(travelUserOptional.get());
        List<GuideReviewDto> guideReviewDtos = new ArrayList<>();

        for (GuideReview guideReview : guideReviews) {
            guideReviewDtos.add(convertToDtoRead(guideReview));
        }

        return guideReviewDtos;
    }

    @Transactional
    public GuideReviewDto updateReview(Long travelUserId, GuideReviewDto guideReviewDto) {
        Optional<TravelUser> travelUserOptional = travelUserRepository.findById(travelUserId);
        Optional<GuideReview> guideReviewOptional = guideReviewRepository.findByGuideReviewIdAndTravelUser(guideReviewDto.getGuideReviewId(), travelUserOptional.get());

        if (guideReviewOptional.isEmpty())
            return null; //잘못된 접근

        GuideReview guideReview = guideReviewOptional.get();
        Double rating = guideReview.getRating();
        Guide guide = guideReview.getGuide();
        Double totalValue = guide.getTotalValue();
        guide.setTotalValue(totalValue - rating);
        System.out.println(guide.getTotalValue());

        if (guideReviewDto.getComment() != null)
            guideReview.setComment(guideReviewDto.getComment());
        if (guideReviewDto.getRating() != null) {
            guide.setRating(calcRating(guide.getTotalValue(), guideReviewDto.getRating(), guide.getReviewCounter()));
            Double changeTotalValue = guide.getTotalValue();
            guide.setTotalValue(changeTotalValue + guideReviewDto.getRating());
            System.out.println(guide.getTotalValue());
            guideReview.setRating(guideReviewDto.getRating());
            guideRepository.save(guide);
        }

        guideReviewRepository.save(guideReview);

        return convertToDto(guideReview);
    }

    @Transactional
    public void deleteReview(Long travelUserId, GuideReviewDto guideReviewDto) {
        Optional<TravelUser> travelUserOptional = travelUserRepository.findById(travelUserId);
        Optional<GuideReview> guideReviewOptional = guideReviewRepository.findByGuideReviewIdAndTravelUser(guideReviewDto.getGuideReviewId(), travelUserOptional.get());

        if (guideReviewOptional.isEmpty())
            return;

        GuideReview guideReview = guideReviewOptional.get();
        Double rating = guideReview.getRating();
        Guide guide = guideReview.getGuide();
        guide.setTotalValue(guide.getTotalValue() - rating);
        guide.setReviewCounter(guide.getReviewCounter() - 1);
        guide.setRating(guide.getTotalValue() / guide.getReviewCounter());
        guideRepository.save(guide);

        guideReviewRepository.delete(guideReview);
    }

    public GuideReviewDto convertToDto(GuideReview guideReview) {
        GuideReviewDto guideReviewDto = new GuideReviewDto();

        guideReviewDto.setGuideReviewId(guideReview.getGuideReviewId());
        guideReviewDto.setGuideProposalId(guideReview.getGuideProposalId());
        guideReviewDto.setComment(guideReview.getComment());
        guideReviewDto.setRating(guideReview.getRating());
        guideReviewDto.setCreatedAt(guideReview.getCreatedAt());
        guideReviewDto.setGuide(GuideService.convertToDto(guideReview.getGuide()));
        guideReviewDto.setWriterTravelUser(travelUserService.convertToDto(guideReview.getTravelUser(), null));

        return guideReviewDto;
    }

    public GuideReviewDto convertToDtoReview(GuideReview guideReview) {
        GuideReviewDto guideReviewDto = new GuideReviewDto();

        guideReviewDto.setGuideReviewId(guideReview.getGuideReviewId());
        guideReviewDto.setGuideProposalId(guideReview.getGuideProposalId());
        guideReviewDto.setComment(guideReview.getComment());
        guideReviewDto.setRating(guideReview.getRating());
        guideReviewDto.setCreatedAt(guideReview.getCreatedAt());
        guideReviewDto.setWriterTravelUser(travelUserService.convertToDto(guideReview.getTravelUser(), null));

        return guideReviewDto;
    }

    public GuideReviewDto convertToDtoRead(GuideReview guideReview) {
        GuideReviewDto guideReviewDto = new GuideReviewDto();

        guideReviewDto.setGuideReviewId(guideReview.getGuideReviewId());
        guideReviewDto.setGuideProposalId(guideReview.getGuideProposalId());
        guideReviewDto.setComment(guideReview.getComment());
        guideReviewDto.setRating(guideReview.getRating());
        guideReviewDto.setCreatedAt(guideReview.getCreatedAt());
        guideReviewDto.setGuide(GuideService.convertToDto(guideReview.getGuide()));

        return guideReviewDto;
    }

     public Double calcRating(Double defaultRating, Double rating, Integer counter) {
        return (defaultRating + rating) / (counter);
    }
}
