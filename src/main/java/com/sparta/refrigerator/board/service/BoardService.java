package com.sparta.refrigerator.board.service;

import com.sparta.refrigerator.auth.entity.User;
import com.sparta.refrigerator.auth.enumeration.UserAuth;
import com.sparta.refrigerator.auth.repository.UserRepository;
import com.sparta.refrigerator.board.dto.BoardRequestDTO;
import com.sparta.refrigerator.board.dto.BoardResponseDTO;
import com.sparta.refrigerator.board.dto.InvitationRequestDTO;
import com.sparta.refrigerator.board.entity.Board;
import com.sparta.refrigerator.board.entity.Invitation;
import com.sparta.refrigerator.board.repository.BoardRepository;
import com.sparta.refrigerator.board.repository.InvitationRepository;
import com.sparta.refrigerator.common.exception.DataNotFoundException;
import com.sparta.refrigerator.common.exception.ForbiddenException;
import com.sparta.refrigerator.common.exception.ViolatedException;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
        if (user.getAuth() == UserAuth.MANAGER) {
            Board board = new Board(requestDTO, user);
            boardRepository.save(board);
            Invitation invitation = new Invitation(board, user);
            invitationRepository.save(invitation);
            return new BoardResponseDTO(board);
        } else {
            throw new ForbiddenException("권한에 맞지 않은 사용자는 요청을 진행할 수 없습니다.");
        }
    }

    //Board 수정
    @Transactional
    public BoardResponseDTO updateBoard(Long boardId, BoardRequestDTO requestDTO, User user) {
        Board board = findById(boardId);

        userRepository.findById(user.getId()).orElseThrow(
            () -> new DataNotFoundException("선택한 유저를 찾을 수 없습니다."));

        checkIfManagerOfBoard(board, user);

        board.update(requestDTO);
        boardRepository.save(board);
        return new BoardResponseDTO(board);
    }

    //Board 삭제
    @Transactional
    public void deleteBoard(Long boardId, User user) {
        Board board = findById(boardId);

        userRepository.findById(user.getId()).orElseThrow(
            () -> new DataNotFoundException("선택한 유저를 찾을 수 없습니다."));

        checkIfManagerOfBoard(board, user);

        boardRepository.delete(board);
    }

    //Board 초대
    @Transactional
    public void inviteBoard(Long boardId, User user, InvitationRequestDTO requestDTO) {
        Board board = findById(boardId);

        User invitee = userRepository.findByUserName(requestDTO.getUserName()).orElseThrow(
            () -> new DataNotFoundException("초대할 사용자가 없습니다."));

        checkIfManagerOfBoard(board, user);

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
    public List<BoardResponseDTO> viewAllBoard() {
        List<Board> boardList = boardRepository.findAllByOrderByCreatedAtDesc();

        if (boardList.isEmpty()) {
            throw new DataNotFoundException("먼저 작성하여 소식을 알려보세요!");
        }

        return boardList.stream()
            .map(BoardResponseDTO::new)
            .toList();
    }

    @Transactional(readOnly = true)
    public Board findById(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(
            () -> new DataNotFoundException("선택한 게시물이 없습니다.")
        );
    }

    private void checkIfManagerOfBoard(Board board, User user) {
        boolean isManagerOfBoard = invitationRepository.existsByBoardAndUser(board, user);

        if (!isManagerOfBoard) {
            throw new ForbiddenException("권한에 맞지 않은 사용자는 요청을 진행할 수 없습니다.");
        }

    }

}