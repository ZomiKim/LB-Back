package com.study.lastlayer.member;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.study.lastlayer.exception.BadRequestException;
import com.study.lastlayer.file.File;
import com.study.lastlayer.file.FileRepository;
import com.study.lastlayer.fileupload.FileUploadService;

record MemberUpdateDto(
		// Member 정보
		String name, String phone, String gender, String birthday, // "yyyy-MM-dd"
		Float height, Float weight, Integer target_date, String goal, Float goal_weight, String allergies,
		String special_notes) {
}

@Service
@Transactional
public class MemberService {
	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private FileUploadService fileUploadService; // 기존 서비스 주입

	@Autowired
	private FileRepository fileRepository;

	public Member updateMember(Long id, MemberUpdateDto dto) {
		// 1. 회원 조회
		Member member = memberRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다. id=" + id));

		// 2. 데이터 업데이트
		member.setName(dto.name());
		member.setPhone(dto.phone());
		member.setGender(dto.gender());
		member.setBirthday(LocalDate.parse(dto.birthday()));
		member.setHeight(dto.height());
		member.setWeight(dto.weight());
		member.setTarget_date(dto.target_date());
		member.setGoal(dto.goal());
		member.setGoal_weight(dto.goal_weight());
		member.setAllergies(dto.allergies());
		member.setSpecial_notes(dto.special_notes());

		// 3. 변경된 신체 정보를 바탕으로 하루 권장 칼로리 재계산 (엔티티 내 메서드 활용)
		member.updateDailyCalories();

		// Dirty Checking(변경 감지)에 의해 별도의 save 없이도 업데이트되지만 명시적으로 리턴 가능
		return member;
	}

	@Transactional(readOnly = true) // 읽기 전용이어도 트랜잭션 내에서 처리하는 것이 권장됩니다.
	public Member getMember(Long memberId) {
		return memberRepository.findById(memberId)
				.orElseThrow(() -> new BadRequestException("해당 회원이 존재하지 않습니다. id=" + memberId));
	}

	@Transactional(readOnly = true)
	public File getProfileImage(Long memberId) {
		Member member = getMember(memberId);
		return member.getProfileImage();
	}

	public File updateProfileImage(Long memberId, MultipartFile file) throws IOException {
		Member member = getMember(memberId);

		// 단일 파일 업로드 메서드 호출 가능
		File savedFile = fileUploadService.fileCreateOne(file, 32, 32);

		if (savedFile != null) {
			member.setProfileImage(savedFile);
		}
		return savedFile;
	}

	public void deleteProfileImage(Long memberId) {
		Member member = getMember(memberId);

		File profileImage = member.getProfileImage();

		if (profileImage != null) {
			// 1. DB에서 사용자와 파일의 연관 관계 끊기
			member.setProfileImage(null);

			// 2. 물리적 파일 삭제 로직 (선택 사항이나 권장)
			fileUploadService.deletePhysicalFile(profileImage.getFilename());

			// 3. DB에서 File 레코드 삭제
			fileRepository.delete(profileImage);
		}
	}
}
