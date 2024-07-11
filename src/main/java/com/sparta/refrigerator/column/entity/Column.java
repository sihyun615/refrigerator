package com.sparta.refrigerator.column.entity;

import com.sparta.refrigerator.auth.entity.User;
import com.sparta.refrigerator.board.entity.Board;
import com.sparta.refrigerator.column.dto.ColumnMoveRequestDto;
import com.sparta.refrigerator.column.dto.ColumnRequestDto;
import com.sparta.refrigerator.common.TimeStamp;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Table(name = "columns")
@RequiredArgsConstructor
public class Column extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @jakarta.persistence.Column(name = "column_name", nullable = false)
    private String columnName;
    @jakarta.persistence.Column(name = "column_index", nullable = false, unique = true)
    private Long columnIndex;
    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Column(Board board, ColumnRequestDto requestDto, User user, Long maxIndex) {
        this.board = board;
        this.user = user;
        this.columnName = requestDto.getColumnName();
        this.columnIndex = maxIndex;
    }

    public void updateIndex(long index) {
        this.columnIndex = index;
    }
}
