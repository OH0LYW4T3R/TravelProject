package com.ll.travelmate.block;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/block")
public class BlockController {
    @PostMapping("/block-user")
    public ResponseEntity<Object> blockTravelUser(Authentication auth, BlockDto blockDto) {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
