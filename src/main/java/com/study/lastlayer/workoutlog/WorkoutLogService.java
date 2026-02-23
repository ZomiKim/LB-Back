package com.study.lastlayer.workoutlog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.lastlayer.exercise.Exercise;
import com.study.lastlayer.exercise.ExerciseRepository;
import com.study.lastlayer.member.Member;
import com.study.lastlayer.member.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkoutLogService {

    private final WorkoutLogRepository workoutLogRepository;
    private final ExerciseRepository exerciseRepository;
    private final MemberRepository memberRepository;

    // 운동 기록 생성
    public WorkoutResponseDto createWorkout(
            Long memberId,
            WorkoutCreateRequestDto dto
    ) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        Exercise exercise = exerciseRepository.findById(dto.getExerciseId())
                .orElseThrow(() -> new IllegalArgumentException("운동이 존재하지 않습니다."));

        int calories = calculateCalories(
                exercise.getMet(),
                member.getWeight(),
                dto.getDurationMin()
        );

        WorkoutLog log = new WorkoutLog();
        log.setMember(member);
        log.setExercise(exercise);
        log.setDurationMin(dto.getDurationMin());
        log.setBurntCalories(calories);
        log.setDateAt(dto.getDateAt());

        workoutLogRepository.save(log);

        return WorkoutResponseDto.fromEntity(log);
    } 

    // 운동 기록 조회 (페이징)
    @Transactional(readOnly = true)
    public Page<WorkoutResponseDto> getWorkouts(
            Long memberId,
            Pageable pageable
    ) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        return workoutLogRepository
                .findByMemberOrderByDateAtDesc(member, pageable)
                .map(WorkoutResponseDto::fromEntity);
    }

    // 칼로리 계산 공식
    private int calculateCalories(Float met, Float weight, int durationMin) {
        double hours = durationMin / 60.0;
        return (int) (met * weight * hours);
    }

}