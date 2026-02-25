package com.study.lastlayer.meal.dietlog;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 식단 기록(DietLog) 생성/수정 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class DietLogRequestDto {

	/** 기록할 식단(Meal) ID */
	private Long mealId;

	/** 섭취 날짜·시간 (미입력 시 저장 시각으로 처리) */
	private LocalDateTime dateAt;
}
