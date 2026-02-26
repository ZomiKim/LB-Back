package com.study.lastlayer.weekhistory;

import java.time.LocalDate;

public class WeekHistoryDto {
	
	public static record WeekHistoryRequest(String date, Double weight) {}
	public static record WeekHistoryUpdateRequest(Double weight) {}
	public static record WeekHistoryResponse(Long id, String date, Double weight, String message) {
		public WeekHistoryResponse(WeekHistory entity, String message) {
			this(entity.getId(), entity.getDate().toString(), entity.getWeight(), message);
		}
	}
	
	public static record WeekHistoryListResponse(Long id, String date, Double weight) {
		public WeekHistoryListResponse(WeekHistory entity) {
			this(entity.getId(), entity.getDate().toString(), entity.getWeight());
		}
	}

}
