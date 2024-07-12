package com.sparta.refrigerator.card.dto;

import com.sparta.refrigerator.card.entity.Card;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotBlank.List;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CardRequestDto {

    @NotBlank(message = "추가할 제목을 입력해주세요")
    private String title;

    private String content;
    private String collaborator;
    private LocalDateTime deadline;

    @Builder
    public CardRequestDto(String title, String content, String collaborator,
        LocalDateTime deadline) {
        this.title = title;
        this.content = content;
        this.collaborator = collaborator;
        this.deadline = deadline;
    }

}
