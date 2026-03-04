package com.study.lastlayer.application;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.study.lastlayer.auth.CustomUserPrincipal;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    //가입 신청
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApplicationResponseDto> apply(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody ApplicationCreateDto dto) {

        ApplicationResponseDto response =
                applicationService.apply(principal.getMemberId(), dto);

        return ResponseEntity.ok(response);
    }
    
 // 특정 클럽의 PENDING 신청 목록 조회 (매니저만 가능)
    @GetMapping("/club/{clubId}/pending")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ApplicationListDto>> getPendingApplications(
    		@PathVariable("clubId") Long clubId,
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        List<ApplicationListDto> list =
                applicationService.getPendingApplications(principal.getMemberId(), clubId);

        return ResponseEntity.ok(list);
    }
}