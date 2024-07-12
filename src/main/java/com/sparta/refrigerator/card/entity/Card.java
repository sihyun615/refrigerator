package com.sparta.refrigerator.card.entity;

import com.sparta.refrigerator.common.TimeStamp;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.print.DocFlavor.STRING;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "card")
public class Card extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private String collaborator;

    private LocalDateTime deadline;


    public void update(String title, String content, String collaborator, LocalDateTime deadline) {
        this.title = title;
        this.content = content;
        this.collaborator = collaborator;
        this.deadline = deadline;
    }





}
