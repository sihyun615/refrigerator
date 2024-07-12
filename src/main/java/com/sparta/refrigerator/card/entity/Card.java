package com.sparta.refrigerator.card.entity;

import com.sparta.refrigerator.comment.entity.Comment;
import com.sparta.refrigerator.auth.entity.User;
import com.sparta.refrigerator.board.entity.Board;
import com.sparta.refrigerator.card.dto.CardRequestDto;
import com.sparta.refrigerator.column.entity.Columns;
import com.sparta.refrigerator.common.TimeStamp;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.events.Comment;
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
    private LocalDate deadline;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "column_id", nullable = false)
    private Columns columns;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();

    public Card(CardRequestDto requestDto, Columns columns, User user, Board board) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.collaborator = requestDto.getCollaborator();
        this.deadline = requestDto.getDeadline();
        this.columns = columns;
        this.user = user;
        this.board = board;
    }

    public void update(CardRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.collaborator = requestDto.getCollaborator();
        this.deadline = requestDto.getDeadline();
    }


}
