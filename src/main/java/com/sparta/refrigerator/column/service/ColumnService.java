package com.sparta.refrigerator.column.service;

import com.sparta.refrigerator.auth.entity.User;
import com.sparta.refrigerator.auth.enumeration.UserAuth;
import com.sparta.refrigerator.auth.service.UserService;
import com.sparta.refrigerator.board.entity.Board;
import com.sparta.refrigerator.board.service.BoardService;
import com.sparta.refrigerator.column.dto.ColumnMoveRequestDto;
import com.sparta.refrigerator.column.dto.ColumnRequestDto;
import com.sparta.refrigerator.column.dto.ColumnResponseDto;
import com.sparta.refrigerator.column.entity.Column;
import com.sparta.refrigerator.column.repository.ColumnRepository;
import com.sparta.refrigerator.exception.BadRequestException;
import com.sparta.refrigerator.exception.ConflictException;
import com.sparta.refrigerator.exception.DataNotFoundException;
import com.sparta.refrigerator.exception.ForbiddenException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ColumnService {

    private ColumnRepository columnRepository;
    private BoardService boardService;
    private UserService userService;

    @Transactional
    public void addColumn(Long boardId, ColumnRequestDto requestDto, User user) {
        Board checkBoard = boardService.findById(boardId);
        User checkUser = userService.findById(user.getId());

        // 컬럼 생성은 ADMIN만 할 수 있음 - 권한 제한
        if (checkUser.getAuth().equals(UserAuth.USER)) {
            throw new ForbiddenException("ADMIN 사용자만이 컬럼을 추가할 수 있습니다.");
        }

        // 이미 존재하는 컬럼인지 확인
        if (columnRepository.findByColumnName(requestDto.getColumnName()).isPresent()) {
            throw new ConflictException("이미 존재하는 컬럼 입니다.");
        }

        // 컬럼 인덱스 최댓값 확인
        Long maxIndex = columnRepository.findMaxColumnIndexByBoard(checkBoard);
        maxIndex = (maxIndex == null) ? 1L : maxIndex + 1;

        Column column = new Column(checkBoard, requestDto, checkUser, maxIndex);
        columnRepository.save(column);
    }

    @Transactional
    public void deleteColumn(Long columnId, User user) {
        Column checkColumn = findById(columnId);
        User checkUser = userService.findById(user.getId());

        // 해당 컬럼을 생성한 ADMIN만이 삭제 가능
        if (!checkColumn.getUser().getId().equals(checkUser.getId())) {
            throw new ForbiddenException("해당 컬럼을 생성한 ADMIN과 일치하지 않아 요청을 처리할 수 없습니다.");
        }

        columnRepository.delete(checkColumn);
    }

    @Transactional(readOnly = true)
    public List<ColumnResponseDto> getAllColumnsOrderByIndex(Long boardId) {
        Board checkBoard = boardService.findById(boardId);
        List<Column> columnList = columnRepository.findAllByBoardOrderByColumnIndex(checkBoard);

        List<ColumnResponseDto> responseDtos = new ArrayList<>();

        for (Column column : columnList) {
            ColumnResponseDto responseDto = ColumnResponseDto.builder()
                .columnName(column.getColumnName())
                .columnIndex(column.getColumnIndex())
                .build();

            responseDtos.add(responseDto);
        }
        return responseDtos;
    }

    @Transactional
    public void moveColumn(Long boardId, Long columnId, ColumnMoveRequestDto requestDto,
        User user) {
        Board checkBoard = boardService.findById(boardId);
        Column checkColumn = findById(columnId);
        User checkUser = userService.findById(user.getId());

        // 해당 컬럼을 생성한 ADMIN만이 이동/수정 가능
        if (!checkColumn.getUser().getId().equals(checkUser.getId())) {
            throw new ForbiddenException("해당 컬럼을 생성한 ADMIN과 일치하지 않아 요청을 처리할 수 없습니다.");
        }

        // 요청으로부터 이동할 컬럼의 인덱스와 목표 인덱스를 가져옴
        Long currentIndex = checkColumn.getColumnIndex();
        Long targetIndex = requestDto.getColumnIndex();

        // 현재 인덱스와 목표 인덱스가 같으면 아무 작업도 하지 않음
        if (currentIndex == targetIndex) {
            return;
        }

        // 모든 컬럼을 현재 보드에서 조회
        List<Column> columnList = columnRepository.findAllByBoardOrderByColumnIndex(checkBoard);

        // 이동할 컬럼을 찾아서 목표 인덱스로 이동
        Column columnToMove = null;
        for (Column column : columnList) {
            if (column.getId().equals(columnId)) {
                columnToMove = column;
                break;
            }
        }

        // 컬럼이 null인 경우 예외 처리
        if (columnToMove == null) {
            throw new DataNotFoundException("이동할 컬럼을 찾을 수 없습니다.");
        }

        // 목표 인덱스 범위 확인
        if (targetIndex < 0 || targetIndex >= columnList.size()) {
            throw new BadRequestException("목표 인덱스가 범위를 벗어납니다.");
        }

        // 현재 인덱스에서 제거하고 목표 인덱스에 추가
        columnList.remove(columnToMove);
        columnList.add(Math.toIntExact(targetIndex), columnToMove);

        // 변경된 순서대로 모든 컬럼을 저장
        for (int i = 0; i < columnList.size(); i++) {
            Column column = columnList.get(i);
            column.updateIndex(i);
            columnRepository.save(column);
        }
    }

    @Transactional(readOnly = true)
    public Column findById(Long columnId) {
        return columnRepository.findById(columnId).orElseThrow(
            () -> new BadRequestException("해당 컬럼아이디가 존재하지 않습니다.")
        );
    }
}
