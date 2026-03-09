package com.study.lastlayer.externapi.chatbot;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.study.lastlayer.auth.CustomUserPrincipal;
import com.study.lastlayer.member.Member;
import com.study.lastlayer.member.MemberService;

@RestController
@RequestMapping("/api/v1/chatbot")
public class ChatbotController {

    private final String FASTAPI_URL = "http://localhost:8000/api/v1/chatbot/";

    private final RestTemplate restTemplate = new RestTemplate();

    private final MemberService memberService;

    public ChatbotController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<ChatResponseDto> askChatbot(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody ChatRequestDto requestdto) {

        // 로그인 사용자 정보 조회
        Member member = memberService.getMember(principal.getMemberId());

        // DTO에 회원정보 추가
        requestdto.setName(member.getName());
        requestdto.setPoint(member.getPoint());

        ChatResponseDto response = restTemplate.postForObject(
                FASTAPI_URL, requestdto, ChatResponseDto.class);

        return ResponseEntity.ok(response);
    }
}
