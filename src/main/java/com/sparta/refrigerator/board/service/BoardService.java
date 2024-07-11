package com.sparta.refrigerator.board.service;

import com.sparta.refrigerator.board.dto.BoardRequestDTO;
import com.sparta.refrigerator.board.dto.BoardResponseDTO;
import com.sparta.refrigerator.board.entity.Board;
import com.sparta.refrigerator.board.repository.BoardRepository;
import com.sparta.refrigerator.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    //Board 생성
    public BoardResponseDTO createBoard(BoardRequestDTO requestDTO) {
        Board board = new Board(requestDTO);
        boardRepository.save(board);
        return new BoardResponseDTO(board);
    }

    //Board 수정
    @Transactional
    public BoardResponseDTO updateBoard(Long boardId, BoardRequestDTO requestDTO) {
        Board board = boardRepository.findById(boardId).orElseThrow(
            () -> new DataNotFoundException("선택한 게시물이 없습니다."));

        return new BoardResponseDTO(board);

    }
}
