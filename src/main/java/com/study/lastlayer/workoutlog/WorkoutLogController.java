package com.study.lastlayer.workoutlog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/workouts")
@RequiredArgsConstructor
public class WorkoutLogController {

    private final WorkoutLogService workoutLogService;

    // 운동 기록 생성
    @PostMapping("/{memberId}")
    public WorkoutResponseDto createWorkout(
            @PathVariable("memberId") Long memberId,
            @RequestBody WorkoutCreateRequestDto dto
    ) {
            return workoutLogService.createWorkout(memberId, dto);
    }

    // 운동 기록 조회
    @GetMapping("/{memberId}")
    public Page<WorkoutResponseDto> getWorkouts(
            @PathVariable("memberId") Long memberId,
            Pageable pageable
    ) {
        return workoutLogService.getWorkouts(memberId, pageable);
    }
}