package com.study.lastlayer.authuser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.lastlayer.exception.BadRequestException;

@Service
public class AuthUserService {
	@Autowired
	private AuthUserRepository authUserRepository;

	@Transactional
	public void addRole(Long memberId, MemberRole newRole) {
		AuthUser user = authUserRepository.findById(memberId)
				.orElseThrow(() -> new BadRequestException(String.format("memberId[%d] 없음", memberId)));
		// 중복 체크
		if (user.getRoles().contains(newRole)) {
			// 이미 권한이 있다면 예외 발생 (400 또는 409)
			throw new BadRequestException(String.format("이미 '%s' 권한을 가지고 있습니다.", newRole));
		}

		// 별도의 Repo 없이 리스트에 추가만 하면 DB에 반영됨
		user.getRoles().add(newRole);
		// @Transactional에 의해 메서드 종료 시 dirty checking으로 자동 저장됩니다.
	}
}
