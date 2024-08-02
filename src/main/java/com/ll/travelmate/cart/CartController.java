package com.ll.travelmate.cart;

import com.ll.travelmate.guideproposal.GuideProposalDto;
import com.ll.travelmate.guideproposal.PurchaseStatus;
import com.ll.travelmate.member.CustomMember;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    @PostMapping("/add-product")
    public ResponseEntity<Object> addProduct(@RequestBody CartDto cartDto, Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        CartDto createCartDto = cartService.addGuideProposalProduct(customMember.getTravelUserId(), cartDto);

        if (createCartDto == null)
            return new ResponseEntity<>("접근할 수 없는 계획이거나, 이미 다른 사용자가 추가하였습니다.", HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(createCartDto, HttpStatus.OK);
    }

    @PostMapping("/purchase-product")
    public ResponseEntity<Object> purchaseProduct(@RequestBody GuideProposalDto guideProposalDto, Authentication auth) {
        // 추후 결제 로직이 들어가야 할 부분
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        CartDto cartDto = cartService.purchaseProduct(customMember.getTravelUserId(), guideProposalDto);

        if (cartDto == null)
            return new ResponseEntity<>("잘못된 접근입니다.", HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }

    @GetMapping("/reading/all")
    public ResponseEntity<Object> readAllProduct(Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        List<CartDto> cartDtoList = cartService.readAllProduct(customMember.getTravelUserId());

        return new ResponseEntity<>(cartDtoList, HttpStatus.OK);
    }

    @GetMapping("/reading/purchase-status")
    public ResponseEntity<Object> readPurchaseStatusProduct(
            @RequestParam(name = "purchaseStatus", defaultValue = "travelCompleted") String purchaseStatus
            , Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        List<CartDto> cartDtoList = cartService.readPurchaseStatusProduct(customMember.getTravelUserId(), PurchaseStatus.valueOf(purchaseStatus));

        return new ResponseEntity<>(cartDtoList, HttpStatus.OK);
    }

    @GetMapping("/reading/{id}")
    public ResponseEntity<Object> readProduct(@PathVariable Long id) {
        CartDto cartDto = cartService.readProduct(id);

        if (cartDto == null)
            return new ResponseEntity<>("찾으시는 정보가 없거나, 잘못된 접근입니다." ,HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }

    @DeleteMapping("/delete-product")
    public ResponseEntity<Object> addProduct(@RequestBody GuideProposalDto guideProposalDto, Authentication auth) {
        CustomMember customMember = (CustomMember) auth.getPrincipal();
        String empty = cartService.deleteGuideProposalProduct(customMember.getTravelUserId(), guideProposalDto);

        if (empty == null)
            return new ResponseEntity<>("접근할 수 없는 계획이거나, 삭제할 수 없는 계획입니다.",HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
