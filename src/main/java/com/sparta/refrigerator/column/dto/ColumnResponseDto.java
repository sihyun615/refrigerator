package com.sparta.refrigerator.column.dto;

import com.sparta.refrigerator.card.entity.Card;
import com.sparta.refrigerator.column.entity.Columns;
import java.util.List;
import lombok.Getter;

@Getter
public class ColumnResponseDto {

    private Long columnId;
    private String columnName;
    private Long columnIndex;
    private List<Card> cards;

    public ColumnResponseDto(Columns columns) {
        this.columnId=columns.getId();
        this.columnName = columns.getColumnName();
        this.columnIndex = columns.getColumnIndex();
        this.cards=columns.getCardList();
    }
}