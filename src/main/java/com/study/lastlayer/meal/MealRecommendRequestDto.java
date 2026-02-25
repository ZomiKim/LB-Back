package com.study.lastlayer.meal;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 추천 식단 생성 요청 DTO
 *
 * memberId와 적용 날짜(dateAt), 식단 유형, 구성 음식 목록을 받습니다.
 */
@Getter
@Setter
@NoArgsConstructor
public class MealRecommendRequestDto {

	private Long memberId;
	private LocalDate dateAt; // 추천/적용 날짜 (null이면 오늘)

	/**
	 * 식사 유형 (B: 아침, L: 점심, D: 저녁, S: 간식)
	 */
	private String mealType;

	/**
	 * 식단 이름/요약 (예: \"추천 아침 세트\")
	 */
	private String menu;

	private List<MealRecommendItemDto> items;
}

