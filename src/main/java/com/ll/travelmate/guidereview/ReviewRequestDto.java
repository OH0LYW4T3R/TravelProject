package com.ll.travelmate.guidereview;

import com.ll.travelmate.guideproposal.GuideProposalDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReviewRequestDto {
    private GuideReviewDto guideReviewDto;
    private GuideProposalDto guideProposalDto;
}
