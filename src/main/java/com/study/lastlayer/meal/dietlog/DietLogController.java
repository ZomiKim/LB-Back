package com.study.lastlayer.meal.dietlog;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/**
 * 식단 기록(DietLog) API — diet_log를 통한 조회 및 등록
 * - 회원별 식단 기록 목록 조회 (기간 필터 optional)
 * - 단건 조회, 기록 추가
 */
@RestController
@RequestMapping("/diet-logs")
@RequiredArgsConstructor
public class DietLogController {

	private final DietLogService dietLogService;

	/** 회원별 식단 기록 목록 조회 (diet_log를 통한 조회). fromDate, toDate 있으면 기간 필터 */
	@GetMapping("/{memberId}")
	public List<DietLogResponseDto> getByMember(
			@PathVariable("memberId") Long memberId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
		return dietLogService.getByMember(memberId, fromDate, toDate);
	}

	/** 식단 기록 단건 조회 */
	@GetMapping("/{memberId}/{id}")
	public DietLogResponseDto getById(@PathVariable("memberId") Long memberId, @PathVariable("id") Long id) {
		return dietLogService.getById(id);
	}

	/** 식단 기록 추가 */
	@PostMapping("/{memberId}")
	@ResponseStatus(HttpStatus.CREATED)
	public DietLogResponseDto create(
			@PathVariable("memberId") Long memberId,
			@RequestBody DietLogRequestDto dto) {
		return dietLogService.create(memberId, dto);
	}
}
