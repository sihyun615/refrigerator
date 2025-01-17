package com.sparta.refrigerator.column.entity;

import com.sparta.refrigerator.auth.entity.User;
import com.sparta.refrigerator.board.entity.Board;
import com.sparta.refrigerator.card.entity.Card;
import com.sparta.refrigerator.column.dto.ColumnRequestDto;
import com.sparta.refrigerator.common.TimeStamp;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import lombok.RequiredArgsConstructor;

@Getter
@Entity
@Table(name = "columns")
@RequiredArgsConstructor
public class Columns extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "column_name", nullable = false)
    private String columnName;
    @Column(name = "column_index", nullable = false)
    private Long columnIndex;
    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @OneToMany(mappedBy = "columns", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Card> cardList = new ArrayList<>();

    public Columns(Board board, ColumnRequestDto requestDto, User user, Long maxIndex) {
        this.board = board;
        this.user = user;
        this.columnName = requestDto.getColumnName();
        this.columnIndex = maxIndex;
    }

    public void updateIndex(long index) {
        this.columnIndex = index;
    }

}
