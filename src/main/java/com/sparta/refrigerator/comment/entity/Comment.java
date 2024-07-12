package com.sparta.refrigerator.comment.entity;

import com.sparta.refrigerator.auth.entity.User;
import com.sparta.refrigerator.card.entity.Card;
import com.sparta.refrigerator.comment.dto.CommentRequestDto;
import com.sparta.refrigerator.common.TimeStamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "comment")
public class Comment extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(name = "content", length = 255, nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    public Comment(CommentRequestDto commentRequestDto, Card card, User user) {
        this.content = commentRequestDto.getContent();
        this.card = card;
        this.user = user;
    }

    public String getUserName() {
        return this.user.getUserName();
    }
}
