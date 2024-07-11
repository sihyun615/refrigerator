package com.sparta.refrigerator.board.controller;

import com.sparta.refrigerator.board.dto.BoardRequestDTO;
import com.sparta.refrigerator.board.dto.BoardResponseDTO;
import com.sparta.refrigerator.board.service.BoardService;
import com.sparta.refrigerator.common.response.DataCommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    //Board 생성
    @PostMapping("/admin/board")
    public ResponseEntity<DataCommonResponse<BoardResponseDTO>> createBoard(
        @RequestBody @Valid BoardRequestDTO requestDTO,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BoardResponseDTO board = boardService.createBoard(requestDTO, userDetails);
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
        BoardResponseDTO board = boardService.updateBoard(boardId, requestDTO, userDetails);
        DataCommonResponse<BoardResponseDTO> response = new DataCommonResponse<>(200,
            "보드 수정이 완료 되었습니다.", board);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
