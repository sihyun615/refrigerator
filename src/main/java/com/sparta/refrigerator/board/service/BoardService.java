package com.sparta.refrigerator.board.service;

import com.sparta.refrigerator.board.dto.BoardRequestDTO;
import com.sparta.refrigerator.board.dto.BoardResponseDTO;
import com.sparta.refrigerator.board.entity.Board;
import com.sparta.refrigerator.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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


}
