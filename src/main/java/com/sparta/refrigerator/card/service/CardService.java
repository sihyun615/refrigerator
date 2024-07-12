package com.sparta.refrigerator.card.service;

import com.sparta.refrigerator.auth.entity.User;
import com.sparta.refrigerator.auth.service.UserService;
import com.sparta.refrigerator.card.dto.CardRequestDto;
import com.sparta.refrigerator.card.dto.CardResponseDto;
import com.sparta.refrigerator.card.entity.Card;
import com.sparta.refrigerator.card.repository.CardRepository;
import com.sparta.refrigerator.column.service.ColumnService;
import com.sparta.refrigerator.exception.CardNotFoundException;
import com.sparta.refrigerator.exception.DataNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

    private final ColumnService columnService;

    private final UserService userService;


    @Transactional
    public CardResponseDto createCard(CardRequestDto requestDto) {

        Card card = Card.builder()
            .title(requestDto.getTitle())
            .content(requestDto.getContent())
            .collaborator(requestDto.getCollaborator())
            .deadline(requestDto.getDeadline())
            .build();

        cardRepository.flush();
        return new CardResponseDto(card);
    }


    @Transactional
    public CardResponseDto updateCard(Long cardId, CardRequestDto requestDto) {
        Card card = cardRepository.findById(cardId)
            .orElseThrow(() -> new CardNotFoundException("해당 카드는 삭제되었거나 존재하지 않습니다."));

        card.update(requestDto.getTitle(), requestDto.getContent(), requestDto.getCollaborator(),
            requestDto.getDeadline());
        return new CardResponseDto(card);
    }

    @Transactional
    public void deleteCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
            .orElseThrow(() -> new CardNotFoundException("해당 카드는 삭제되었거나 존재하지 않습니다."));
        cardRepository.delete(card);

    }

    @Transactional(readOnly = true)
    public CardResponseDto getCard(Long cardId) {

        return cardRepository.findById(cardId)
            .map(CardResponseDto::new)
            .orElseThrow(() -> new DataNotFoundException("선택한 카드가 없습니다"));

    }

    public List<CardResponseDto> getAllCards() {
        List<Card> cards = cardRepository.findAll();
        return cards
            .stream()
            .map(CardResponseDto::new)
            .collect(Collectors.toList());
    }

    public List<CardResponseDto> collaboratorCard(Long columnId, Long userId) {
        List<Card> cards = cardRepository.findAllByUser_Id(userId);
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
