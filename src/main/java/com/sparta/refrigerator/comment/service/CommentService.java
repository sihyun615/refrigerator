package com.sparta.refrigerator.comment.service;

import com.sparta.refrigerator.auth.entity.User;
import com.sparta.refrigerator.card.entity.Card;
import com.sparta.refrigerator.card.service.CardService;
import com.sparta.refrigerator.comment.dto.CommentRequestDto;
import com.sparta.refrigerator.comment.dto.CommentResponseDto;
import com.sparta.refrigerator.comment.entity.Comment;
import com.sparta.refrigerator.comment.repository.CommentRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CardService cardService;
    private final CommentRepository commentRepository;

    public CommentResponseDto createComment(CommentRequestDto commentRequestDto, Long cardId,
        User user) {
        Card card = cardService.findCard(cardId);
        Comment comment = commentRepository.save(new Comment(commentRequestDto, card, user));
        return new CommentResponseDto(comment);
    }

    public List<CommentResponseDto> getAllComments(Long cardId) {
        Card card = cardService.findCard(cardId);
        List<Comment> comment = commentRepository.findAllByCardOrderByCreatedAtDesc(card);
        return comment.stream().map(CommentResponseDto::new).collect(Collectors.toList());
    }

}
