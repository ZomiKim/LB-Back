package com.study.lastlayer.meal.mealplan;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.lastlayer.exception.BadRequestException;
import com.study.lastlayer.meal.Meal;
import com.study.lastlayer.meal.MealRecommendItemDto;
import com.study.lastlayer.meal.MealRecommendRequestDto;
import com.study.lastlayer.meal.MealRecommendResponseDto;
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
	private final MemberRepository memberRepository;
	private final MealService mealService;
	private final DietLogService dietLogService;

	/**
	 * 추천 식단 생성
	 * <p>
	 * 1) Meal 생성<br>
	 * 2) MealItem 여러 개 생성<br>
	 * 3) MealPlan 생성 (isAccepted=false)
	 */
	public MealRecommendResponseDto createRecommendedMeal(Long memberId, MealRecommendRequestDto dto) {

		if (memberId == null) {
			throw new IllegalArgumentException("memberId는 필수입니다.");
		}
		if (dto.getMealType() == null || dto.getMealType().isEmpty()) {
			throw new IllegalArgumentException("mealType은 필수입니다.");
		}

		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new BadRequestException("회원이 존재하지 않습니다. id=" + memberId));

		// 1) Meal 생성
		Meal meal = mealService.createMealFromRecommend(dto);

		// 2) MealItem 생성
		if (dto.getItems() != null && !dto.getItems().isEmpty()) {
			for (MealRecommendItemDto itemDto : dto.getItems()) {
				MealItem item = new MealItem();
				item.setMeal(meal);
				item.setName(itemDto.getName());
				item.setAmount(itemDto.getAmount() != null ? itemDto.getAmount() : 0);
				item.setCarbohydrate(itemDto.getCarbohydrate() != null ? itemDto.getCarbohydrate() : 0);
				item.setProtein(itemDto.getProtein() != null ? itemDto.getProtein() : 0);
				item.setFat(itemDto.getFat() != null ? itemDto.getFat() : 0);
				item.setCalories(itemDto.getCalories() != null ? itemDto.getCalories() : 0);
				mealItemRepository.save(item);
			}
		}

		// 3) MealPlan 생성
		MealPlan mealPlan = new MealPlan();
		mealPlan.setMember(member);
		mealPlan.setMeal(meal);

		LocalDate dateAt = dto.getDateAt() != null ? dto.getDateAt() : LocalDate.now();
		mealPlan.setDateAt(dateAt);
		mealPlan.setIsAccepted(false);

		MealPlan savedPlan = mealPlanRepository.save(mealPlan);

		// 응답 DTO 구성
		List<MealRecommendItemDto> items = dto.getItems();
		MealResponseDto mealResponse = MealResponseDto.fromEntity(meal);

		return MealRecommendResponseDto.builder()
				.mealPlanId(savedPlan.getId())
				.meal(mealResponse)
				.items(items)
				.build();
	}

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
}

