package com.study.lastlayer.meal.mealitem;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

/**
 * 식단 항목(MealItem) 조회 응답 DTO
 */
@Getter
@Builder
public class MealItemResponseDto {

	private Long id;
	private Long mealId;
	private String name;
	private Integer amount;
	private Integer carbohydrate;
	private Integer protein;
	private Integer fat;
	private Integer calories;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static MealItemResponseDto fromEntity(MealItem entity) {
		return MealItemResponseDto.builder()
				.id(entity.getId())
				.mealId(entity.getMeal() != null ? entity.getMeal().getId() : null)
				.name(entity.getName())
				.amount(entity.getAmount())
				.carbohydrate(entity.getCarbohydrate() != null ? entity.getCarbohydrate() : 0)
				.protein(entity.getProtein() != null ? entity.getProtein() : 0)
				.fat(entity.getFat() != null ? entity.getFat() : 0)
				.calories(entity.getCalories() != null ? entity.getCalories() : 0)
				.createdAt(entity.getCreatedAt())
				.updatedAt(entity.getUpdatedAt())
				.build();
	}
}
