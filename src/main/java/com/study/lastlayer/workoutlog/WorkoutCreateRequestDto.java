package com.study.lastlayer.workoutlog;

import java.time.LocalDate;

import lombok.Getter;


@Getter
public class WorkoutCreateRequestDto {
	
	private Long exerciseId;
	private Integer durationMin;
	private LocalDate dateAt;

}
