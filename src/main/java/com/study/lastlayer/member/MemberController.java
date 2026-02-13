package com.study.lastlayer.member;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.study.lastlayer.auth.CustomUserPrincipal;
import com.study.lastlayer.file.File;

@RestController
public class MemberController {
	@Autowired
	private MemberService memberService;

	/**
	 * 회원 정보 업데이트 API
	 * 
	 * @param id  수정할 회원의 ID (PK)
	 * @param dto 수정할 데이터
	 */
	@PutMapping("/me")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Member> update(@AuthenticationPrincipal CustomUserPrincipal principal,
			@RequestBody MemberUpdateDto dto) {
		Member updatedMember = memberService.updateMember(principal.getMemberId(), dto);
		return ResponseEntity.ok(updatedMember);
	}

	/**
	 * 로그인한 사용자의 회원 정보 조회 API
	 */
	@GetMapping("/me")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Member> getMember(@AuthenticationPrincipal CustomUserPrincipal principal) {
		// 인증 객체에서 현재 로그인한 사용자의 memberId를 가져와 조회
		Member member = memberService.getMember(principal.getMemberId());
		return ResponseEntity.ok(member);
	}

	/**
	 * 프로필 이미지 정보 조회
	 */
	@GetMapping("/me/profile-image")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<File> getProfileImage(@AuthenticationPrincipal CustomUserPrincipal principal) {
		File file = memberService.getProfileImage(principal.getMemberId());
		return ResponseEntity.ok(file);
	}

	@PatchMapping(value = "me/profile-image", consumes = "multipart/form-data")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<File> updateProfileImage(
			@AuthenticationPrincipal CustomUserPrincipal principal,
			@RequestPart("file") MultipartFile multipartFile) throws IOException {

		File fileEntity = memberService.updateProfileImage(principal.getMemberId(), multipartFile);
		return ResponseEntity.ok(fileEntity);
	}


	@DeleteMapping("/me/profile-image")
	@PreAuthorize("isAuthenticated()")
	public void deleteProfileImage(@AuthenticationPrincipal CustomUserPrincipal principal) {
		memberService.deleteProfileImage(principal.getMemberId());
	}
}
