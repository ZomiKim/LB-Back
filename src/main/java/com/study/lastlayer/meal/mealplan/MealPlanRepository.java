package com.study.lastlayer.meal.mealplan;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {

	/**
	 * 특정 회원의 특정 날짜에 해당하는 추천 식단 목록을 id 오름차순으로 조회합니다.
	 */
	@Query("""
			SELECT mp
			FROM MealPlan mp
			WHERE mp.member.member_id = :memberId
			  AND mp.dateAt = :dateAt
			ORDER BY mp.id
			""")
	List<MealPlan> findByMemberIdAndDateAtOrderById(@Param("memberId") Long memberId, @Param("dateAt") LocalDate dateAt);
}

