package com.ll.travelmate.block;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlockService {
    private final BlockRepository blockRepository;

//    @Transactional
//    public BlockDto blockTravelUser(Long travelUserId, BlockDto blockDto) {
//
//    }

    public BlockDto convertBlockDto(Block block) {
        BlockDto blockDto = new BlockDto();

        return blockDto;
    }
}
