package com.study.lastlayer.authuser;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.study.lastlayer.auth.AuthService;

record RoleRequest(MemberRole role) {
}

record RoleResponse(
		Long userId, // 대상자 식별을 위해 ID 포함 권장
		List<MemberRole> currentRoles // 변경 후 최종 권한 목록
) {
}

@RestController
public class AuthUserController {
	@Autowired
	private AuthUserService authUserService;

	@Autowired
	private AuthService authService;

	@PostMapping("/users/{userId}/roles")
	@PreAuthorize("hasRole('ADMIN')") // 오직 ADMIN만 이 API에 접근 가능
	@Transactional
	public RoleResponse addRole(
			@PathVariable("userId") Long userId, // 권한을 수정할 대상 유저 ID
			@RequestBody RoleRequest request) {

		List<MemberRole> updatedRoles = authUserService.addRole(userId, request.role());
		return new RoleResponse(userId, updatedRoles);
	}
}
