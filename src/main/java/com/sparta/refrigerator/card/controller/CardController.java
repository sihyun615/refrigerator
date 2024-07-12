package com.sparta.refrigerator.card.controller;

import com.sparta.refrigerator.card.dto.CardRequestDto;
import com.sparta.refrigerator.card.dto.CardResponseDto;
import com.sparta.refrigerator.card.service.CardService;
import com.sparta.refrigerator.card.util.SuccessResponse;
import com.sparta.refrigerator.card.util.SuccessResponseFactory;
import com.sparta.refrigerator.common.response.DataCommonResponse;
import com.sparta.refrigerator.exception.UnauthorizedException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<?> createCard(@RequestBody CardRequestDto requestDto) {
        CardResponseDto responseDto = cardService.createCard(requestDto);
        return SuccessResponseFactory.ok(responseDto);
    }

    @GetMapping("/columns/{columnId}/card")
    public ResponseEntity<?> getCard(@PathVariable Long cardId) {
        List<CardResponseDto> responseDto = cardService.getCard(cardId);
        return SuccessResponseFactory.ok(responseDto);
    }

    @PutMapping("/columns/{columnId}/cards/{cardId}")
    public ResponseEntity<?> updateCard(@PathVariable Long cardId,
        @RequestBody CardRequestDto requestDto) {
        CardResponseDto responseDto = cardService.updateCard(cardId, requestDto);
        return SuccessResponseFactory.ok(responseDto);
    }

    @DeleteMapping("/cards/{cardId}")
    public ResponseEntity<?> deleteCard(@PathVariable Long cardId) {
        cardService.deleteCard(cardId);
        return SuccessResponseFactory.noContent();
    }

    /**
     * 카드 전체조회 기능
     *
     * @param columnId : 카드가 등록된 컬럼의 Id
     * @return : 등록된 카드 정보
     */
//    @GetMapping
//    public ResponseEntity<DataCommonResponse<List<CardResponseDto>>> getAllComments(
//        @PathVariable Long columnId,
//        @RequestParam("page") int page) {
//
//        if (userDetails == null) {
//            throw new UnauthorizedException("로그인이 필요합니다.");
//        }
//
//        List<CardResponseDto> cards = cardService.getAllColumns(columnId, page, PAGE_SIZE);
//        DataCommonResponse<List<CardResponseDto>> response = new DataCommonResponse<>(200,
//            "댓글 조회 성공", cards);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }


}
