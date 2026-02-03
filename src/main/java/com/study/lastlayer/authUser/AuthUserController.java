package com.study.lastlayer.authUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.study.lastlayer.auth.AuthService;
import com.study.lastlayer.auth.CustomUserPrincipal;

record RoleRequest(MemberRole role) {
}

record RoleRespones(MemberRole role, String jwt) {
}

@RestController
public class AuthUserController {
	@Autowired
	private AuthUserService authUserService;

	@Autowired
	private AuthService authService;

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/auth/role")
	@Transactional
	public RoleRespones addRole(@AuthenticationPrincipal CustomUserPrincipal principal,
			@RequestBody RoleRequest request) {
		// 서비스의 로직을 호출 (BadRequestException 등은 ControllerAdvice에서 처리 권장)
		authUserService.addRole(principal.getMemberId(), request.role());
		RoleRespones r = new RoleRespones(request.role(), authService.createToken(principal.getUsername()));

		return r;
	}
}
