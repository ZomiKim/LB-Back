package com.study.lastlayer.auth;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;

import com.study.lastlayer.authuser.MemberRole;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {
	private final SecretKey secretKey;

	public JwtUtil(String jwtSecret) {
		this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
	}

	public Claims validateToken(String token) {
		return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
	}

	public Long extractMemberId(Claims claims) {
		return claims.get("memberId", Long.class);
	}

	@SuppressWarnings("unchecked")
	public List<MemberRole> extractRoles(Claims claims) {
		// claims.get()의 결과가 Object이므로 List로 캐스팅합니다.
		return (List<MemberRole>) claims.get("roles");
	}

	@Value("${jwt.expiration}")
	private long expirationTime;

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
}
