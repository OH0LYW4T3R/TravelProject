package com.ll.travelmate.guideproposal;

import com.ll.travelmate.cart.Cart;
import com.ll.travelmate.guide.Guide;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
public class GuideProposal {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guideProposalId;
    private String area;
    private Integer price;
    private String goal;
    private String schedule;
    @CreationTimestamp
    private LocalDateTime createdAt; // 추가
    private LocalDateTime cancelTime; // 추가
    private LocalDateTime paymentTime; // 추가 해당 컬럼은 두번이 기록된다. waitingForPayment때, purchaseCompleted때
    private LocalDateTime travelStartDate;
    private LocalDateTime travelEndDate;
    private PurchaseStatus purchaseStatus; // 추가

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guide_id")
    private Guide guide;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart; // 추가

}
