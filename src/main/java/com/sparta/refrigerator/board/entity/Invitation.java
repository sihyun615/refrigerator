package com.sparta.refrigerator.board.entity;

import com.sparta.refrigerator.auth.entity.User;
import com.sparta.refrigerator.auth.enumeration.UserAuth;
import com.sparta.refrigerator.common.TimeStamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "invitaion")
public class Invitation extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_auth", nullable = false)
    private UserAuth auth;

    public Invitation(Board board, User user, UserAuth auth) {
        this.board = board;
        this.user = user;
        this.auth = auth;
    }
}
