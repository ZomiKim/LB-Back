package com.study.lastlayer.clubmember;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClubMemberRepository extends JpaRepository<ClubMember, Long> {

    @Query("SELECT cm FROM ClubMember cm WHERE cm.member.member_id = :memberId")
    List<ClubMember> findByMemberId(@Param("memberId") Long memberId);
}