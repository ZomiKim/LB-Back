package com.study.lastlayer.authUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

record RoleRequest(String roleName) {
}

@RestController
public class AuthUserController {
	@Autowired
	private AuthUserService authUserService;

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/auth/role")
	@Transactional
	public void addRole(@AuthenticationPrincipal com.study.lastlayer.auth.CustomUserPrincipal principal,
			@RequestBody RoleRequest request) {
		// 서비스의 로직을 호출 (BadRequestException 등은 ControllerAdvice에서 처리 권장)
		authUserService.addRole(principal.getMemberId(), request.roleName());

		return;
	}
}
