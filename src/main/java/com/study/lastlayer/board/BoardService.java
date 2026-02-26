package com.study.lastlayer.board;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    //전체
    public List<BoardDto> getBoardList() {
        return boardRepository.findBoardListWithMember();
    }

    
    //클럽별 게시글
    public List<BoardDto> getBoardListByClubId(Long clubId) {
        return boardRepository.findBoardListByClubId(clubId);
    }

//클럽별 공지사항
    public List<BoardDto> getNoticeBoardListByClubId(Long clubId) {
        return boardRepository.findNoticeBoardsByClubId(clubId);
    }

 // 클럽별 일반 게시글 조회
    public List<BoardDto> getNormalBoardListByClubId(Long clubId) {
        return boardRepository.findNormalBoardsByClubId(clubId);
    }

}