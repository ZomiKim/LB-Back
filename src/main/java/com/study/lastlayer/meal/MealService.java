package com.study.lastlayer.meal;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.lastlayer.exception.BadRequestException;
import com.study.lastlayer.file.File;
import com.study.lastlayer.file.FileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MealService {

	private final MealRepository mealRepository;
	private final FileRepository fileRepository;

	/** 식단 목록 조회 */
	@Transactional(readOnly = true)
	public List<MealResponseDto> getAllMeals() {
		return mealRepository.findAll().stream()
				.map(MealResponseDto::fromEntity)
				.toList();
	}

	/** 식단 단건 조회 */
	@Transactional(readOnly = true)
	public MealResponseDto getMeal(Long id) {
		Meal meal = mealRepository.findById(id)
				.orElseThrow(() -> new BadRequestException("식단이 존재하지 않습니다. id=" + id));
		return MealResponseDto.fromEntity(meal);
	}

	/** 식단 생성 */
	public MealResponseDto createMeal(MealRequestDto dto) {
		if (dto.getMealType() == null || dto.getMealType().isEmpty()) {
			throw new IllegalArgumentException("식사 유형(mealType)은 필수입니다.");
		}
		Meal meal = new Meal();
		meal.setMealType(dto.getMealType().charAt(0));
		meal.setMenu(dto.getMenu() != null ? dto.getMenu() : "");
		meal.setTotalCalories(dto.getTotalCalories() != null ? dto.getTotalCalories() : 0);
		meal.setCarbohydrate(dto.getCarbohydrate() != null ? dto.getCarbohydrate() : 0);
		meal.setFat(dto.getFat() != null ? dto.getFat() : 0);
		meal.setProtein(dto.getProtein() != null ? dto.getProtein() : 0);
		meal.setComment(dto.getComment() != null ? dto.getComment() : "");
		if (dto.getImageFileId() != null) {
			File file = fileRepository.findById(dto.getImageFileId())
					.orElseThrow(() -> new IllegalArgumentException("이미지 파일이 존재하지 않습니다. id=" + dto.getImageFileId()));
			meal.setImageFile(file);
		}
		Meal saved = mealRepository.save(meal);
		return MealResponseDto.fromEntity(saved);
	}

	/**
	 * 추천/직접 입력 DTO를 기반으로 Meal 엔티티만 생성하는 내부용 메서드.
	 * (MealPlanService에서 재사용)
	 */
	public Meal createMealFromRecommend(MealRecommendRequestDto dto) {
		Meal meal = new Meal();
		meal.setMealType(dto.getMealType().charAt(0));
		meal.setMenu(dto.getMenu() != null ? dto.getMenu() : "");

		// 총 칼로리는 요청에서 직접 받지 않는 경우, 아이템 합산 등의 로직을
		// 이후에 넣을 수 있도록 일단 0으로 두거나, 추후 확장 가능하게 둔다.
		meal.setTotalCalories(0);

		meal.setCarbohydrate(0);
		meal.setFat(0);
		meal.setProtein(0);
		meal.setComment("");

		return mealRepository.save(meal);
	}

	/** 식단 수정 */
	public MealResponseDto updateMeal(Long id, MealRequestDto dto) {
		Meal meal = mealRepository.findById(id)
				.orElseThrow(() -> new BadRequestException("식단이 존재하지 않습니다. id=" + id));
		applyDtoToEntity(dto, meal);
		Meal saved = mealRepository.save(meal);
		return MealResponseDto.fromEntity(saved);
	}

	/** 식단 삭제 */
	public void deleteMeal(Long id) {
		if (!mealRepository.existsById(id)) {
			throw new BadRequestException("식단이 존재하지 않습니다. id=" + id);
		}
		mealRepository.deleteById(id);
	}

	private void applyDtoToEntity(MealRequestDto dto, Meal meal) {
		if (dto.getMealType() != null && !dto.getMealType().isEmpty()) {
			meal.setMealType(dto.getMealType().charAt(0));
		}
		if (dto.getMenu() != null) {
			meal.setMenu(dto.getMenu());
		}
		if (dto.getTotalCalories() != null) {
			meal.setTotalCalories(dto.getTotalCalories());
		}
		if (dto.getImageFileId() != null) {
			File file = fileRepository.findById(dto.getImageFileId())
					.orElseThrow(() -> new IllegalArgumentException("이미지 파일이 존재하지 않습니다. id=" + dto.getImageFileId()));
			meal.setImageFile(file);
		} else {
			meal.setImageFile(null);
		}
		if (dto.getCarbohydrate() != null) {
			meal.setCarbohydrate(dto.getCarbohydrate());
		}
		if (dto.getFat() != null) {
			meal.setFat(dto.getFat());
		}
		if (dto.getProtein() != null) {
			meal.setProtein(dto.getProtein());
		}
		if (dto.getComment() != null) {
			meal.setComment(dto.getComment());
		}
	}
}
