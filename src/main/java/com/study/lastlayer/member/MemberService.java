package com.study.lastlayer.member;

import org.springframework.stereotype.Service;

record MemberUpdateDto(
		// Member 정보
		String name, String phone, String gender, String birthday, // "yyyy-MM-dd"
		Float height, Float weight, Integer target_date, String goal, Float goal_weight, String allergies,
		String special_notes) {
}

@Service
public class MemberService {

}
