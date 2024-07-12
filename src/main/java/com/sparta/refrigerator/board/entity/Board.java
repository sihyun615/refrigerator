package com.sparta.refrigerator.board.entity;

import com.sparta.refrigerator.auth.entity.User;
import com.sparta.refrigerator.board.dto.BoardRequestDTO;
import com.sparta.refrigerator.card.entity.Card;
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
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "boards")
public class Board extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String boardName;

    @Column(nullable = false)
    private String boardInfo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invitation> invitationList = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Columns> columnsList = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Card> cardList = new ArrayList<>();

    public Board(BoardRequestDTO requestDTO, User user) {
        this.boardName = requestDTO.getBoardName();
        this.boardInfo = requestDTO.getBoardInfo();
        this.user = user;
    }

    public void update(BoardRequestDTO requestDTO) {
        this.boardName = requestDTO.getBoardName();
        this.boardInfo = requestDTO.getBoardInfo();
    }

}
