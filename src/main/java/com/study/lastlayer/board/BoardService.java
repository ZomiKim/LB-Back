package com.study.lastlayer.board;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import com.study.lastlayer.club.Club;
import com.study.lastlayer.club.ClubRepository;
import com.study.lastlayer.file.File;
import com.study.lastlayer.file.FileRepository;
import com.study.lastlayer.member.Member;
import com.study.lastlayer.member.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;
    private final FileRepository fileRepository;
    

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

 // 게시글 단일 조회
    public BoardDto getBoardDetail(Long boardId) {
        return boardRepository.findBoardDetailById(boardId)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));
    }


    //작성
    @Transactional
    public BoardDto createBoard(BoardCreateDto dto, Member member) throws Exception { // throws Exception 추가
        // 클럽 확인
        Club club = clubRepository.findById(dto.getClubId())
                .orElseThrow(() -> new RuntimeException("클럽이 존재하지 않습니다."));

        // 파일 처리
        File fileEntity = null;
        if (dto.getFile() != null && !dto.getFile().isEmpty()) {
            MultipartFile uploadedFile = dto.getFile();
            String storedFilename = UUID.randomUUID() + "_" + uploadedFile.getOriginalFilename();

            // 실제 서버 폴더에 저장
            String uploadDir = "C:/lastlayer/upload/"; // 필요 시 환경설정으로 변경 가능
            Path savePath = Paths.get(uploadDir + storedFilename);
            Files.createDirectories(savePath.getParent());
            uploadedFile.transferTo(savePath.toFile());

            // DB에 파일 정보 저장
            fileEntity = File.builder()
                    .filename(storedFilename)
                    .org_filename(uploadedFile.getOriginalFilename())
                    .build();
            fileRepository.save(fileEntity);
        }

        // 게시글 생성
        Board board = Board.builder()
                .club(club)
                .member(member)
                .title(dto.getTitle())
                .contents(dto.getContents())
                .board_type(dto.getBoardType())
                .file(fileEntity)
                .build();

        boardRepository.save(board);

        // DTO 반환
        return new BoardDto(
                board.getId(),
                board.getBoard_type(),
                board.getContents(),
                board.getCreatedAt(),
                board.getDeletedAt(),
                board.getLike_count(),
                board.getTitle(),
                board.getUpdatedAt(),
                board.getView_count(),
                club.getId(),
                fileEntity != null ? fileEntity.getId() : null,      // file_id 반환
                fileEntity != null ? fileEntity.getFilename() : null, // filename 반환
                member.getMember_id(),
                dto.getMemberName() != null ? dto.getMemberName() : member.getName(),
                member.getProfileImage() != null ? member.getProfileImage().getFilename() : null
        );
    }

    
  
 
    
	


}