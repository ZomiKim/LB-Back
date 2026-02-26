package com.study.lastlayer.board;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.study.lastlayer.auth.CustomUserPrincipal;
import com.study.lastlayer.member.Member;
import com.study.lastlayer.member.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;
    
    @Autowired
	private MemberService memberService;

    //전체 클럽 게시글들 모두
    @GetMapping("")
    public List<BoardDto> getBoardList() {
        return boardService.getBoardList();
    }
    
 // 클럽별 게시글 전체
    @GetMapping("/{clubId}/boards")
    public List<BoardDto> getBoardListByClubId(
            @PathVariable("clubId") Long clubId) {
        return boardService.getBoardListByClubId(clubId);
    }
    
 // 클럽별 공지글 조회
    @GetMapping("/{clubId}/boards/notice")
    public List<BoardDto> getNoticeBoardListByClubId(
            @PathVariable("clubId") Long clubId) {
        return boardService.getNoticeBoardListByClubId(clubId);
    }
    
 // 클럽별 일반 게시글 조회
    @GetMapping("/{clubId}/boards/normal")
    public List<BoardDto> getNormalBoardListByClubId(@PathVariable("clubId") Long clubId) {
        return boardService.getNormalBoardListByClubId(clubId);
    }
    
 // 게시글 단일 조회
    @GetMapping("/{boardId}")
    public BoardDto getBoardDetail(@PathVariable("boardId") Long boardId) {
        return boardService.getBoardDetail(boardId);
    }
    
    // 게시글 작성
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BoardDto> createBoard(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @ModelAttribute BoardCreateDto dto
    ) throws Exception { 
        Member member = memberService.getMember(principal.getMemberId());
        BoardDto created = boardService.createBoard(dto, member);
        return ResponseEntity.ok(created);
    }
   
    
}