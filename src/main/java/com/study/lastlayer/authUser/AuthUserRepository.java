package com.study.lastlayer.authUser;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {
	Optional<AuthUser> findByEmail(String email);

	Boolean existsByEmail(String email);
}
