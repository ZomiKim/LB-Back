package com.study.lastlayer.meal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 추천/직접 입력 시 개별 음식 정보 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class MealRecommendItemDto {

	private String name;
	private Integer amount;       // g
	private Integer carbohydrate; // kcal
	private Integer protein;      // kcal
	private Integer fat;          // kcal
	private Integer calories;     // kcal
}

