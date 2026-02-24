package com.study.lastlayer.meal.mealplan;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.lastlayer.exception.NotFoundException;
import com.study.lastlayer.meal.Meal;
import com.study.lastlayer.meal.MealRecommendItemDto;
import com.study.lastlayer.meal.MealRecommendRequestDto;
import com.study.lastlayer.meal.MealRecommendResponseDto;
import com.study.lastlayer.meal.MealResponseDto;
import com.study.lastlayer.meal.MealService;
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

	/**
	 * 추천 식단 생성
	 * <p>
	 * 1) Meal 생성<br>
	 * 2) MealItem 여러 개 생성<br>
	 * 3) MealPlan 생성 (isAccepted=false)
	 */
	public MealRecommendResponseDto createRecommendedMeal(MealRecommendRequestDto dto) {

		if (dto.getMemberId() == null) {
			throw new IllegalArgumentException("memberId는 필수입니다.");
		}
		if (dto.getMealType() == null || dto.getMealType().isEmpty()) {
			throw new IllegalArgumentException("mealType은 필수입니다.");
		}

		Member member = memberRepository.findById(dto.getMemberId())
				.orElseThrow(() -> new NotFoundException("회원이 존재하지 않습니다. id=" + dto.getMemberId()));

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
}

