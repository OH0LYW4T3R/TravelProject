package com.ll.travelmate.block;

import com.ll.travelmate.friend.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlockService {
    private final BlockRepository blockRepository;
    private final FriendRepository friendRepository;

//    @Transactional
//    public BlockDto blockTravelUser(Long travelUserId, BlockDto blockDto) {
//        // 친구가 된 상태거나, 친구가 아니거나
//        // 친구가 된 상태면, 내 친구목록에선 안보이지만, 상대방 목록에선 뜨되
//        // 상대방이 메시지를 보내려고 하면 막아야함
//    }

    public BlockDto convertBlockDto(Block block) {
        BlockDto blockDto = new BlockDto();

        return blockDto;
    }
}
