package com.sparta.refrigerator.column.service;

import com.sparta.refrigerator.auth.entity.User;
import com.sparta.refrigerator.auth.enumeration.UserAuth;
import com.sparta.refrigerator.auth.service.UserService;
import com.sparta.refrigerator.board.entity.Board;
import com.sparta.refrigerator.column.dto.ColumnMoveRequestDto;
import com.sparta.refrigerator.column.dto.ColumnResponseDto;
import com.sparta.refrigerator.column.entity.Column;
import com.sparta.refrigerator.column.entity.StatusEnum;
import com.sparta.refrigerator.column.repository.ColumnRepository;
import com.sparta.refrigerator.exception.BadRequestException;
import com.sparta.refrigerator.exception.ConflictException;
import com.sparta.refrigerator.exception.ForbiddenException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ColumnService {
    private ColumnRepository columnRepository;
    private BoardService boardService;
    private UserService userService;

    public void addColumn(Long boardId, StatusEnum statusEnum, User user) {
        Board checkBoard = boardService.findById(boardId);
        User checkUser = userService.findById(user.getId());

        // 컬럼 생성은 ADMIN만 할 수 있음 - 권한 제한
        if(checkUser.getUserAuth().equals(UserAuth.USER)){
            throw new ForbiddenException("ADMIN 사용자만이 컬럼을 추가할 수 있습니다.");
        }

        // 이미 존재하는 컬럼인지 확인
        if(columnRepository.findByStatus(statusEnum).isPresent()){
            throw new ConflictException("이미 존재하는 컬럼 입니다.");
        }

        Column column=new Column(checkBoard,statusEnum,checkUser);
        columnRepository.save(column);
    }

    public void deleteColumn(Long columnId, User user){
        Column checkColumn = findById(columnId);
        User checkUser = userService.findById(user.getId());

        // 해당 컬럼을 생성한 ADMIN만이 삭제 가능
        if(!checkColumn.getUser().getId().equals(checkUser.getId())){
            throw new ForbiddenException("해당 컬럼을 생성한 ADMIN과 일치하지 않아 요청을 처리할 수 없습니다.");
        }

        columnRepository.delete(checkColumn);
    }

    public List<ColumnResponseDto> getAllColumns(Long boardId) {
        Board checkBoard = boardService.findById(boardId);
        List<Column> columnList = columnRepository.findAllByBoard(checkBoard);

        List<ColumnResponseDto> responseDtos = new ArrayList<>();

        for (Column column : columnList) {
            ColumnResponseDto responseDto = ColumnResponseDto.builder()
                    .status(column.getStatus())
                    .createdAt(column.getCreatedAt())
                    .modifiedAt(column.getModifiedAt())
                    .build();

            responseDtos.add(responseDto);
        }
        return responseDtos;
    }

    public void moveColumn(Long columnId, ColumnMoveRequestDto requestDto, User user){
        Column checkColumn = findById(columnId);
        User checkUser = userService.findById(user.getId());

        // 해당 컬럼을 생성한 ADMIN만이 이동/수정 가능
        if(!checkColumn.getUser().getId().equals(checkUser.getId())){
            throw new ForbiddenException("해당 컬럼을 생성한 ADMIN과 일치하지 않아 요청을 처리할 수 없습니다.");
        }

        // 컬럼 index 순서대로 먼저 정렬
        List<Column> columnList = columnRepository.findAllByOrderByColumnIndex();
        if(requestDto.getColumnIndex()>columnList.size()){

        }
    }

    public Column findById(Long columnId) {
        return columnRepository.findById(columnId).orElseThrow(
                () -> new BadRequestException("해당 컬럼아이디가 존재하지 않습니다.")
        );
    }
}
