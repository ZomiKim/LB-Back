package com.study.lastlayer.auth;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.study.lastlayer.authuser.MemberRole;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component // 이제 Spring이 자동으로 이 클래스를 찾아서 Bean으로 등록합니다.
public class JwtUtil {
	private final SecretKey secretKey;
	private final long expirationTime;

	// 생성자에서 Spring이 설정 파일의 값을 찾아서 직접 넣어줍니다.
	public JwtUtil(
			@Value("${lastlayer.jwt-secret}") String jwtSecret,
			@Value("${jwt.expiration}") long expirationTime) {
		this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
		this.expirationTime = expirationTime;
	}

	public Claims validateToken(String token) {
		return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
	}

	public Long extractMemberId(Claims claims) {
		return claims.get("memberId", Long.class);
	}

	@SuppressWarnings("unchecked")
	public List<MemberRole> extractRoles(Claims claims) {
		// 1. 우선 Object로 가져옵니다.
		Object rolesObject = claims.get("roles");

		// 2. 만약 null이면 빈 리스트 반환
		if (rolesObject == null) {
			return Collections.emptyList();
		}

		// 3. List<String> 형태의 데이터를 MemberRole enum 리스트로 변환
		List<String> rolesList = (List<String>) rolesObject;

		return rolesList.stream()
				.map(MemberRole::fromString) // 아까 만든 fromString 메서드 활용!
				.collect(Collectors.toList());
	}

	// --- JWT 발행
	public String createToken(String email, Long memberId, List<MemberRole> roles, String name) {
		Claims claims = Jwts.claims().setSubject(email);
		claims.put("memberId", memberId); // Long형 ID 저장
		claims.put("roles", roles); // 권한 정보 추가
		claims.put("name", name);

		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + expirationTime * 60 * 1000L);

		return Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(expiryDate)
				.signWith(secretKey, SignatureAlgorithm.HS256).compact();
	}

	// --- JWT 발행
	public String createToken(String email, Long expirationTimeMin) {
		Claims claims = Jwts.claims().setSubject(email);

		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + expirationTimeMin * 60 * 1000L);

		return Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(expiryDate)
				.signWith(secretKey, SignatureAlgorithm.HS256).compact();
	}
}
