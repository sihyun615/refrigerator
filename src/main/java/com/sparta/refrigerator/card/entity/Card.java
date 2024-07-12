package com.sparta.refrigerator.card.entity;

import com.sparta.refrigerator.auth.entity.User;
import com.sparta.refrigerator.common.TimeStamp;
import com.sparta.refrigerator.column.entity.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    private String collaborator;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "column_id", nullable = false)
    private Column column;


    public void update(String title, String content, String collaborator, LocalDateTime deadline) {
        this.title = title;
        this.content = content;
        this.collaborator = collaborator;
        this.deadline = deadline;
    }





}
