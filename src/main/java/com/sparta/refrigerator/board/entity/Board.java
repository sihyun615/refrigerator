package com.sparta.refrigerator.board.entity;

import com.sparta.refrigerator.board.dto.BoardRequestDTO;
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
import org.springframework.security.core.userdetails.User;

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

    public Board(BoardRequestDTO requestDTO) {
        this.boardName = requestDTO.getBoardName();
        this.boardInfo = requestDTO.getBoardInfo();
    }

}
