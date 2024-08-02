package com.ll.travelmate.guideproposal;

import com.ll.travelmate.guide.Guide;
import com.ll.travelmate.guide.GuideDto;
import com.ll.travelmate.guide.GuideRepository;
import com.ll.travelmate.guide.GuideService;
import com.ll.travelmate.user.TravelUser;
import com.ll.travelmate.user.TravelUserRepository;
import com.ll.travelmate.user.TravelUserService;
import com.ll.travelmate.util.ProductRefreshUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/*
* 이 두상태 일때 (registration, waitingForDecision) 시작일 넘으면 삭제 // 테스트 완
* 구매를 3일동안 하지 않으면 삭제 // 테스트 완
* 취소된 상품은 1일뒤 다시 재등록 : 등록 상태 // 테스트 완
* 구매된 상태 (purchaseCompleted)는 만기일을 넘으면 travelCompleted 변환 // 테스트 완
* 여행완료 상태는 (travelCompleted) 30일까지만 보관 (그동안에 여행일기라던지, 리뷰를 써야함)
* */


@Service
@RequiredArgsConstructor
public class GuideProposalService {
    private final TravelUserRepository travelUserRepository;
    private final TravelUserService travelUserService;
    private final GuideRepository guideRepository;
    private final GuideProposalRepository guideProposalRepository;
    private final GuideService guideService;

    @Transactional
    public GuideProposalDto createGuideProposal(GuideProposalDto guideProposalDto, Long travelUserId) {
        GuideProposal guideProposal = new GuideProposal();
        Optional<TravelUser> travelUserOptional = travelUserRepository.findById(travelUserId);
        Optional<Guide> guideOptional = guideRepository.findByTravelUser(travelUserOptional.get());

        guideProposal.setGuide(guideOptional.get());
        guideProposal.setPrice(guideProposalDto.getPrice());
        guideProposal.setArea(guideProposalDto.getArea());
        guideProposal.setGoal(guideProposalDto.getGoal());
        guideProposal.setSchedule(guideProposalDto.getSchedule());
        guideProposal.setTravelStartDate(guideProposalDto.getTravelStartDate());
        guideProposal.setTravelEndDate(guideProposalDto.getTravelEndDate());
        guideProposal.setPurchaseStatus(PurchaseStatus.registration);

        guideProposalRepository.save(guideProposal);

        return convertToDto(guideProposal, guideProposal.getGuide());
    }

    // 여기서 부터 여행 완료나 취소시 재등록
    @Transactional
    public List<GuideProposalDto> readAllGuideProposal(Long travelUserId) {
        List<GuideProposalDto> guideProposalDtos = new ArrayList<>();
        Optional<TravelUser> travelUserOptional = travelUserRepository.findById(travelUserId);
        Optional<Guide> guideOptional = guideRepository.findByTravelUser(travelUserOptional.get());

        for (GuideProposal guideProposal : guideOptional.get().getGuideProposals()) {
            cancelToRegistrationCheck(guideProposal);
            paymentToRegistrationCheck(guideProposal);
            purchaseToTravelCompleteCheck(guideProposal);

            boolean bool1 = travelCompleteCheck(guideProposal);
            boolean bool2 = startTimeCheck(guideProposal);

            if (bool1 && bool2)
                guideProposalDtos.add(convertToDto(guideProposal, null));
        }

        return guideProposalDtos;
    }

    @Transactional
    public List<GuideProposalDto> readRecommendGuideProposal(Long travelUserId, String area) {
        List<GuideProposalDto> guideProposalDtos = new ArrayList<>();
        List<GuideProposal> guideProposals = guideProposalRepository.findByAreaContaining(area);

        for (GuideProposal guideProposal : guideProposals) {
            cancelToRegistrationCheck(guideProposal);
            paymentToRegistrationCheck(guideProposal);
            purchaseToTravelCompleteCheck(guideProposal);

            boolean bool1 = travelCompleteCheck(guideProposal);
            boolean bool2 = startTimeCheck(guideProposal);

            System.out.println(bool1);
            System.out.println(bool2);

            if (!bool2)
                continue;

            if (Objects.equals(guideProposal.getGuide().getTravelUser().getTravelUserId(), travelUserId) ||
                    guideProposal.getPurchaseStatus() != PurchaseStatus.registration
            )
                continue;

            guideProposalDtos.add(convertToDto(guideProposal, guideProposal.getGuide()));
        }

        return guideProposalDtos;
    }

