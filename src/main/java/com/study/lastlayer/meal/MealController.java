package com.study.lastlayer.meal;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.study.lastlayer.meal.mealplan.MealPlanService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/meals")
@RequiredArgsConstructor
public class MealController {

	private final MealService mealService;
	private final MealPlanService mealPlanService;

	/** 식단 목록 조회 */
	@GetMapping
	public List<MealResponseDto> getMeals() {
		return mealService.getAllMeals();
	}

	/** 식단 단건 조회 */
	@GetMapping("/{id}")
	public MealResponseDto getMeal(@PathVariable("id") Long id) {
		return mealService.getMeal(id);
	}

	/** 식단 생성 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public MealResponseDto createMeal(@RequestBody MealRequestDto dto) {
		return mealService.createMeal(dto);
	}

	/** 식단 수정 */
	@PutMapping("/{id}")
	public MealResponseDto updateMeal(@PathVariable("id") Long id, @RequestBody MealRequestDto dto) {
		return mealService.updateMeal(id, dto);
	}

	/** 식단 삭제 */
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteMeal(@PathVariable("id") Long id) {
		mealService.deleteMeal(id);
	}

	/**
	 * 추천 식단 생성
	 *
	 * POST /api/lastlayer/meals/recommend
	 */
	@PostMapping("/recommend")
	@ResponseStatus(HttpStatus.CREATED)
	public MealRecommendResponseDto recommendMeal(@RequestBody MealRecommendRequestDto dto) {
		return mealPlanService.createRecommendedMeal(dto);
	}
}
