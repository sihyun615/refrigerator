package com.sparta.refrigerator.comment.dto;

import com.sparta.refrigerator.comment.entity.Comment;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class CommentResponseDto {

    private final String userName;
    private final String content;
    private final LocalDateTime createdAt;

    public CommentResponseDto(Comment comment) {
        this.userName = comment.getUserName();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
    }

}