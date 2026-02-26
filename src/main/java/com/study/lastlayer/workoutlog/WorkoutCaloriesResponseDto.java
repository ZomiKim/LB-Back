package com.study.lastlayer.workoutlog;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WorkoutCaloriesResponseDto {

    private LocalDate date;
    private Integer totalCalories;
}