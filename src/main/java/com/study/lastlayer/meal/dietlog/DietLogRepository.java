package com.study.lastlayer.meal.dietlog;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DietLogRepository extends JpaRepository<DietLog, Long> {

	/** 회원별 식단 기록 목록 (최신 순) */
	@Query("SELECT d FROM DietLog d WHERE d.member.member_id = :memberId ORDER BY d.dateAt DESC")
	List<DietLog> findByMemberIdOrderByDateAtDesc(@Param("memberId") Long memberId);

	/** 회원별 기간 내 식단 기록 목록 (최신 순) */
	@Query("SELECT d FROM DietLog d WHERE d.member.member_id = :memberId AND d.dateAt BETWEEN :from AND :to ORDER BY d.dateAt DESC")
	List<DietLog> findByMemberIdAndDateAtBetween(@Param("memberId") Long memberId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}