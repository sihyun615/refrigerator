package com.sparta.refrigerator.board.controller;

import com.sparta.refrigerator.auth.service.UserDetailsImpl;
import com.sparta.refrigerator.board.dto.BoardRequestDTO;
import com.sparta.refrigerator.board.dto.BoardResponseDTO;
import com.sparta.refrigerator.board.dto.InvitationRequestDTO;
import com.sparta.refrigerator.board.service.BoardService;
import com.sparta.refrigerator.common.response.DataCommonResponse;
import com.sparta.refrigerator.common.response.StatusCommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private static final int PAGE_SIZE = 5;

    private final BoardService boardService;

    //Board 생성
    @PostMapping("/admin/boards")
    public ResponseEntity<DataCommonResponse<BoardResponseDTO>> createBoard(
        @RequestBody @Valid BoardRequestDTO requestDTO,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BoardResponseDTO board = boardService.createBoard(requestDTO, userDetails.getUser());
        DataCommonResponse<BoardResponseDTO> response = new DataCommonResponse<>(201, "보드 작성되었습니다.",
            board);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //Board 수정
    @PutMapping("/admin/boards/{boardId}")
    public ResponseEntity<DataCommonResponse<BoardResponseDTO>> updateBoard(
        @PathVariable(value = "boardId") Long boardId,
        @RequestBody @Valid BoardRequestDTO requestDTO,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BoardResponseDTO board = boardService.updateBoard(boardId, requestDTO,
            userDetails.getUser());
        DataCommonResponse<BoardResponseDTO> response = new DataCommonResponse<>(200,
            "보드 수정이 완료 되었습니다.", board);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //Board 삭제
    @DeleteMapping("/admin/boards/{boardId}")
    public ResponseEntity<StatusCommonResponse> deleteBoard(
        @PathVariable(value = "boardId") Long boardId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        boardService.deleteBoard(boardId, userDetails.getUser());
        StatusCommonResponse response = new StatusCommonResponse(204, "보드 삭제되었습니다.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //Board 초대
    @PostMapping("/admin/boards/{boardId}/invitation")
    public ResponseEntity<DataCommonResponse<String>> inviteBoard(
        @PathVariable(value = "boardId") Long boardId,
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody @Valid InvitationRequestDTO requestDTO) {

        boardService.inviteBoard(boardId, userDetails.getUser(), requestDTO);
        DataCommonResponse<String> response = new DataCommonResponse<>(200, "보드 초대가 완료되었습니다.",
            "초대 성공");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //Board 단건 조회
    @GetMapping("/boards/{boardId}")
    public ResponseEntity<DataCommonResponse<BoardResponseDTO>> viewBoard(
        @PathVariable(value = "boardId") Long boardId) {
        BoardResponseDTO board = boardService.viewBoard(boardId);
        DataCommonResponse<BoardResponseDTO> response = new DataCommonResponse<>(200,
            "보드 단건 조회 성공하였습니다.", board);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //Board 전체 조회
    @GetMapping("/boards")
    public ResponseEntity<DataCommonResponse<Page<BoardResponseDTO>>> viewAllBoard(
        @RequestParam(value = "page", defaultValue = "0") int page) {
        Page<BoardResponseDTO> responseDTOPage = boardService.viewAllBoard(page, PAGE_SIZE);
        DataCommonResponse<Page<BoardResponseDTO>> response = new DataCommonResponse<>(200,
            "보드 전체 조회 성공하였습니다.", responseDTOPage);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
