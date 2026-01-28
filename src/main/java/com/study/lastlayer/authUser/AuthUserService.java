package com.study.lastlayer.authUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.lastlayer.exception.BadRequestException;

@Service
public class AuthUserService {
	@Autowired
	private AuthUserRepository authUserRepository;

	@Transactional
	public void addRole(Long memberId, String newRole) {
		AuthUser user = authUserRepository.findById(memberId)
				.orElseThrow(() -> new BadRequestException(String.format("memberId[%d] 없음", memberId)));

		// 별도의 Repo 없이 리스트에 추가만 하면 DB에 반영됨
		user.getRoles().add(newRole);
		// @Transactional에 의해 메서드 종료 시 dirty checking으로 자동 저장됩니다.
	}
}
