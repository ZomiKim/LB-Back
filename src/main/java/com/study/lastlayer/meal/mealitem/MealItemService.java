package com.study.lastlayer.meal.mealitem;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.lastlayer.exception.BadRequestException;
import com.study.lastlayer.meal.Meal;
import com.study.lastlayer.meal.MealRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MealItemService {

	private final MealItemRepository mealItemRepository;
	private final MealRepository mealRepository;

	/** 특정 식단(meal)에 속한 항목 목록 조회 */
	@Transactional(readOnly = true)
	public List<MealItemResponseDto> getByMealId(Long mealId) {
		return mealItemRepository.findByMealId(mealId).stream()
				.map(MealItemResponseDto::fromEntity)
				.toList();
	}

	/** 식단 항목 단건 조회 */
	@Transactional(readOnly = true)
	public MealItemResponseDto getById(Long id) {
		MealItem item = mealItemRepository.findById(id)
				.orElseThrow(() -> new BadRequestException("식단 항목이 존재하지 않습니다. id=" + id));
		return MealItemResponseDto.fromEntity(item);
	}

	/** 식단 항목 추가 */
	public MealItemResponseDto create(Long mealId, MealItemRequestDto dto) {
		if (dto.getName() == null || dto.getName().isBlank()) {
			throw new IllegalArgumentException("음식명(name)은 필수입니다.");
		}
		if (dto.getAmount() == null || dto.getAmount() < 0) {
			throw new IllegalArgumentException("섭취량(amount)은 0 이상 필수입니다.");
		}
		Meal meal = mealRepository.findById(mealId)
				.orElseThrow(() -> new BadRequestException("식단이 존재하지 않습니다. id=" + mealId));
		MealItem item = new MealItem();
		item.setMeal(meal);
		item.setName(dto.getName().trim());
		item.setAmount(dto.getAmount());
		item.setCarbohydrate(dto.getCarbohydrate() != null ? dto.getCarbohydrate() : 0);
		item.setProtein(dto.getProtein() != null ? dto.getProtein() : 0);
		item.setFat(dto.getFat() != null ? dto.getFat() : 0);
		item.setCalories(dto.getCalories() != null ? dto.getCalories() : 0);
		MealItem saved = mealItemRepository.save(item);
		return MealItemResponseDto.fromEntity(saved);
	}

	/** 식단 항목 수정 */
	public MealItemResponseDto update(Long id, MealItemRequestDto dto) {
		MealItem item = mealItemRepository.findById(id)
				.orElseThrow(() -> new BadRequestException("식단 항목이 존재하지 않습니다. id=" + id));
		applyDtoToEntity(dto, item);
		MealItem saved = mealItemRepository.save(item);
		return MealItemResponseDto.fromEntity(saved);
	}

	/** 식단 항목 삭제 */
	public void delete(Long id) {
		if (!mealItemRepository.existsById(id)) {
			throw new BadRequestException("식단 항목이 존재하지 않습니다. id=" + id);
		}
		mealItemRepository.deleteById(id);
	}

	private void applyDtoToEntity(MealItemRequestDto dto, MealItem item) {
		if (dto.getName() != null) {
			item.setName(dto.getName());
		}
		if (dto.getAmount() != null) {
			item.setAmount(dto.getAmount());
		}
		if (dto.getCarbohydrate() != null) {
			item.setCarbohydrate(dto.getCarbohydrate());
		}
		if (dto.getProtein() != null) {
			item.setProtein(dto.getProtein());
		}
		if (dto.getFat() != null) {
			item.setFat(dto.getFat());
		}
		if (dto.getCalories() != null) {
			item.setCalories(dto.getCalories());
		}
	}
}