    @Transactional
    public List<GuideProposalDto> readPurchaseStatusGuideProposal(Long travelUserId, PurchaseStatus purchaseStatus) {
        List<GuideProposalDto> guideProposalDtos = new ArrayList<>();
        Optional<TravelUser> travelUserOptional = travelUserRepository.findById(travelUserId);
        Optional<Guide> guideOptional = guideRepository.findByTravelUser(travelUserOptional.get());
        List<GuideProposal> guideProposals = guideProposalRepository.findByGuideAndPurchaseStatus(guideOptional.get(), purchaseStatus);

        for (GuideProposal guideProposal : guideProposals) {
            cancelToRegistrationCheck(guideProposal);
            paymentToRegistrationCheck(guideProposal);
            purchaseToTravelCompleteCheck(guideProposal);
            boolean bool1 = travelCompleteCheck(guideProposal);
            boolean bool2 = startTimeCheck(guideProposal);

            if (bool1 && bool2)
                guideProposalDtos.add(convertToDto(guideProposal, null));
        }

        return guideProposalDtos;
    }

    @Transactional
    public GuideProposalDto readGuideProposal(Long id) {
        Optional<GuideProposal> guideProposalOptional = guideProposalRepository.findById(id);
        return guideProposalOptional.map(guideProposal -> convertToDto(guideProposal, guideProposal.getGuide())).orElse(null);
    }

    // 여기까지

    @Transactional
    public GuideProposalDto updateGuideProposal(Long travelUserId, GuideProposalDto guideProposalDto) {
        if (guideProposalDto.getGuideProposalId() == null)
            return null;

        Optional<TravelUser> travelUserOptional = travelUserRepository.findById(travelUserId);
        Optional<Guide> guideOptional = guideRepository.findByTravelUser(travelUserOptional.get());
        Optional<GuideProposal> guideProposalOptional = guideProposalRepository.findByGuideAndGuideProposalId(guideOptional.get(), guideProposalDto.getGuideProposalId());

        if (guideProposalOptional.isEmpty())
            return null;

        if (guideProposalOptional.get().getPurchaseStatus() != PurchaseStatus.registration)
            return null; // 등록된 상태에서만 수정 가능

        GuideProposal guideProposal = guideProposalOptional.get();

        if(guideProposalDto.getArea() != null)
            guideProposal.setArea(guideProposalDto.getArea());
        if (guideProposalDto.getGoal() != null)
            guideProposal.setGoal(guideProposalDto.getGoal());
        if (guideProposalDto.getSchedule() != null)
            guideProposal.setSchedule(guideProposalDto.getSchedule());
        if (guideProposalDto.getPrice() != null)
            guideProposal.setPrice(guideProposalDto.getPrice());
        if (guideProposalDto.getTravelStartDate() != null)
            guideProposal.setTravelStartDate(guideProposalDto.getTravelStartDate());
        if (guideProposalDto.getTravelEndDate() != null)
            guideProposal.setTravelEndDate(guideProposalDto.getTravelEndDate());

        guideProposalRepository.save(guideProposal);

        return convertToDto(guideProposal, guideOptional.get());
    }

    @Transactional
    public GuideProposalDto acceptGuideProposal(Long travelUserId, GuideProposalDto guideProposalDto) {
        Optional<GuideProposal> guideProposalOptional = guideProposalRepository.findById(guideProposalDto.getGuideProposalId());
        GuideProposal guideProposal = guideProposalOptional.get();

        if (guideProposal.getCart() == null)
            return null; // 이미 삭제된 계획임.

        if (guideProposal.getPurchaseStatus() != PurchaseStatus.waitingForDecision)
            return null; // 잘못된 상태

        guideProposal.setPurchaseStatus(PurchaseStatus.waitingForPayment);
        guideProposal.setPaymentTime(LocalDateTime.now());

        guideProposalRepository.save(guideProposal);

        return convertToDto(guideProposal, guideProposal.getGuide());
    }

    @Transactional
    public GuideProposalDto refuseGuideProposal(Long travelUserId, GuideProposalDto guideProposalDto) {
        Optional<GuideProposal> guideProposalOptional = guideProposalRepository.findById(guideProposalDto.getGuideProposalId());
        GuideProposal guideProposal = guideProposalOptional.get();

        if (guideProposal.getCart() == null)
            return null; // 이미 삭제된 계획임.

        if (guideProposal.getPurchaseStatus() != PurchaseStatus.waitingForDecision)
            return null; // 잘못된 상태

        guideProposal.setPurchaseStatus(PurchaseStatus.cancellation);
        guideProposal.setCancelTime(LocalDateTime.now());

        guideProposalRepository.save(guideProposal);

        return convertToDto(guideProposal, guideProposal.getGuide());
    }

