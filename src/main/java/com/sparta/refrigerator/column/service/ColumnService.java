package com.sparta.refrigerator.column.service;

import com.sparta.refrigerator.auth.entity.User;
import com.sparta.refrigerator.auth.service.UserService;
import com.sparta.refrigerator.board.entity.Board;
import com.sparta.refrigerator.column.dto.ColumnRequestDto;
import com.sparta.refrigerator.column.entity.Column;
import com.sparta.refrigerator.column.repository.ColumnRepository;
import com.sparta.refrigerator.exception.BadRequestException;
import org.springframework.stereotype.Service;

@Service
public class ColumnService {
    private ColumnRepository columnRepository;
    private BoardService boardService;
    private UserService userService;

    public void addColumn(Long boardId, ColumnRequestDto requestDto, User user) {
        Board checkBoard = boardService.findById(boardId);
        User checkUser = userService.findById(user.getId());

        Column column=new Column(checkBoard,requestDto,checkUser);
        columnRepository.save(column);
    }
    public Column findById(Long columnId) {
        return columnRepository.findById(columnId).orElseThrow(
                () -> new BadRequestException("해당 컬럼아이디가 존재하지 않습니다.")
        );
    }
}
