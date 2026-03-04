package com.study.lastlayer.application;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.lastlayer.club.Club;
import com.study.lastlayer.member.Member;


public interface ApplicationRepository extends JpaRepository<Application, Long> {

	//신청
    Optional<Application> findByMemberAndClub(Member member, Club club);

    
    //가입신청 리스트 조회
	List<Application> findByClubAndStatus(Club club, ApplicationStatus pending);

}


