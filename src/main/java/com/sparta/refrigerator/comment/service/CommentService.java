package com.sparta.refrigerator.comment.service;

import com.sparta.refrigerator.auth.entity.User;
import com.sparta.refrigerator.card.entity.Card;
import com.sparta.refrigerator.card.repository.CardRepository;
import com.sparta.refrigerator.comment.dto.CommentRequestDto;
import com.sparta.refrigerator.comment.dto.CommentResponseDto;
import com.sparta.refrigerator.comment.entity.Comment;
import com.sparta.refrigerator.comment.repository.CommentRepository;
import com.sparta.refrigerator.exception.DataNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CardRepository cardRepository;
    private final CommentRepository commentRepository;

    public CommentResponseDto createComment(CommentRequestDto commentRequestDto, Long cardId,
        User user) {
        Card card = findCard(cardId);
        Comment comment = commentRepository.save(new Comment(commentRequestDto, card, user));
        return new CommentResponseDto(comment);
    }

    public List<CommentResponseDto> getAllComments(Long cardId, int page, int pageSize) {
        findCard(cardId);
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> commentPage = commentRepository.findAllByCard(cardId, pageable);
        return commentPage.stream().map(CommentResponseDto::new).collect(Collectors.toList());
    }

    public Card findCard(Long cardId) {
        return cardRepository.findById(cardId).orElseThrow(
            () -> new DataNotFoundException("해당 카드는 삭제되었거나 존재하지 않습니다.")
        );
    }

}
