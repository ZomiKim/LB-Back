package com.study.lastlayer.meal.mealplan;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.study.lastlayer.auth.CustomUserPrincipal;
import com.study.lastlayer.meal.dietlog.DietLogResponseDto;

import lombok.RequiredArgsConstructor;

/**
 * MealPlan 관련 API
 * - 추천 식단에 대해 유저가 '먹겠습니다'를 눌렀을 때 diet_log를 생성
 */
@RestController
@RequestMapping("/meal-plans")
@RequiredArgsConstructor
public class MealPlanController {

	private final MealPlanService mealPlanService;

	/**
	 * 추천 식단 채택 ('먹겠습니다')
	 * - 로그인한 유저 기준으로만 가능
	 * - meal_plan.isAccepted = true
	 * - 해당 회원 + meal + dateAt 기준으로 diet_log 1건 생성
	 */
	@PostMapping("/{id}/accept")
	@ResponseStatus(HttpStatus.CREATED)
	public DietLogResponseDto acceptMealPlan(
			@AuthenticationPrincipal CustomUserPrincipal principal,
			@PathVariable("id") Long id) {
		return mealPlanService.acceptMealPlan(principal.getMemberId(), id);
	}
}

