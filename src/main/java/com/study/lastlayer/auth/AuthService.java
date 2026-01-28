package com.study.lastlayer.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.lastlayer.authUser.AuthUser;
import com.study.lastlayer.authUser.AuthUserRepository;
import com.study.lastlayer.exception.BadRequestException;

@Service
public class AuthService {
	private final JwtUtil jwtUtil;
	private final AuthUserRepository authUserRepository; // 추가
	private final PasswordEncoder passwordEncoder; // 암호화 비교를 위해 추가

	public AuthService(JwtUtil jwtUtil, AuthUserRepository authRepository, PasswordEncoder passwordEncoder) {
		this.jwtUtil = jwtUtil;
		this.authUserRepository = authRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public String login(String email, String password) {
		// 1. 레코드를 통해 DB에서 사용자 조회
		AuthUser user = authUserRepository.findByEmail(email)
				.orElseThrow(() -> new BadRequestException("이메일을 찾을 수 없습니다."));

		// 2. 비밀번호 일치 여부 확인
		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new BadRequestException("비밀번호가 일치하지 않습니다.");
		}

		// 3. DB에서 가져온 실제 id와 email로 토큰 생성
		return jwtUtil.createToken(user.getEmail(), user.getId(), user.getRoles());
	}

	@Transactional
	public void signup(String email, String password) {
		// 1. 중복 검사
		authUserRepository.findByEmail(email).ifPresent(u -> {
			throw new BadRequestException(String.format("이미 사용 중인 이메일입니다.[%s]", email));
		});

		// 2. 인증 정보(AuthUser) 생성 및 저장
		String encodedPassword = passwordEncoder.encode(password);
		AuthUser authUser = AuthUser.builder().email(email).password(encodedPassword).build();
		authUserRepository.save(authUser); // 먼저 DB에 저장하여 ID 발급
	}
}
