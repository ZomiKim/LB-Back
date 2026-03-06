package com.study.lastlayer.meal.mealplan;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.study.lastlayer.auth.CustomUserPrincipal;
import com.study.lastlayer.externapi.dietrecommendation.DietRecommendationService;
import com.study.lastlayer.meal.dietlog.DietLogResponseDto;
import com.study.lastlayer.member.Member;
import com.study.lastlayer.member.MemberRepository;

import lombok.RequiredArgsConstructor;

/**
 * MealPlan 관련 API
 *
 * 플로우 요약:
 * 1. 로그인 후 "추천받기" → POST /meal-plans/recommend/today
 * 2. 파이썬(FastAPI)이 로그인한 회원 정보를 받아 개인 맞춤 식단 추천
 * 3. 추천 결과는 meal, meal_item, meal_plan 테이블에 저장
 * 4. 유저가 "먹겠습니다(accept)" 시 diet_log에 저장되어 식단 조회 가능
 * 5. 추천받은 식단 조회: GET /meal-plans/today 또는 GET /meal-plans?date=yyyy-MM-dd
 * 6. 메뉴가 마음에 안 들면 "전체 다시 받기" → POST /meal-plans/recommend/today/replace
 */
@RestController
@RequestMapping("/meal-plans")
@RequiredArgsConstructor
public class MealPlanController {

	private final MealPlanService mealPlanService;
	private final DietRecommendationService dietRecommendationService;
	private final MemberRepository memberRepository;

	/**
	 * 추천 식단 채택 ('먹겠습니다')
	 * - 로그인한 유저 기준 (principal에서 memberId 사용, 클라이언트는 memberId 보낼 필요 없음)
	 */
	@PostMapping("/{id}/accept")
	@ResponseStatus(HttpStatus.CREATED)
	public DietLogResponseDto acceptMealPlan(
			@AuthenticationPrincipal CustomUserPrincipal principal,
			@PathVariable("id") Long id) {
		return mealPlanService.acceptMealPlan(principal.getMemberId(), id);
	}

	/**
	 * 오늘 날짜의 내 추천 식단 목록 조회.
	 * POST /meal-plans/recommend/today 호출 후 저장된 결과를 확인할 때 사용합니다.
	 */
	@GetMapping("/today")
	public List<MealPlanResponseDto> getTodayMealPlans(@AuthenticationPrincipal CustomUserPrincipal principal) {
		return mealPlanService.getMealPlansByDate(principal.getMemberId(), LocalDate.now());
	}

	/**
	 * 특정 날짜의 내 추천 식단 목록 조회.
	 * @param date yyyy-MM-dd (생략 시 오늘)
	 */
	@GetMapping
	public List<MealPlanResponseDto> getMealPlansByDate(
			@AuthenticationPrincipal CustomUserPrincipal principal,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		return mealPlanService.getMealPlansByDate(principal.getMemberId(), date);
	}

	/**
	 * 오늘 날짜 기준으로 아침/점심/저녁 추천 식단을 한 번에 생성하는 API.
	 * - 로그인한 사용자 기준 (principal에서 회원 정보 사용, 클라이언트는 memberId 보낼 필요 없음)
	 */
	@PostMapping("/recommend/today")
	@ResponseStatus(HttpStatus.CREATED)
	public void recommendTodayMeals(@AuthenticationPrincipal CustomUserPrincipal principal) {
		Long memberId = principal.getMemberId();
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다. id=" + memberId));

		LocalDate today = LocalDate.now();
		String pythonJson = dietRecommendationService.recommendDailyMeal(member);
		mealPlanService.createRecommendedMealsFromPythonJson(memberId, pythonJson, today);
	}

	/**
	 * 전체 다시 받기: 오늘 날짜의 기존 추천 식단을 삭제한 뒤, 파이썬에서 새로 추천받아 meal/meal_item/meal_plan에 저장합니다.
	 * - 이미 채택(accept)한 식단이 있으면 400 예외 (전체 다시 받기 불가).
	 */
	@PostMapping("/recommend/today/replace")
	@ResponseStatus(HttpStatus.CREATED)
	public void replaceTodayRecommendations(@AuthenticationPrincipal CustomUserPrincipal principal) {
		Long memberId = principal.getMemberId();
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다. id=" + memberId));

		LocalDate today = LocalDate.now();
		mealPlanService.deleteMealPlansForDate(memberId, today);

		String pythonJson = dietRecommendationService.recommendDailyMeal(member);
		mealPlanService.createRecommendedMealsFromPythonJson(memberId, pythonJson, today);
	}
}

