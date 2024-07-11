package com.sparta.refrigerator.board.controller;

import com.sparta.refrigerator.board.dto.BoardRequestDTO;
import com.sparta.refrigerator.board.dto.BoardResponseDTO;
import com.sparta.refrigerator.board.service.BoardService;
import com.sparta.refrigerator.common.response.DataCommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    //Board 생성
    @PostMapping("/admin/board")
    public ResponseEntity<DataCommonResponse<BoardResponseDTO>> createBoard(
        @RequestBody @Valid BoardRequestDTO requestDTO) {
        BoardResponseDTO board = boardService.createBoard(requestDTO);
        DataCommonResponse<BoardResponseDTO> response = new DataCommonResponse<>(201, "보드 작성됬었습니다.",
            board);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
