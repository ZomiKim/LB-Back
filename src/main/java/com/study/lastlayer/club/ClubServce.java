package com.study.lastlayer.club;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClubServce {
	
	private final AllClubListReop allClubListReop;
	
	 public List<ClubDto> getAllClubList(){
	        return allClubListReop.findByClubList();
	    }

	 public List<ClubDto> getAllClubListOrderByCreatedAtDesc() {
		 return allClubListReop.findByClubListOrderByCreatedAtDesc();
	 }

	 
//	 네이티브 쿼리 때문에 길어요
	

	    public List<ClubDto> getAllClubListOrderByBoardCountNative() {
	        List<Object[]> results = allClubListReop.findByClubListOrderByBoardCountNative();
	        List<ClubDto> dtoList = new ArrayList<>();

	        for (Object[] row : results) {
	            ClubDto dto = ClubDto.builder()
	                .id(((Number) row[0]).longValue())
	                .description((String) row[1])
	                .keywords((String) row[2])
	                .name((String) row[3])
	                .bgFileId(row[4] != null ? ((Number) row[4]).longValue() : null)
	                .filename((String) row[5])
	                .managerId(((Number) row[6]).longValue())
	                .createdAt(((java.sql.Timestamp) row[7]).toLocalDateTime())
	                .build();

	            dtoList.add(dto);
	        }

	        return dtoList;
	    }

	 

}
