package com.sparta.refrigerator.board.service;

import com.sparta.refrigerator.board.dto.BoardRequestDTO;
import com.sparta.refrigerator.board.dto.BoardResponseDTO;
import com.sparta.refrigerator.board.entity.Board;
import com.sparta.refrigerator.board.repository.BoardRepository;
import com.sparta.refrigerator.exception.DataNotFoundException;
import com.sparta.refrigerator.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    //Board 생성
    public BoardResponseDTO createBoard(BoardRequestDTO requestDTO, User user) {
        if (user.getRole() == User.Role.MANAGER) {
            Board board = new Board(requestDTO);
            boardRepository.save(board);
            return new BoardResponseDTO(board);
        } else {
            throw new ForbiddenException("권한에 맞지 않은 사용자는 요청을 진행할 수 없습니다.");
        }
    }

    //Board 수정
    @Transactional
    public BoardResponseDTO updateBoard(Long boardId, BoardRequestDTO requestDTO) {
        Board board = boardRepository.findById(boardId).orElseThrow(
            () -> new DataNotFoundException("선택한 게시물이 없습니다."));

        board.update(requestDTO.getBoardName(), requestDTO.getBoardInfo());
        boardRepository.save(board);

        return new BoardResponseDTO(board);

    }
}