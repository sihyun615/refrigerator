package com.sparta.refrigerator.comment.controller;

import com.sparta.refrigerator.auth.service.UserDetailsImpl;
import com.sparta.refrigerator.comment.dto.CommentRequestDto;
import com.sparta.refrigerator.comment.dto.CommentResponseDto;
import com.sparta.refrigerator.comment.service.CommentService;
import com.sparta.refrigerator.common.response.DataCommonResponse;
import com.sparta.refrigerator.common.exception.UnauthorizedException;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards/{boardId}/columns/{columnId}/cards/{cardId}/comments")
public class CommentController {

    private static final int PAGE_SIZE = 5;

    private final CommentService commentService;

    /**
     * 댓글 등록 기능
     *
     * @param cardId            : 댓글 등록 할 카드의 Id
     * @param commentRequestDto : 등록할 댓글 정보
     * @return : 등록된 댓글 정보
     */
    @PostMapping
    public ResponseEntity<DataCommonResponse<CommentResponseDto>> createComment(
        @PathVariable Long cardId,
        @Valid @RequestBody CommentRequestDto commentRequestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        if (userDetails == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
        CommentResponseDto responseDto = commentService.createComment(commentRequestDto, cardId,
            userDetails.getUser());
        DataCommonResponse<CommentResponseDto> response = new DataCommonResponse<>(201, "댓글 등록 성공",
            responseDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    /**
     * 댓글 조회 기능
     *
     * @param cardId : 댓글 등록된 카드의 Id
     * @return : 등록된 댓글 정보
     */
    @GetMapping
    public ResponseEntity<DataCommonResponse<List<CommentResponseDto>>> getAllComments(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable Long cardId) {

        if (userDetails == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
        List<CommentResponseDto> comments = commentService.getAllComments(cardId);
        DataCommonResponse<List<CommentResponseDto>> response = new DataCommonResponse<>(200,
            "댓글 조회 성공", comments);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
