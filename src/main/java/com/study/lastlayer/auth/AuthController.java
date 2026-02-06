package com.study.lastlayer.auth;

import org.hibernate.annotations.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.study.lastlayer.authuser.AuthUserDto;

record LoginRequest(String email, String password) {
}

@RestController
@RequestMapping("auth")
public class AuthController {
	@Autowired
	private AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request) {
		// AuthService에서 검증 후 토큰 발행
		AuthUserDto dto = authService.login(request.email(), request.password());

		return ResponseEntity.ok(dto);
	}

	@PostMapping("/signup")
	public ResponseEntity<?> signUp(@RequestBody SignupRequest request) {
		authService.signup(request);

		AuthUserDto dto = authService.login(request.email(), request.password());

		// 클라이언트가 사용하기 편하도록 Map이나 DTO에 담아서 반환
		return ResponseEntity.ok(dto);
	}

	record IsExistResponse(boolean isExist) {
	}

	@Comment("email 중복 검사")
	@GetMapping("/check")
	public IsExistResponse checkExistence(@RequestParam(name = "email", required = false) String email) {

		// 1. 요청 파라미터 검증 (두 값 모두 없거나, 두 값 모두 있을 때 예외 처리)
		if (email == null) {
			// 400 Bad Request
			throw new com.study.lastlayer.exception.BadRequestException("파라미터 'email'이 사용 되어야 합니다.");
		}

		return new IsExistResponse(authService.checkExistence(email));
	}
}
