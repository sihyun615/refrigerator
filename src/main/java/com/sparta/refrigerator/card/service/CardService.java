package com.sparta.refrigerator.card.service;

import com.sparta.refrigerator.auth.entity.User;
import com.sparta.refrigerator.auth.service.UserService;
import com.sparta.refrigerator.board.entity.Board;
import com.sparta.refrigerator.board.repository.InvitationRepository;
import com.sparta.refrigerator.board.service.BoardService;
import com.sparta.refrigerator.card.dto.CardMoveRequestDto;
import com.sparta.refrigerator.card.dto.CardRequestDto;
import com.sparta.refrigerator.card.dto.CardResponseDto;
import com.sparta.refrigerator.card.entity.Card;
import com.sparta.refrigerator.card.repository.CardRepository;
import com.sparta.refrigerator.column.dto.ColumnMoveRequestDto;
import com.sparta.refrigerator.column.entity.Columns;
import com.sparta.refrigerator.column.repository.ColumnRepository;
import com.sparta.refrigerator.column.service.ColumnService;
import com.sparta.refrigerator.exception.BadRequestException;
import com.sparta.refrigerator.exception.CardNotFoundException;
import com.sparta.refrigerator.exception.DataNotFoundException;
import com.sparta.refrigerator.exception.ForbiddenException;
import com.sparta.refrigerator.exception.UserMisMatchException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final ColumnService columnService;
    private final InvitationRepository invitationRepository;
    private final UserService userService;
    private final BoardService boardService;
    private final ColumnRepository columnRepository;


    @Transactional
    public CardResponseDto createCard(Long boardId, Long columnId, CardRequestDto requestDto,
        User user) {
        Columns checkColumn = columnService.findById(columnId);
        Board checkBoard = boardService.findById(boardId);

        if(checkBoard == null) {
            throw new DataNotFoundException("보드가 존재하지 않습니다");
        }

        boolean isBoardUser = invitationRepository.existsByBoardAndUser(checkColumn.getBoard(),
            user);

        if (!isBoardUser) {
            throw new DataNotFoundException("보드에 초대된 사용자가 아닙니다");
        }

        // 카드 인덱스 최댓값 확인
        Long maxIndex = cardRepository.findMaxCardIndexByColumns(checkColumn);
        maxIndex = (maxIndex == null) ? 0L : maxIndex + 1;

        Card card = new Card(requestDto, checkColumn, user, checkBoard, maxIndex);
        cardRepository.save(card);

        return new CardResponseDto(card);
    }


    @Transactional
    public CardResponseDto updateCard(Long boardId, Long columnId, Long cardId,
        CardRequestDto requestDto, User user) {
        Board checkBoard = boardService.findById(boardId);

        if (checkBoard == null) {
            throw new DataNotFoundException("존재하지 않는 보드입니다");
        }

        Columns checkColumn = columnService.findById(columnId);

        if (checkColumn == null) {
            throw new DataNotFoundException("존재하지 않는 컬럼입니다");
        }

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

        if (checkBoard == null) {
            throw new DataNotFoundException("존재하지 않는 보드입니다");
        }
        Columns checkColumn = columnService.findById(columnId);

        if (checkColumn == null) {
            throw new DataNotFoundException("존재하지 않는 컬럼입니다");
        }

        Card card = cardRepository.findById(cardId)
            .orElseThrow(() -> new CardNotFoundException("해당 카드는 삭제되었거나 존재하지 않습니다."));
        if (!card.getUser().getId().equals(user.getId())) {
            throw new UserMisMatchException("카드는 생성한 작업자만 삭제가능합니다");
        }
        cardRepository.delete(card);
    }

    @Transactional(readOnly = true)
    public CardResponseDto getCard(Long cardId, User user, Long columnId, Long boardId) {
        Board checkBoard = boardService.findById(boardId);
        Card checkCard = findCard(cardId);

        if (checkBoard == null) {
            throw new DataNotFoundException("존재하지 않는 보드입니다");
        }

        Columns checkColumn = columnService.findById(columnId);

        if (checkColumn == null) {
            throw new DataNotFoundException("존재하지 않는 칼럼입니다");
        }

        boolean isBoardUser = invitationRepository.existsByBoardAndUser(checkColumn.getBoard(),
            user);

        if (!isBoardUser) {
            throw new DataNotFoundException("보드에 초대된 사용자가 아닙니다");
        }

        userService.findById(user.getId());
        return new CardResponseDto(checkCard);
    }

    @Transactional(readOnly = true)
    public List<CardResponseDto> getCards(User user) {
        userService.findById(user.getId());
        List<Card> cards = cardRepository.findAll();

        return cards
            .stream()
            .map(CardResponseDto::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CardResponseDto> getBoardCards(User user, Long boardId) {
        Board checkBoard = boardService.findById(boardId);
        userService.findById(user.getId());

        if (checkBoard == null) {
            throw new DataNotFoundException("보드가 존재하지 않습니다");
        }

        List<Card> cards = cardRepository.findAllByBoardId(boardId);

        if (cards == null) {
            throw new DataNotFoundException("해당 보드에는 카드가 존재하지않습니다");
        }

        return cards
            .stream()
            .map(CardResponseDto::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CardResponseDto> getAssigneeCard(String collaborator, Long boardId, User user) {
        Board checkBoard = boardService.findById(boardId);
        userService.findById(user.getId());

        if (checkBoard == null) {
            throw new DataNotFoundException("보드가 존재하지 않습니다");
        }

        List<Card> cards = cardRepository.findAllByCollaboratorAndBoardId(collaborator, boardId);

        if (cards.isEmpty()) {
            throw new DataNotFoundException("해당 보드에서 작업자가 생성한 카드를 찾을 수 없습니다.");
        }

        return cards
            .stream()
            .map(CardResponseDto::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CardResponseDto> getColumnNameCard(Long boardId, String columnName, User user) {
        Board checkBoard = boardService.findById(boardId);
        userService.findById(user.getId());

        if (checkBoard == null) {
            throw new DataNotFoundException("보드가 존재하지 않습니다");
        }

        Columns columns = columnRepository.findByColumnName(columnName)
            .orElseThrow(() -> new IllegalArgumentException("컬럼이 없습니다"));

        List<Card> cards = cardRepository.findByColumns(columns);

        return cards
            .stream()
            .map(CardResponseDto::new)
            .collect(Collectors.toList());
    }

    @Transactional
    public void moveCard(Long boardId, Long columnId, Long cardId, CardMoveRequestDto requestDto, User user) {
        Board checkBoard = boardService.findById(boardId);
        Columns currentColumn = columnService.findById(columnId);
        Columns targetColumn = columnService.findById(requestDto.getColumnId());
        userService.findById(user.getId());

        // 요청으로부터 이동할 카드의 목표 인덱스 가져옴
        Long targetCardIndex = requestDto.getCardIndex();

//        Card targetCard = (Card) cardRepository.findByColumns(currentColumn);



        // 이동할 카드찾기
        Card cardToMove = cardRepository.findById(cardId)
            .orElseThrow(() -> new DataNotFoundException("이동할 카드를 찾을 수 없습니다."));

        // 현재 컬럼의 카드 목록 갱신
        List<Card> currentCardList = cardRepository.findByColumns(currentColumn);
        currentCardList.remove(cardToMove);

        // 목표 인덱스 범위 확인
        List<Card> targetCardList = cardRepository.findByColumns(targetColumn);
        if (targetCardIndex < 0 || targetCardIndex > targetCardList.size()) {
            throw new BadRequestException("목표 인덱스가 범위를 벗어납니다.");
        }

        // 이동할 카드의 컬럼과 인덱스 업데이트
        cardToMove.updateColumns(targetColumn);
        targetCardList.add(Math.toIntExact(targetCardIndex), cardToMove);

//        // 현재 컬럼과 목표 컬럼이 같고, 옮기려는 카드 인덱스와 목표 카드인덱스가 같으면 아무 작업도 하지 않음
//        if (currentColumn.getId().equals(targetColumn.getId()) && currentColumn.getCardList().get().getCardIndex() == targetCardIndex) {
//            return;
//        }

        // 변경된 순서대로 모든 카드를 저장
        for (int i = 0; i < currentCardList.size(); i++) {
            Card card = currentCardList.get(i);
            card.updateCardIndex(i);
            cardRepository.save(card);
        }

        for (int i = 0; i < targetCardList.size(); i++) {
            Card card = targetCardList.get(i);
            card.updateCardIndex(i);
            cardRepository.save(card);
        }
    }


    public Card findCard(Long cardId) {
        return cardRepository.findById(cardId).orElseThrow(
            () -> new DataNotFoundException("해당 카드는 삭제되었거나 존재하지 않습니다.")
        );
    }

    @Transactional(readOnly = true)
    public List<CardResponseDto> getColumnCard(Long boardId, Long columnId, User user) {
        Board checkBoard = boardService.findById(boardId);
        Columns checkColumn = columnService.findById(columnId);
        userService.findById(user.getId());

        if (checkBoard == null) {
            throw new DataNotFoundException("보드가 존재하지 않습니다");
        }

        List<Card> cards = cardRepository.findByColumns(checkColumn);

        return cards
            .stream()
            .map(CardResponseDto::new)
            .collect(Collectors.toList());
    }
}