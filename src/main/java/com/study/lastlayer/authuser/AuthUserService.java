package com.study.lastlayer.authuser;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.lastlayer.exception.BadRequestException;

@Service
public class AuthUserService {
	@Autowired
	private AuthUserRepository authUserRepository;

	@Transactional
	public List<MemberRole> addRole(Long memberId, MemberRole newRole) {
		AuthUser user = authUserRepository.findById(memberId)
				.orElseThrow(() -> new BadRequestException(String.format("memberId[%d] 없음", memberId)));
		// 중복 체크
		if (user.getRoles().contains(newRole)) {
			// 이미 권한이 있다면 예외 발생 (400 또는 409)
			throw new BadRequestException(String.format("MemberID[%d], 이미 '%s' 권한을 가지고 있습니다.", memberId, newRole));
		}

		// 별도의 Repo 없이 리스트에 추가만 하면 DB에 반영됨
		user.getRoles().add(newRole);
		return user.getRoles(); // 최신화된 전체 리스트 반환
	}
}
