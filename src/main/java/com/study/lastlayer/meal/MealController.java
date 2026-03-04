package com.study.lastlayer.meal;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.study.lastlayer.auth.CustomUserPrincipal;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/meals")
@RequiredArgsConstructor
public class MealController {

	private final MealService mealService;

	/** 식단 목록 조회 (로그인 사용자 기준) */
	@GetMapping
	public List<MealResponseDto> getMeals(@AuthenticationPrincipal CustomUserPrincipal principal) {
		return mealService.getAllMeals(principal.getMemberId());
	}

	/** 식단 단건 조회 */
	@GetMapping("/{id}")
	public MealResponseDto getMeal(
			@AuthenticationPrincipal CustomUserPrincipal principal,
			@PathVariable("id") Long id) {
		return mealService.getMeal(id);
	}

	/** 식단 생성 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public MealResponseDto createMeal(
			@AuthenticationPrincipal CustomUserPrincipal principal,
			@RequestBody MealRequestDto dto) {
		// diet_log 자동 생성은 MealService의 createMealWithItems에서 처리
		return mealService.createMeal(dto);
	}

	/**
	 * 식단 + 항목을 한 번에 생성
	 * - MealItem의 영양 정보를 합산해 Meal의 총 영양 정보에 반영
	 * - 첫 번째 MealItem의 이름을 Meal의 대표 메뉴명으로 사용
	 * - diet_log 자동 생성
	 */
	@PostMapping("/with-items")
	@ResponseStatus(HttpStatus.CREATED)
	public MealResponseDto createMealWithItems(
			@AuthenticationPrincipal CustomUserPrincipal principal,
			@RequestBody MealWithItemsRequestDto dto) {
		return mealService.createMealWithItems(principal.getMemberId(), dto);
	}

	/** 식단 수정 */
	@PutMapping("/{id}")
	public MealResponseDto updateMeal(
			@AuthenticationPrincipal CustomUserPrincipal principal,
			@PathVariable("id") Long id,
			@RequestBody MealRequestDto dto) {
		return mealService.updateMeal(id, dto);
	}

	/** 식단 삭제 */
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteMeal(
			@AuthenticationPrincipal CustomUserPrincipal principal,
			@PathVariable("id") Long id) {
		mealService.deleteMeal(id);
	}

//	/** 추천 식단 생성 (로그인 사용자 기준) */
//	@PostMapping("/recommend")
//	@ResponseStatus(HttpStatus.CREATED)
//	public MealRecommendResponseDto recommendMeal(
//			@AuthenticationPrincipal CustomUserPrincipal principal,
//			@RequestBody MealRecommendRequestDto dto) {
//		return mealPlanService.createRecommendedMeal(principal.getMemberId(), dto);
//	}
}
