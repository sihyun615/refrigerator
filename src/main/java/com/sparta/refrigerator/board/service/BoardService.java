package com.sparta.refrigerator.board.service;

import com.sparta.refrigerator.board.dto.BoardRequestDTO;
import com.sparta.refrigerator.board.dto.BoardResponseDTO;
import com.sparta.refrigerator.board.dto.InvitationRequestDTO;
import com.sparta.refrigerator.board.entity.Board;
import com.sparta.refrigerator.board.entity.Invitation;
import com.sparta.refrigerator.board.repository.BoardRepository;
import com.sparta.refrigerator.board.repository.InvitationRepository;
import com.sparta.refrigerator.exception.DataNotFoundException;
import com.sparta.refrigerator.exception.ForbiddenException;
import com.sparta.refrigerator.exception.ViolatedException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final InvitationRepository invitationRepository;

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
    public BoardResponseDTO updateBoard(Long boardId, BoardRequestDTO requestDTO, User user) {
        Board board = findById(boardId);

        userRepository.findById(user.userId()).orElseThrow(
            () -> new DataNotFoundException("선택한 유저를 찾을 수 없습니다."));

        if (!board.getUser().getRole().equals(user.getRole())) {
            throw new ForbiddenException("권한에 맞지 않은 사용자는 요청을 진행할 수 없습니다.");
        }
        board.update(requestDTO);
        boardRepository.save(board);
        return new BoardResponseDTO(board);
    }

    //Board 삭제
    @Transactional
    public void deleteBoard(Long boardId, User user) {
        Board board = findById(boardId);

        userRepository.findById(user.userId()).orElseThrow(
            () -> new DataNotFoundException("선택한 유저를 찾을 수 없습니다."));

        if (!board.getUser().getRole().equals(user.getRole())) {
            throw new ForbiddenException("권한에 맞지 않은 사용자는 요청을 진행할 수 없습니다.");
        }
        boardRepository.delete(board);
    }

    //Board 초대
    @Transactional
    public void inviteBoard(Long boardId, User user, InvitationRequestDTO requestDTO) {
        Board board = findById(boardId);

        if (!user.getRole().equals(User.Role.MANAGER)) {
            throw new ForbiddenException("권한에 맞지 않은 사용자는 요청을 진행할 수 없습니다.");
        }

        User invitee = userRepository.findByUsername(requestDTO.getUserName()).orElseThrow(
            () -> new DataNotFoundException("초대할 사용자가 없습니다."));

        boolean isAlreadyInvited = invitationRepository.existsByBoardAndUser(board, invitee);
        if (isAlreadyInvited) {
            throw new ViolatedException("이미 해당 보드에 초대된 사용자입니다.");
        }

        Invitation invitation = new Invitation(board, invitee);
        invitationRepository.save(invitation);
    }

    //Board 단건 조회
    @Transactional(readOnly = true)
    public BoardResponseDTO viewBoard(Long boardId) {
        return boardRepository.findById(boardId)
            .map(BoardResponseDTO::new)
            .orElseThrow(() -> new DataNotFoundException("선택한 게시물이 없습니다."));

    }

    //Board 전체 조회
    @Transactional(readOnly = true)
    public Page<BoardResponseDTO> viewAllBoard(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Board> boardPage = boardRepository.findAllByOrderByCreatedAtDesc(pageable);

        if (boardPage.isEmpty()) {
            throw new DataNotFoundException("먼저 작성하여 소식을 알려보세요!");
        }

        return boardPage.map(BoardResponseDTO::new);
    }

    @Transactional(readOnly = true)
    public Board findById(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(
            () -> new DataNotFoundException("선택한 게시물이 없습니다.")
        );
    }

}