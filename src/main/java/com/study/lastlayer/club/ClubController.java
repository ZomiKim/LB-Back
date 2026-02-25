package com.study.lastlayer.club;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clubs")

public class ClubController {
	
	
	 private final ClubServce clubServce; 
	
	
	//전체 클럽 리스트
	 @GetMapping("")
	public List<ClubDto> getClubList(){
		return clubServce.getAllClubList();
	}
	 
	
	// 최신순 클럽 리스트
	 @GetMapping("/latest")
	 public List<ClubDto> getLatestClubList(){
	     return clubServce.getAllClubListOrderByCreatedAtDesc();
	 }
	 
	 
	// 게시글 많은 순 클럽 리스트
	 @GetMapping("/mostBoards")
	 public List<ClubDto> getClubListByBoardCount() {
		 return clubServce.getAllClubListOrderByBoardCountNative();
	 }
	 
	
	// 회원 많은 순 클럽 리스트
	 @GetMapping("/mostMember")
	    public List<ClubDto> getClubListByMemberCount() {
	        return clubServce.getAllClubsByMemberCount();
	    }
	 
	 @GetMapping("/search")
	 public List<ClubDto> searchClubs(@RequestParam("keyword") String keyword) {
	     return clubServce.searchClubs(keyword);
	 }
	 

}
