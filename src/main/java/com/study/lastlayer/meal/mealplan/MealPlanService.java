package com.study.lastlayer.meal.mealplan;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.lastlayer.exception.BadRequestException;
import com.study.lastlayer.meal.Meal;
import com.study.lastlayer.meal.MealRepository;
import com.study.lastlayer.meal.MealResponseDto;
import com.study.lastlayer.meal.MealService;
import com.study.lastlayer.meal.dietlog.DietLogRequestDto;
import com.study.lastlayer.meal.dietlog.DietLogResponseDto;
import com.study.lastlayer.meal.dietlog.DietLogService;
import com.study.lastlayer.meal.mealitem.MealItem;
import com.study.lastlayer.meal.mealitem.MealItemRepository;
import com.study.lastlayer.member.Member;
import com.study.lastlayer.member.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MealPlanService {

	private final MealPlanRepository mealPlanRepository;
	private final MealItemRepository mealItemRepository;
	private final MealRepository mealRepository;
	private final MemberRepository memberRepository;
	private final MealService mealService;
	private final DietLogService dietLogService;
	private final ObjectMapper objectMapper;

	/**
	 * 추천 식단(MealPlan)을 실제로 '먹겠습니다'로 채택할 때,
	 * - meal_plan.isAccepted = true 로 변경
	 * - 해당 회원 + meal + dateAt 기준으로 diet_log 1건 자동 생성
	 */
	public DietLogResponseDto acceptMealPlan(Long memberId, Long mealPlanId) {
		if (memberId == null) {
			throw new IllegalArgumentException("memberId는 필수입니다.");
		}

		MealPlan plan = mealPlanRepository.findById(mealPlanId)
				.orElseThrow(() -> new BadRequestException("추천 식단이 존재하지 않습니다. id=" + mealPlanId));

		if (!plan.getMember().getMember_id().equals(memberId)) {
			throw new BadRequestException("본인의 추천 식단만 채택할 수 있습니다.");
		}

		// 이미 채택된 경우 중복 생성 방지
		if (Boolean.TRUE.equals(plan.getIsAccepted())) {
			throw new BadRequestException("이미 채택된 추천 식단입니다.");
		}

		// 1) 채택 처리
		plan.setIsAccepted(true);

		// 2) diet_log 생성 (plan.dateAt이 있으면 그 날짜 기준, 없으면 현재 시각)
		LocalDateTime logDateTime = null;
		if (plan.getDateAt() != null) {
			logDateTime = plan.getDateAt().atStartOfDay();
		}
		return dietLogService.create(memberId, plan.getMeal().getId(), logDateTime);
	}

	/**
	 * Python 추천 결과(JSON 파싱 후 메뉴 1개)을 기반으로
	 * - Meal 1건
	 * - MealItem 1건
	 * - MealPlan 1건을 생성합니다.
	 *
	 * 기존 MealRecommend* DTO를 거치지 않고 바로 MealPlan을 만드는 용도입니다.
	 *
	 * @param memberId 추천 대상 회원 ID
	 * @param item     추천 메뉴 1개 (예: 아침/점심/저녁 중 하나)
	 * @param mealType 식사 유형 (B/L/D/S)
	 * @param dateAt   적용 날짜 (null이면 오늘)
	 * @return 생성된 MealPlan
	 */
	public MealPlan createRecommendedMealFromPython(Long memberId,
			PythonRecommendedMenu item,
			char mealType,
			LocalDate dateAt) {

		if (memberId == null) {
			throw new IllegalArgumentException("memberId는 필수입니다.");
		}
		if (mealType == 0) {
			throw new IllegalArgumentException("mealType은 필수입니다.");
		}
		if (item == null) {
			throw new IllegalArgumentException("추천 메뉴가 비어 있습니다.");
		}

		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new BadRequestException("회원이 존재하지 않습니다. id=" + memberId));

		// 1) Meal 생성
		Meal meal = new Meal();
		meal.setMealType(mealType);
		meal.setMenu(item.getName() != null ? item.getName() : "");
		meal.setTotalCalories(item.getCalories() != null ? item.getCalories() : 0);
		meal.setCarbohydrate(0);
		meal.setProtein(0);
		meal.setFat(0);
		meal.setComment(item.getDescription() != null ? item.getDescription() : "");

		Meal savedMeal = mealService.saveMeal(meal);

		// 2) MealItem 생성 (추천 메뉴 1개를 하나의 MealItem으로 저장)
		MealItem mealItem = new MealItem();
		mealItem.setMeal(savedMeal);
		mealItem.setName(item.getName() != null ? item.getName() : "");
		mealItem.setAmount(0);
		mealItem.setCarbohydrate(0);
		mealItem.setProtein(0);
		mealItem.setFat(0);
		mealItem.setCalories(item.getCalories() != null ? item.getCalories() : 0);
		mealItemRepository.save(mealItem);

		// 3) MealPlan 생성
		MealPlan mealPlan = new MealPlan();
		mealPlan.setMember(member);
		mealPlan.setMeal(savedMeal);
		mealPlan.setDateAt(dateAt != null ? dateAt : LocalDate.now());
		mealPlan.setIsAccepted(false);

		return mealPlanRepository.save(mealPlan);
	}

	/**
	 * FastAPI에서 받은 추천 결과 원본 JSON을 파싱하여
	 * 아침/점심/저녁 메뉴를 meal/meal_item/meal_plan에 저장합니다.
	 *
	 * 기대 JSON(예시):
	 * {
	 *   "breakfast": {"name": "...", "description": "...", "calories": 123, ...},
	 *   "lunch": {...},
	 *   "dinner": {...}
	 * }
	 */
	public void createRecommendedMealsFromPythonJson(Long memberId, String pythonJson, LocalDate dateAt) {
		if (memberId == null) {
			throw new IllegalArgumentException("memberId는 필수입니다.");
		}
		if (dateAt == null) {
			dateAt = LocalDate.now();
		}
		if (pythonJson == null || pythonJson.isBlank()) {
			throw new BadRequestException("추천 결과(JSON)가 비어 있습니다.");
		}

		JsonNode root;
		try {
			root = objectMapper.readTree(pythonJson);
		} catch (Exception e) {
			throw new BadRequestException("추천 결과(JSON) 파싱에 실패했습니다: " + e.getMessage());
		}

		saveIfPresent(memberId, root.get("breakfast"), 'B', dateAt);
		saveIfPresent(memberId, root.get("lunch"), 'L', dateAt);
		saveIfPresent(memberId, root.get("dinner"), 'D', dateAt);
	}

	private void saveIfPresent(Long memberId, JsonNode node, char mealType, LocalDate dateAt) {
		if (node == null || node.isNull() || node.isMissingNode()) {
			return;
		}
		String name = textOrEmpty(node.get("name"));
		String description = textOrEmpty(node.get("description"));
		Integer calories = intOrNull(node.get("calories"));

		// 이름이 없으면 저장 가치가 없으므로 스킵 (파이썬 응답이 불완전한 경우 안전 처리)
		if (name.isBlank()) {
			return;
		}
		createRecommendedMealFromPython(memberId, new PythonRecommendedMenu(name, description, calories), mealType, dateAt);
	}

	private static String textOrEmpty(JsonNode node) {
		return (node == null || node.isNull()) ? "" : node.asText("");
	}

	private static Integer intOrNull(JsonNode node) {
		if (node == null || node.isNull() || node.isMissingNode()) return null;
		if (node.isNumber()) return node.asInt();
		String text = node.asText(null);
		if (text == null || text.isBlank()) return null;
		try {
			return Integer.parseInt(text.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * FastAPI 추천 메뉴 최소 필드만 보관하는 내부 DTO.
	 * (externapi 패키지의 Payload 클래스를 없애고도 MealPlan 저장을 유지하기 위함)
	 */
	public static class PythonRecommendedMenu {
		private final String name;
		private final String description;
		private final Integer calories;

		public PythonRecommendedMenu(String name, String description, Integer calories) {
			this.name = name;
			this.description = description;
			this.calories = calories;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public Integer getCalories() {
			return calories;
		}
	}

	/**
	 * 특정 회원의 특정 날짜에 해당하는 추천 식단 목록을 조회합니다.
	 * (오늘 날짜 조회 시 GET /meal-plans/today 에서 사용)
	 */
	@Transactional(readOnly = true)
	public List<MealPlanResponseDto> getMealPlansByDate(Long memberId, LocalDate dateAt) {
		if (memberId == null) {
			throw new IllegalArgumentException("memberId는 필수입니다.");
		}
		if (dateAt == null) {
			dateAt = LocalDate.now();
		}
		return mealPlanRepository.findByMemberIdAndDateAtOrderById(memberId, dateAt).stream()
				.map(MealPlanResponseDto::fromEntity)
				.collect(Collectors.toList());
	}

	/**
	 * 특정 회원의 오늘 날짜 추천 식단을 모두 삭제합니다.
	 * - MealPlan 삭제 후, 해당 Meal에 연결된 MealItem 삭제, 마지막으로 Meal 삭제.
	 * - 이미 채택(accepted)된 식단이 있으면 예외 발생(삭제 불가).
	 */
	public void deleteMealPlansForDate(Long memberId, LocalDate dateAt) {
		if (memberId == null) {
			throw new IllegalArgumentException("memberId는 필수입니다.");
		}
		if (dateAt == null) {
			dateAt = LocalDate.now();
		}
		List<MealPlan> plans = mealPlanRepository.findByMemberIdAndDateAtOrderById(memberId, dateAt);
		for (MealPlan plan : plans) {
			if (Boolean.TRUE.equals(plan.getIsAccepted())) {
				throw new BadRequestException("이미 채택된 추천 식단이 있어 전체 다시 받기를 할 수 없습니다. 날짜: " + dateAt);
			}
		}
		List<Long> mealIds = plans.stream()
				.map(p -> p.getMeal().getId())
				.distinct()
				.toList();
		mealPlanRepository.deleteAll(plans);
		for (Long mealId : mealIds) {
			mealItemRepository.deleteByMealId(mealId);
			mealRepository.deleteById(mealId);
		}
	}
}

