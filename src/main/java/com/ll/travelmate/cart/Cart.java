package com.ll.travelmate.cart;

import com.ll.travelmate.guideproposal.GuideProposal;
import com.ll.travelmate.user.TravelUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/*
* 상태 : 등록, 결정대기, 결제대기, 취소, 구매 완료, 여행 완료


* 누군가의 카트에 가이드 계획이 올라가면 (가이드계획 구매상태 : 등록 -> 구매대기)
* 가이드는 구매 대기인 상품을 조회하여 수락, 거절 가능 // 수락시 두번 물어보기
* 수락시 -> 결제 대기
* 결제 완료시 -> 구매완료
* 가이드 계획의 마지막 일정날을 기준으로 여행완료 상태로 바뀜
* 해당 여행 완료시 보관은 30일 보관함.
*
* 거절시 -> 취소 (취소된 상품은 1일뒤 다시 재등록 : 등록 상태)
*
* 삭제는 구매자 : 결제 대기 포함 이전 상태만 가능
*       판매자 : 취소, 수락 이전 상태만 가능
*
* 결제대기 상태부터 서로 연락 가능
* */

@Entity
@Getter
@Setter
@ToString
public class Cart {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;
    @OneToOne
    @JoinColumn(name = "travel_user_id") // name은 외래키의 컬럼 이름이다.
    private TravelUser travelUser;
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<GuideProposal> products = new ArrayList<>();
}