    @Transactional
    public String deleteGuideProposal(Long travelUserId, Long id) {
        Optional<TravelUser> travelUserOptional = travelUserRepository.findById(travelUserId);
        Optional<Guide> guideOptional = guideRepository.findByTravelUser(travelUserOptional.get());
        Optional<GuideProposal> guideProposalOptional = guideProposalRepository.findByGuideAndGuideProposalId(guideOptional.get(), id);

        if (guideProposalOptional.isEmpty())
            return null;

        GuideProposal guideProposal = guideProposalOptional.get();

        if (guideProposal.getPurchaseStatus() == PurchaseStatus.waitingForPayment ||
        guideProposal.getPurchaseStatus() == PurchaseStatus.cancellation ||
        guideProposal.getPurchaseStatus() == PurchaseStatus.purchaseCompleted ||
        guideProposal.getPurchaseStatus() == PurchaseStatus.travelCompleted)
            return null;

        guideProposalRepository.delete(guideProposal);

        return "삭제가 완료되었습니다.";
    }
    public GuideProposalDto convertToDto(GuideProposal guideProposal, Guide guide) {
        GuideProposalDto guideProposalDto = new GuideProposalDto();
        if (guide != null) {
            GuideDto guideDto = guideService.convertToDto(guide);
            guideProposalDto.setGuideDto(guideDto);
        }

        guideProposalDto.setGuideProposalId(guideProposal.getGuideProposalId());
        guideProposalDto.setArea(guideProposal.getArea());
        guideProposalDto.setGoal(guideProposal.getGoal());
        guideProposalDto.setPrice(guideProposal.getPrice());
        guideProposalDto.setSchedule(guideProposal.getSchedule());
        guideProposalDto.setCreatedAt(guideProposal.getCreatedAt());
        guideProposalDto.setTravelStartDate(guideProposal.getTravelStartDate());
        guideProposalDto.setTravelEndDate(guideProposal.getTravelEndDate());
        guideProposalDto.setPurchaseStatus(guideProposal.getPurchaseStatus());

        if (guideProposal.getCancelTime() != null)
            guideProposalDto.setCancelTime(guideProposal.getCancelTime());

        if (guideProposal.getPaymentTime() != null)
            guideProposalDto.setPaymentTime(guideProposal.getPaymentTime());

        if (guideProposal.getCart() != null)
            guideProposalDto.setPurchaseTravelUserDto(travelUserService.convertToDto(guideProposal.getCart().getTravelUser(), null));

        return guideProposalDto;
    }

    public boolean travelCompleteCheck(GuideProposal guideProposal) {
        if (guideProposal.getPurchaseStatus() == PurchaseStatus.travelCompleted) {
                if(ProductRefreshUtil.isCompletePassed(guideProposal.getTravelEndDate())) {
                    guideProposalRepository.delete(guideProposal);
                    return false;
                }
        }

        return true;
    }

    public boolean startTimeCheck(GuideProposal guideProposal) {
        if (guideProposal.getPurchaseStatus() == PurchaseStatus.registration || guideProposal.getPurchaseStatus() == PurchaseStatus.waitingForDecision) {
                if(ProductRefreshUtil.startTimeCheck(guideProposal.getTravelStartDate())) {
                    guideProposalRepository.delete(guideProposal);
                    return false;
                }
        }

        return true;
    }
    public void cancelToRegistrationCheck(GuideProposal guideProposal) {
        if (guideProposal.getPurchaseStatus() == PurchaseStatus.cancellation) {
                if (ProductRefreshUtil.isCancelPassed(guideProposal.getCancelTime())) {
                    guideProposal.setPurchaseStatus(PurchaseStatus.registration);
                    guideProposal.setCancelTime(null);
                    guideProposal.setCart(null);
                    guideProposalRepository.save(guideProposal);
                }
        }
    }

    public void paymentToRegistrationCheck(GuideProposal guideProposal) {
        if (guideProposal.getPurchaseStatus() == PurchaseStatus.waitingForPayment) {
                if (ProductRefreshUtil.isPaymentPassed(guideProposal.getPaymentTime())) {
                    guideProposal.setPurchaseStatus(PurchaseStatus.registration);
                    guideProposal.setPaymentTime(null);
                    guideProposal.setCart(null);
                    guideProposalRepository.save(guideProposal);
                }
        }
    }

     public void purchaseToTravelCompleteCheck(GuideProposal guideProposal) {
        if (guideProposal.getPurchaseStatus() == PurchaseStatus.purchaseCompleted){
            if (ProductRefreshUtil.travelCompleteCheck(guideProposal.getTravelEndDate())) {
                guideProposal.setPurchaseStatus(PurchaseStatus.travelCompleted);
                guideProposalRepository.save(guideProposal);
            }
        }
    }
}
