package com.ll.travelmate.cart;

import com.ll.travelmate.guideproposal.GuideProposalDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CartDto {
    private Long cartId;
    private Long guideProposalId;
    private GuideProposalDto guideProposalDto;
}
