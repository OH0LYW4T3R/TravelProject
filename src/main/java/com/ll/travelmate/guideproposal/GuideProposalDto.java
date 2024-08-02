package com.ll.travelmate.guideproposal;

import com.ll.travelmate.guide.GuideDto;
import com.ll.travelmate.user.TravelUserDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class GuideProposalDto {
    private Long guideProposalId;
    private GuideDto guideDto;
    private TravelUserDto purchaseTravelUserDto;
    private String area;
    private Integer price;
    private String goal;
    private String schedule;
    private LocalDateTime createdAt; // 추가
    private LocalDateTime cancelTime; // 추가
    private LocalDateTime paymentTime; // 추가
    private LocalDateTime travelStartDate;
    private LocalDateTime travelEndDate;
    private PurchaseStatus purchaseStatus;
}
