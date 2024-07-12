package com.sparta.refrigerator.card.service;

import com.sparta.refrigerator.card.dto.CardRequestDto;
import com.sparta.refrigerator.card.dto.CardResponseDto;
import com.sparta.refrigerator.card.entity.Card;
import com.sparta.refrigerator.card.repository.CardRepository;
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
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("카드를 찾지 못했습니다"));

        card.update(requestDto.getTitle(), requestDto.getContent(), requestDto.getCollaborator(), requestDto.getDeadline());
        return new CardResponseDto(card);
    }

    @Transactional
    public void deleteCard(Long cardId) {

        Card card = cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("카드를 찾지 못했습니다"));
//      user.removePost(card);
    }

    public List<CardResponseDto> getCard(Long cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("카드를 찾지 못했습니다"));

        return cardRepository.findById(cardId)
            .stream()
            .map(CardResponseDto::new)
            .collect(Collectors.toList());
    }

//    public List<CardResponseDto> getAllColumns(Long columnId, int page, int pageSize) {
//        findColumn(columnId);
//        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
//        Page<Card> cardPage = CardRepository.findAllByColumnId(columnId, pageable);
//        return cardPage.stream().map(CardResponseDto::new).collect(Collectors.toList());
//    }

    public Card findColumn(Long cardId) {
        return cardRepository.findById(cardId).orElseThrow(
            () -> new DataNotFoundException("해당 칼럼은 삭제되었거나 존재하지 않습니다.")
        );
    }
}
