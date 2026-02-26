package com.study.lastlayer.workoutlog;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.study.lastlayer.member.Member;

public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, Long>{
	
	Page<WorkoutLog> findByMember(
	        Member member,
	        Pageable pageable
	);
	
	@Query("""
		    SELECT w.dateAt, SUM(w.burntCalories)
		    FROM WorkoutLog w
		    WHERE w.member = :member
		      AND w.dateAt BETWEEN :startDate AND :endDate
		    GROUP BY w.dateAt
		    ORDER BY w.dateAt ASC
		""")
		List<Object[]> sumCaloriesByDate(
		        @Param("member") Member member,
		        @Param("startDate") LocalDate startDate,
		        @Param("endDate") LocalDate endDate
		);

}
