package com.sparta.refrigerator.card.controller;

import com.sparta.refrigerator.auth.service.UserDetailsImpl;
import com.sparta.refrigerator.card.dto.CardCollaboratorRequestDto;
import com.sparta.refrigerator.card.dto.CardMoveRequestDto;
import com.sparta.refrigerator.card.dto.CardRequestDto;
import com.sparta.refrigerator.card.dto.CardResponseDto;
import com.sparta.refrigerator.card.dto.CardStatusRequestDto;
import com.sparta.refrigerator.card.service.CardService;
import com.sparta.refrigerator.column.dto.ColumnMoveRequestDto;
import com.sparta.refrigerator.column.service.ColumnService;
import com.sparta.refrigerator.common.response.DataCommonResponse;
import com.sparta.refrigerator.common.response.StatusCommonResponse;
import com.sparta.refrigerator.exception.UnauthorizedException;
import jakarta.validation.Valid;
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

    private final CardService cardService;

    private final ColumnService columnService;

    /**
     * 키드 생성기능
     *
     * @param boardId          : 카드를 등록 할 보드의 Id
     * @param requestDto : 등록할 카드의 정보
     * @return : 등록된 카드 정보
     */

    @PostMapping("/boards/{boardId}/columns/{columnId}/cards")
    public ResponseEntity<DataCommonResponse<CardResponseDto>> createCard(
        @PathVariable Long boardId, @PathVariable Long columnId,
        @Valid @RequestBody CardRequestDto requestDto,
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

    /**
     * 카드 수정기능
     *
     * @param cardId          : 수정할 카드의 Id
     * @param requestDto : 수정될 카드의 정보
     * @return : 수정된 카드 정보
     */

    @PutMapping("/boards/{boardId}/columns/{columnId}/cards/{cardId}")
    public ResponseEntity<DataCommonResponse<CardResponseDto>> updateCard(
        @PathVariable Long boardId, @PathVariable Long columnId, @PathVariable Long cardId,
        @Valid @RequestBody CardRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CardResponseDto responseDto = cardService.updateCard(boardId, columnId, cardId, requestDto,
            userDetails.getUser());
        DataCommonResponse<CardResponseDto> response = new DataCommonResponse<>(200, "카드가 수정되었습니다.",
            responseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 카드 삭제기능
     *
     * @param cardId : 삭제할 카드의 Id
     * @return : 삭제성공
     */

    @DeleteMapping("/boards/{boardId}/columns/{columnId}/cards/{cardId}")
    public ResponseEntity<StatusCommonResponse> deleteCard(@PathVariable Long boardId,
        @PathVariable Long columnId, @PathVariable Long cardId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        cardService.deleteCard(boardId, columnId, cardId, userDetails.getUser());
        StatusCommonResponse response = new StatusCommonResponse(204, "카드가 삭제되었습니다.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 카드 단건조회 기능
     *
     * @param cardId          : 조회할 카드의 Id
     * @return : 조회된 카드 정보
     */

    @GetMapping("/board/{boardId}/columns/{columnId}/cards/{cardId}")
    public ResponseEntity<DataCommonResponse<CardResponseDto>> getCard(
        @PathVariable Long columnId, @PathVariable Long cardId, @PathVariable Long boardId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CardResponseDto responseDto = cardService.getCard(cardId, userDetails.getUser(), columnId, boardId);
        DataCommonResponse<CardResponseDto> response = new DataCommonResponse<>(200,
            "카드 단건 조회에 성공하였습니다.", responseDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 카드 전체조회 기능
     *
     * @return : 조회된 모든 카드 정보
     */

    @GetMapping("/admin/cards")
    public ResponseEntity<DataCommonResponse<List<CardResponseDto>>> getCards(
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<CardResponseDto> responseDtoList = cardService.getCards(userDetails.getUser());
        DataCommonResponse<List<CardResponseDto>> response = new DataCommonResponse<>(200,
            "보드 상관없이 모든 카드 전체조회에 성공하였습니다.", responseDtoList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 한정된 보드내에서의 모든 카드 전체조회
     *
     * @param boardId : 전체조회할 보드 Id
     * @return : 선택한 보드내에서 조회된 모든 카드 정보
     */

    @GetMapping("/boards/{boardId}/cards")
    public ResponseEntity<DataCommonResponse<List<CardResponseDto>>> getBoardCards(
        @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long boardId) {
        List<CardResponseDto> responseDtoList = cardService.getBoardCards(userDetails.getUser(),
            boardId);
        DataCommonResponse<List<CardResponseDto>> response = new DataCommonResponse<>(200,
            "보드내에서 카드 전체조회에 성공하였습니다.", responseDtoList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 작업자별 카드 조회
     *
     * @param  : 조회할 작업자 이름 (collaborator)
     * @param boardId : 작업자별 카드를 조회할 보드 Id
     * @return : 선택한 보드내에서 조회된 작업자별 카드 정보
     */

    @GetMapping("/boards/{boardId}/cards/assignee")
    public ResponseEntity<DataCommonResponse<List<CardResponseDto>>> getAssigneeCard(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long boardId, @RequestParam String collaborator) {
        List<CardResponseDto> responseDtoList = cardService.getAssigneeCard(
            collaborator, boardId, userDetails.getUser());
        DataCommonResponse<List<CardResponseDto>> response = new DataCommonResponse<>(200,
            "카드 작업자별 조회에 성공하였습니다.", responseDtoList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 카드 상태별 조회
     *
     * @param boardId : 상태별 조회할 보드 Id
     * @param columnName : 조회할 상태 이름(columnName)
     * @return : 선택한 보드내에서 조회된 상태별 카드 정보
     */

    @GetMapping("/boards/{boardId}/cards/status")
    public ResponseEntity<DataCommonResponse<List<CardResponseDto>>> getColumnNameCard(
        @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long boardId,
        @RequestParam String columnName) {
        List<CardResponseDto> responseDtoList = cardService.getColumnNameCard(boardId,
            columnName, userDetails.getUser());
        DataCommonResponse<List<CardResponseDto>> response = new DataCommonResponse<>(200,
            "카드 상태별 조회에 성공하였습니다.", responseDtoList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/boards/{boardId}/columns/{columnId}/cards/{cardId}/transfer")
    public ResponseEntity<StatusCommonResponse> moveCard(@PathVariable Long boardId,
        @PathVariable Long columnId, @PathVariable Long cardId, @RequestBody CardMoveRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        cardService.moveCard(boardId, columnId, cardId, requestDto, userDetails.getUser());
        StatusCommonResponse response = new StatusCommonResponse(200, "카드가 이동되었습니다.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/boards/{boardId}/columns/{columnId}/cards")
    public ResponseEntity<DataCommonResponse<List<CardResponseDto>>> getColumnCard(
        @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long boardId,
        @PathVariable Long columnId) {
        List<CardResponseDto> responseDtoList = cardService.getColumnCard(boardId,
            columnId, userDetails.getUser());
        DataCommonResponse<List<CardResponseDto>> response = new DataCommonResponse<>(200,
            "카드 컬럼별 조회에 성공하였습니다.", responseDtoList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}