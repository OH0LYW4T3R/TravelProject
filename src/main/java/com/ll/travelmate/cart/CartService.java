package com.ll.travelmate.cart;

import com.ll.travelmate.guideproposal.*;
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

@Service
@RequiredArgsConstructor
public class CartService {
    private final TravelUserService travelUserService;
    private final GuideProposalService guideProposalService;
    private final TravelUserRepository travelUserRepository;
    private final GuideProposalRepository guideProposalRepository;
    private final CartRepository cartRepository;

    @Transactional
    public CartDto addGuideProposalProduct(Long travelUserId, CartDto cartDto) {
        // 가이드가 올린 계획을 카트에 연결하기
        // 내계정에 연결되어있는 카트 가져오기
        Optional<TravelUser> travelUserOptional = travelUserRepository.findById(travelUserId);
        Cart cart = travelUserOptional.get().getCart();
        Optional<GuideProposal> guideProposalOptional = guideProposalRepository.findById(cartDto.getGuideProposalId());

        if (guideProposalOptional.isEmpty())
            return null; // 삭제했거나

        GuideProposal guideProposal = guideProposalOptional.get();

        if (travelUserId.equals(guideProposal.getGuide().getTravelUser().getTravelUserId()))
            return null; // 자기 자신것을 살려고 할때

        if (guideProposal.getPurchaseStatus() != PurchaseStatus.registration)
            return null; // 누가 이미 가져갔으면

        guideProposal.setPurchaseStatus(PurchaseStatus.waitingForDecision);
        guideProposal.setCart(cart);
        guideProposalRepository.save(guideProposal);

        return convertToDto(cart, guideProposal);
    }

    @Transactional
    public CartDto purchaseProduct(Long travelUserId, GuideProposalDto guideProposalDto) {
        // 나중에 결제 로직이 들어가야함.
        Optional<TravelUser> travelUserOptional = travelUserRepository.findById(travelUserId);
        Cart cart = travelUserOptional.get().getCart();
        Optional<GuideProposal> guideProposalOptional = guideProposalRepository.findById(guideProposalDto.getGuideProposalId());

        if (guideProposalOptional.isEmpty())
            return null;

        if (!Objects.equals(cart.getCartId(), guideProposalOptional.get().getCart().getCartId()))
            return null; // 자기 상품이 아닌것

        GuideProposal guideProposal = guideProposalOptional.get();

        if (guideProposal.getPurchaseStatus() != PurchaseStatus.waitingForPayment)
            return null; // 결제 대기 상태가 아닌것

        guideProposal.setPurchaseStatus(PurchaseStatus.purchaseCompleted);
        guideProposal.setPaymentTime(LocalDateTime.now());
        guideProposalRepository.save(guideProposal);

        return convertToDto(cart, guideProposal);
    }

    @Transactional
    public String deleteGuideProposalProduct(Long travelUserId, GuideProposalDto guideProposalDto) {
        Optional<TravelUser> travelUserOptional = travelUserRepository.findById(travelUserId);
        Cart cart = travelUserOptional.get().getCart();
        Optional<GuideProposal> guideProposalOptional = guideProposalRepository.findById(guideProposalDto.getGuideProposalId());

        if (guideProposalOptional.isEmpty())
            return null;

        if (!Objects.equals(cart.getCartId(), guideProposalOptional.get().getCart().getCartId()))
            return null; // 자기 상품이 아닌것

        GuideProposal guideProposal = guideProposalOptional.get();

        if (guideProposal.getPurchaseStatus() == PurchaseStatus.cancellation ||
        guideProposal.getPurchaseStatus() == PurchaseStatus.purchaseCompleted ||
        guideProposal.getPurchaseStatus() == PurchaseStatus.travelCompleted)
            return null;

        guideProposal.setCart(null);
        guideProposal.setPurchaseStatus(PurchaseStatus.registration);
        guideProposal.setPaymentTime(null);

        guideProposalRepository.save(guideProposal);

        return "";
    }

    @Transactional
    public List<CartDto> readAllProduct(Long travelUserId) {
        Optional<TravelUser> travelUserOptional = travelUserRepository.findById(travelUserId);
        Cart cart = travelUserOptional.get().getCart();
        List<GuideProposal> guideProposals = cart.getProducts();
        List<CartDto> cartDtos = new ArrayList<>();

        for (GuideProposal guideProposal : guideProposals) {
            purchaseToTravelCompleteCheck(guideProposal);
            cartDtos.add(convertToDto(cart, guideProposal));
        }

        return cartDtos;
    }

    @Transactional
    public List<CartDto> readPurchaseStatusProduct(Long travelUserId, PurchaseStatus purchaseStatus) {
        Optional<TravelUser> travelUserOptional = travelUserRepository.findById(travelUserId);
        Cart cart = travelUserOptional.get().getCart();
        List<GuideProposal> guideProposals = guideProposalRepository.findByCartAndPurchaseStatus(cart, purchaseStatus);
        List<CartDto> cartDtos = new ArrayList<>();

        for (GuideProposal guideProposal : guideProposals) {
            cartDtos.add(convertToDto(cart, guideProposal));
        }

        return cartDtos;
    }

    @Transactional
    public CartDto readProduct(Long guideProposalId) {
        Optional<GuideProposal> guideProposalOptional = guideProposalRepository.findById(guideProposalId);

        if (guideProposalOptional.isEmpty())
            return null;

        GuideProposal guideProposal = guideProposalOptional.get();
        purchaseToTravelCompleteCheck(guideProposal);

        return convertToDto(guideProposal.getCart(), guideProposal);
    }

    public CartDto convertToDto(Cart cart, GuideProposal guideProposal) {
        CartDto cartDto = new CartDto();

        cartDto.setCartId(cart.getCartId());
        cartDto.setGuideProposalId(guideProposal.getGuideProposalId());
        cartDto.setGuideProposalDto(guideProposalService.convertToDto(guideProposal, guideProposal.getGuide()));

        return cartDto;
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
