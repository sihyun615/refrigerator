package com.sparta.refrigerator.card.controller;

import com.sparta.refrigerator.card.dto.CardRequestDto;
import com.sparta.refrigerator.card.dto.CardResponseDto;
import com.sparta.refrigerator.card.service.CardService;

import com.sparta.refrigerator.common.response.DataCommonResponse;
import com.sparta.refrigerator.common.response.StatusCommonResponse;
import com.sparta.refrigerator.exception.UnauthorizedException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/admin")
@RequiredArgsConstructor
public class CardController {

    private static final int PAGE_SIZE = 5;

    private final CardService cardService;

    @PostMapping("/columns/{columnId}/card")
    public ResponseEntity<?> createCard(@PathVariable Long columnId,
        @RequestBody CardRequestDto requestDto) {
        CardResponseDto responseDto = cardService.createCard(requestDto);
        DataCommonResponse<CardResponseDto> response = new DataCommonResponse<>(201, "카드 생성되었습니다.",
            responseDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/columns/{columnId}/card/{cardId}")
    public ResponseEntity<DataCommonResponse<CardResponseDto>> getCard(
        @PathVariable Long columnId, @PathVariable Long cardId) {
        CardResponseDto responseDto = cardService.getCard(cardId);
        DataCommonResponse<CardResponseDto> response = new DataCommonResponse<>(200,
            "카드 단건 조회 성공하였습니다.", responseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/columns/{columnId}/cards")
    public ResponseEntity<DataCommonResponse<List<CardResponseDto>>> collaboratorCard(
        @PathVariable Long columnId, @RequestParam Long userId) {
        List<CardResponseDto> responseDtoList = cardService.collaboratorCard(columnId, userId);
        DataCommonResponse<List<CardResponseDto>> response = new DataCommonResponse<>(200,
            "카드 작업자별 조회 성공하였습니다.", responseDtoList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/columns/{columnId}/cards/{cardId}")
    public ResponseEntity<?> updateCard(@PathVariable Long columnId, @PathVariable Long cardId,
        @RequestBody CardRequestDto requestDto) {
        CardResponseDto responseDto = cardService.updateCard(cardId, requestDto);
        DataCommonResponse<CardResponseDto> response = new DataCommonResponse<>(201, "카드가 수정되었습니다.",
            responseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/cards/{cardId}")
    public ResponseEntity<?> deleteCard(@PathVariable Long cardId) {
        cardService.deleteCard(cardId);
        StatusCommonResponse response = new StatusCommonResponse(204, "카드가 삭제되었습니다.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/cards")
    public ResponseEntity<DataCommonResponse<List<CardResponseDto>>> getAllCards() {
        List<CardResponseDto> responseDtoList = cardService.getAllCards();
        DataCommonResponse<List<CardResponseDto>> response = new DataCommonResponse<>(200,
            "카드 전체조회 성공하였습니다.", responseDtoList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }






}
