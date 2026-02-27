package com.study.lastlayer.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // username으로 회원 조회
    Optional<Member> findByUsername(String username);

    // email로 회원 조회 (AuthUser와 연계 시)
    Optional<Member> findByAuthUserEmail(String email);

}