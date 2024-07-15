package com.sparta.refrigerator.card.dto;

import com.sparta.refrigerator.card.entity.Card;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardResponseDto {

    private Long cardId;
    private String title;
    private String content;
    private String collaborator;
    private String deadline;

    public CardResponseDto(Card card) {
        this.cardId = card.getId();
        this.title = card.getTitle();
        this.content = card.getContent();
        this.collaborator = card.getCollaborator();
        this.deadline = card.getDeadline();
    }
}
