package com.sparta.refrigerator.board.dto;

import com.sparta.refrigerator.board.entity.Board;
import lombok.Getter;

@Getter
public class BoardResponseDTO {
    private Long id;
    private String boardName;
    private String boardInfo;
    private Long userId;

    public BoardResponseDTO(Board board) {
        this.id = board.getId();
        this.boardName = board.getBoardName();
        this.boardInfo = board.getBoardInfo();
        this.userId = board.getUser().getId();
    }
}
