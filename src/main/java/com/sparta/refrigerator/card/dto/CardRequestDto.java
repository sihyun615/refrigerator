package com.sparta.refrigerator.card.dto;

import com.sparta.refrigerator.card.entity.Card;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotBlank.List;
import java.time.LocalDate;
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
    private LocalDate deadline;
}
