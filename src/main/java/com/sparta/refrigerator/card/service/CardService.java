package com.sparta.refrigerator.card.service;

import com.sparta.refrigerator.auth.entity.User;
import com.sparta.refrigerator.auth.service.UserService;
import com.sparta.refrigerator.board.entity.Board;
import com.sparta.refrigerator.board.repository.InvitationRepository;
import com.sparta.refrigerator.board.service.BoardService;
import com.sparta.refrigerator.card.dto.CardRequestDto;
import com.sparta.refrigerator.card.dto.CardResponseDto;
import com.sparta.refrigerator.card.entity.Card;
import com.sparta.refrigerator.card.repository.CardRepository;
import com.sparta.refrigerator.column.entity.Columns;
import com.sparta.refrigerator.column.repository.ColumnRepository;
import com.sparta.refrigerator.column.service.ColumnService;
import com.sparta.refrigerator.exception.CardNotFoundException;
import com.sparta.refrigerator.exception.DataNotFoundException;
import com.sparta.refrigerator.exception.UserMisMatchException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final ColumnService columnService;
    private final InvitationRepository invitationRepository;
    private final ColumnRepository columnRepository;
    private final UserService userService;
    private final BoardService boardService;


    @Transactional
    public CardResponseDto createCard(Long boardId, Long columnId, CardRequestDto requestDto,
        User user) {
        Columns checkColumn = columnService.findById(columnId);
        Board checkBoard = boardService.findById(boardId);
        boolean isBoardUser = invitationRepository.existsByBoardAndUser(checkColumn.getBoard(),
            user);

        if (!isBoardUser) {
            throw new DataNotFoundException("보드에 초대된 사용자가 아닙니다");
        }

        Card card = new Card(requestDto, checkColumn, user, checkBoard);
        cardRepository.save(card);

        return new CardResponseDto(card);
    }


    @Transactional
    public CardResponseDto updateCard(Long boardId, Long columnId, Long cardId,
        CardRequestDto requestDto,
        User user) {
        Board checkBoard = boardService.findById(boardId);
        Columns checkColumn = columnService.findById(columnId);
        Card card = findCard(cardId);

        boolean isBoardUser = invitationRepository.existsByBoardAndUser(checkColumn.getBoard(),
            user);

        if (!isBoardUser) {
            throw new DataNotFoundException("보드에 초대된 사용자가 아닙니다");
        }

        if (!card.getUser().getId().equals(user.getId())) {
            throw new UserMisMatchException("카드는 생성한 작업자만 수정가능합니다");
        }
        card.update(requestDto);
        return new CardResponseDto(card);
    }

    @Transactional
    public void deleteCard(Long boardId, Long columnId, Long cardId, User user) {
        Board checkBoard = boardService.findById(boardId);
        Card card = cardRepository.findById(cardId)
            .orElseThrow(() -> new CardNotFoundException("해당 카드는 삭제되었거나 존재하지 않습니다."));
        if (!card.getUser().getId().equals(user.getId())) {
            throw new UserMisMatchException("카드는 생성한 작업자만 수정가능합니다");
        }
        cardRepository.delete(card);
    }

    @Transactional(readOnly = true)
    public CardResponseDto getCard(Long cardId, User user, Long columnId) {
        Columns checkColumn = columnService.findById(columnId);
        boolean isBoardUser = invitationRepository.existsByBoardAndUser(checkColumn.getBoard(),
            user);

        if (!isBoardUser) {
            throw new DataNotFoundException("보드에 초대된 사용자가 아닙니다");
        }
        userService.findById(user.getId());
        return cardRepository.findById(cardId)
            .map(CardResponseDto::new)
            .orElseThrow(() -> new DataNotFoundException("선택한 카드가 없습니다"));
    }

    @Transactional(readOnly = true)
    public List<CardResponseDto> getCards() {
        List<Card> cards = cardRepository.findAll();
        return cards
            .stream()
            .map(CardResponseDto::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CardResponseDto> getCollaboratorCard(Long userId, Long boardId) {
        Board checkBoard = boardService.findById(boardId);
        User user = userService.findById(userId);
        List<Card> cards = cardRepository.findByUser(user);

        return cards
            .stream()
            .map(CardResponseDto::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CardResponseDto> getColumnNameCard(Long columnId, Long boardId) {
        Board checkBoard = boardService.findById(boardId);
        Columns columns = columnService.findById(columnId);
        List<Card> cards = cardRepository.findByColumns(columns);
        return cards
            .stream()
            .map(CardResponseDto::new)
            .collect(Collectors.toList());
    }

    public Card findCard(Long cardId) {
        return cardRepository.findById(cardId).orElseThrow(
            () -> new DataNotFoundException("해당 카드는 삭제되었거나 존재하지 않습니다.")
        );
    }

}
