package com.study.lastlayer.meal;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

/**
 * 추천 생성 응답 DTO
 *
 * 생성된 Meal, MealItem 목록, MealPlan id를 함께 반환합니다.
 */
@Getter
@Builder
public class MealRecommendResponseDto {

	private Long mealPlanId;
	private MealResponseDto meal;
	private List<MealRecommendItemDto> items;
}

