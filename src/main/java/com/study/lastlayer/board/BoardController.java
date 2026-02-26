package com.study.lastlayer.board;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;

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
    
}