package com.study.lastlayer.workoutlog;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.study.lastlayer.member.Member;

public interface WorkoutLogRepository extends JpaRepository<WorkoutLog, Long>{
	
	Page<WorkoutLog> findByMemberOrderByDateAtDesc(
			Member member,
			Pageable pageable
			);
	
	Page<WorkoutLog> findByMemberAndDateAt(
			Member member,
			LocalDate dateAt,
			Pageable pageable
			);

}
