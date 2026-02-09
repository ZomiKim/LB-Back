package com.study.lastlayer.auth;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.lastlayer.authuser.AuthUser;
import com.study.lastlayer.authuser.AuthUserDto;
import com.study.lastlayer.authuser.AuthUserRepository;
import com.study.lastlayer.authuser.MemberRole;
import com.study.lastlayer.exception.BadRequestException;
import com.study.lastlayer.member.Member;
import com.study.lastlayer.member.MemberRepository;

import jakarta.servlet.http.HttpServletResponse;

record SignupRequest(
		// AuthUser 정보
		String email, String password,

		// Member 정보
		String name, String phone, String gender, String birthday, // "yyyy-MM-dd"
		Float height, Float weight, Integer target_date, String goal, Float goal_weight, String allergies,
		String special_notes) {
}

@Service
public class AuthService {
	private final AuthUserRepository authUserRepository;
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	// 명시적 생성자 주입
	public AuthService(AuthUserRepository authUserRepository, MemberRepository memberRepository,
			PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
		this.authUserRepository = authUserRepository;
		this.memberRepository = memberRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
	}

	@Transactional(readOnly = true)
	public String createToken(String email) {
		AuthUserDto user = authUserRepository.findUserAuthByEmail(email)
				.orElseThrow(() -> new BadRequestException("이메일을 찾을 수 없습니다."));
		return jwtUtil.createToken(user.getEmail(), user.getId(), user.getRoles(), user.getName());
	}

	@Transactional(readOnly = true)
	public AuthUserDto login(String email, String password, HttpServletResponse response) {
		// 1. DTO 프로젝션으로 사용자 정보 조회 (Member 정보 포함)
		AuthUserDto userDto = authUserRepository.findUserAuthByEmail(email)
				.orElseThrow(() -> new BadRequestException("이메일을 찾을 수 없습니다."));

		// 2. 비밀번호 일치 여부 확인
		if (password != null && !passwordEncoder.matches(password, userDto.getPassword())) {
			throw new BadRequestException("비밀번호가 일치하지 않습니다.");
		}

		// 3. 별도로 Roles(권한) 정보를 조회하여 DTO에 세팅
		// (JPQL new 연산자의 한계를 극복하기 위한 필수 단계)
		List<MemberRole> roles = authUserRepository.findRolesByAuthUserId(userDto.getId());
		userDto.setRoles(roles);

		// 4. 토큰 생성 및 반환
		userDto.setAccessToken(
				jwtUtil.createToken(userDto.getEmail(), userDto.getId(), userDto.getRoles(), userDto.getName()));

		setRefreshToken(email, response);
		return userDto;
	}

	void setRefreshToken(String email, HttpServletResponse response) {
		// 2. RefreshToken을 쿠키로 굽기
		String refreshToken = jwtUtil.createToken(email, 60 * 24L); // 24시간

		jakarta.servlet.http.Cookie refreshTokenCookie = new jakarta.servlet.http.Cookie("refreshToken", refreshToken);
		refreshTokenCookie.setHttpOnly(true);
		refreshTokenCookie.setPath("/");
		refreshTokenCookie.setMaxAge(60 * 60 * 24); // 24시간
		refreshTokenCookie.setAttribute("SameSite", "Lax"); // Lax : 외부 사이트에서 링크 클릭 (GET) 허용. 예) 이메일로 "비밀번호 변경" 링크
		response.addCookie(refreshTokenCookie);
	}

	@Transactional
	public void signup(SignupRequest request) {
		// 1. 중복 체크
		if (authUserRepository.existsByEmail(request.email())) {
			throw new BadRequestException("이미 존재하는 이메일입니다.");
		}

		// 2. AuthUser 저장 (계정 생성)
		AuthUser authUser = AuthUser.builder().email(request.email())
				.password(passwordEncoder.encode(request.password())).roles(Arrays.asList(MemberRole.USER)).build();

		AuthUser savedAuthUser = authUserRepository.save(authUser);

		// 3. Member 저장 (프로필 및 신체 정보)
		// daily_calories 계산 로직 (예시: 기초대사량 기반 간단한 공식)
		int calculatedCalories = calculateDailyCalories(request);

		Member member = Member.builder().authUser(savedAuthUser) // @MapsId에 의해 ID 공유
				.name(request.name()).phone(request.phone()).gender(request.gender())
				.birthday(LocalDate.parse(request.birthday())).height(request.height()).weight(request.weight())
				.target_date(request.target_date()).goal(request.goal()).goal_weight(request.goal_weight())
				.allergies(request.allergies()).special_notes(request.special_notes())
				.daily_calories(calculatedCalories) // 자동 계산된 값 주입
				.notificationCount(0).point(0L).build();

		memberRepository.save(member);

		// 4. 가입 후 즉시 로그인을 위해 DTO 반환
		String token = jwtUtil.createToken(savedAuthUser.getEmail(), savedAuthUser.getId(), savedAuthUser.getRoles(),
				request.name());
	}

	// Mifflin-St Jeor 공식	
	private int calculateDailyCalories(SignupRequest req) {
		// Step 1: 나이 계산 (만 나이 기준)
		LocalDate birthDate = LocalDate.parse(req.birthday());
		LocalDate today = LocalDate.now();

		// Step 1: 만 나이 계산
		int age = today.getYear() - birthDate.getYear();
		if (birthDate.plusYears(age).isAfter(today)) {
			age--; // 올해 생일이 아직 안 지났으면 1살 차감
		}

		// Step 2: 기초대사량 (BMR) 계산
		double bmr;
		if ("M".equalsIgnoreCase(req.gender())) {
			// (남) (10 * W) + (6.25 * H) - (5 * A) + 5
			bmr = (10 * req.weight()) + (6.25 * req.height()) - (5 * age) + 5;
		} else {
			// (여) (10 * W) + (6.25 * H) - (5 * A) - 161
			bmr = (10 * req.weight()) + (6.25 * req.height()) - (5 * age) - 161;
		}

		// Step 3: 유지 칼로리 (TDEE) - 활동량 '보통' 기준 (1.55)
		double tdee = bmr * 1.55;

		// Step 4 & 5: 하루 필요 감량분 계산
		// (현재체중 - 목표체중) * 7700 / 목표기간
		double totalWeightToLose = req.weight() - req.goal_weight();
		double dailyDeficit = (totalWeightToLose * 7700) / req.target_date();

		// Step 6: 최종 섭취량 (TDEE - 하루 필요 감량분)
		int dailyCalories = (int) Math.round(tdee - dailyDeficit);

		// [안전 장치] 생존을 위한 최소 칼로리 제한 (기초대사량 이하로 먹으면 위험)
		int minimumSafetyCalories = (int) Math.round(bmr);

		return Math.max(dailyCalories, minimumSafetyCalories);
	}

	public boolean checkExistence(String email) {

		boolean isExist = true;

		if (email != null) {
			// Service 레이어에서 해당 이메일이 이미 존재하는지 확인
			isExist = authUserRepository.existsByEmail(email);
		}
		return isExist;
	}
}
