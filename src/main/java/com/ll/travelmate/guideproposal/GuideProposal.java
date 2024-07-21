package com.ll.travelmate.guideproposal;

import com.ll.travelmate.guide.Guide;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
public class GuideProposal {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guideProposalId;
    private Integer price;
    private String goal;
    private String schedule;
    private LocalDateTime travelStartDate;
    private LocalDateTime travelEndDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guide_id")
    private Guide guide;
}
