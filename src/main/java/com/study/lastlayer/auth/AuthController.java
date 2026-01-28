package com.study.lastlayer.auth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
		String token = authService.login(request.email(), request.password());

		// 클라이언트가 사용하기 편하도록 Map이나 DTO에 담아서 반환
		return ResponseEntity.ok(Map.of("accessToken", token, "tokenType", "Bearer"));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> signUp(@RequestBody Map<String, String> request) {
		String email = request.get("email");
		String rawPassword = request.get("password");
		authService.signup(email, rawPassword);

		String token = authService.login(email, rawPassword);

		// 클라이언트가 사용하기 편하도록 Map이나 DTO에 담아서 반환
		return ResponseEntity.ok(Map.of("accessToken", token, "tokenType", "Bearer"));
	}
}
