package com.study.lastlayer.board;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BoardRepository extends JpaRepository<Board, Long> {

	@Query("""
		    SELECT new com.study.lastlayer.board.BoardDto(
		        b.id,
		        b.board_type,
		        b.contents,
		        b.createdAt,
		        b.deletedAt,
		        b.like_count,
		        b.title,
		        b.updatedAt,
		        b.view_count,
		        b.club.id,
		        bf.id,
		        b.member.member_id,
		        m.name,
		        pf.filename
		    )
		    FROM Board b
		    JOIN b.member m
		    LEFT JOIN b.file bf
		    LEFT JOIN m.profileImage pf
		    LEFT JOIN ClubMember cm
		        ON cm.member = m AND cm.club = b.club
		    WHERE b.deletedAt IS NULL
		    ORDER BY b.createdAt DESC
		""")
		List<BoardDto> findBoardListWithMember();
}
