package com.sparta.refrigerator.card.controller;

import com.sparta.refrigerator.auth.service.UserDetailsImpl;
import com.sparta.refrigerator.card.dto.CardRequestDto;
import com.sparta.refrigerator.card.dto.CardResponseDto;
import com.sparta.refrigerator.card.service.CardService;
import com.sparta.refrigerator.column.service.ColumnService;
import com.sparta.refrigerator.common.response.DataCommonResponse;
import com.sparta.refrigerator.common.response.StatusCommonResponse;
import com.sparta.refrigerator.exception.UnauthorizedException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CardController {

    private static final int PAGE_SIZE = 5;

    private final CardService cardService;

    private final ColumnService columnService;


    @PostMapping("/admin/boards/{boardId}/columns/{columnId}/cards")
    public ResponseEntity<DataCommonResponse<CardResponseDto>> createCard(
        @PathVariable Long boardId, @PathVariable Long columnId,
        @RequestBody CardRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        if (userDetails == null) {
            throw new UnauthorizedException("로그인이 필요합니다");
        }
        CardResponseDto responseDto = cardService.createCard(boardId, columnId, requestDto,
            userDetails.getUser());
        DataCommonResponse<CardResponseDto> response = new DataCommonResponse<>(201, "카드 생성되었습니다.",
            responseDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/admin/boards/{boardId}/columns/{columnId}/cards/{cardId}")
    public ResponseEntity<DataCommonResponse<CardResponseDto>> updateCard(
        @PathVariable Long boardId, @PathVariable Long columnId, @PathVariable Long cardId,
        @RequestBody CardRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CardResponseDto responseDto = cardService.updateCard(boardId, columnId, cardId, requestDto,
            userDetails.getUser());
        DataCommonResponse<CardResponseDto> response = new DataCommonResponse<>(200, "카드가 수정되었습니다.",
            responseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/admin/boards/{boardId}/columns/{columnId}/cards/{cardId}")
    public ResponseEntity<StatusCommonResponse> deleteCard(@PathVariable Long boardId,
        @PathVariable Long columnId, @PathVariable Long cardId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        cardService.deleteCard(boardId, columnId, cardId, userDetails.getUser());
        StatusCommonResponse response = new StatusCommonResponse(204, "카드가 삭제되었습니다.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/columns/{columnId}/cards/{cardId}")
    public ResponseEntity<DataCommonResponse<CardResponseDto>> getCard(
        @PathVariable Long columnId, @PathVariable Long cardId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CardResponseDto responseDto = cardService.getCard(cardId, userDetails.getUser(), columnId);
        DataCommonResponse<CardResponseDto> response = new DataCommonResponse<>(200,
            "카드 단건 조회에 성공하였습니다.", responseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/boards/{boardId}/cards")
    public ResponseEntity<DataCommonResponse<List<CardResponseDto>>> getCards(
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<CardResponseDto> responseDtoList = cardService.getCards();
        DataCommonResponse<List<CardResponseDto>> response = new DataCommonResponse<>(200,
            "카드 전체조회에 성공하였습니다.", responseDtoList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 작업자별 카드 조회 기능
     *
     * @param userId : 조회할 작업자의 Id
     * @return : 입력된 작업자의 카드
     */
    @GetMapping("/boards/{boardId}/cards/assignee")
    public ResponseEntity<DataCommonResponse<List<CardResponseDto>>> getCollaboratorCard(
        @RequestParam Long userId, @PathVariable Long boardId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<CardResponseDto> responseDtoList = cardService.getCollaboratorCard(userId, boardId);
        DataCommonResponse<List<CardResponseDto>> response = new DataCommonResponse<>(200,
            "카드 작업자별 조회에 성공하였습니다.", responseDtoList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/boards/{boardId}/columns/{columnId}")
    public ResponseEntity<DataCommonResponse<List<CardResponseDto>>> getColumnNameCard(
        @PathVariable Long columnId, @PathVariable Long boardId) {
        List<CardResponseDto> responseDtoList = cardService.getColumnNameCard(columnId, boardId);
        DataCommonResponse<List<CardResponseDto>> response = new DataCommonResponse<>(200,
            "카드 상태별 조회에 성공하였습니다.", responseDtoList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}