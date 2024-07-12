package com.sparta.refrigerator.column.controller;

import com.sparta.refrigerator.auth.service.UserDetailsImpl;
import com.sparta.refrigerator.column.dto.ColumnMoveRequestDto;
import com.sparta.refrigerator.column.dto.ColumnRequestDto;
import com.sparta.refrigerator.column.dto.ColumnResponseDto;
import com.sparta.refrigerator.column.service.ColumnService;
import com.sparta.refrigerator.common.response.DataCommonResponse;
import com.sparta.refrigerator.common.response.StatusCommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ColumnController {

    private final ColumnService columnService;

    @PostMapping("/admin/boards/{boardId}/columns")
    public ResponseEntity<StatusCommonResponse> addColumn(@PathVariable(name = "boardId") Long boardId,
        @RequestBody @Valid ColumnRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        columnService.addColumn(boardId, requestDto, userDetails.getUser());
        StatusCommonResponse response = new StatusCommonResponse(201, "컬럼 추가되었습니다.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/admin/columns/{columnId}")
    public ResponseEntity<StatusCommonResponse> deleteColumn(@PathVariable(name = "columnId") Long columnId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        columnService.deleteColumn(columnId, userDetails.getUser());
        StatusCommonResponse response = new StatusCommonResponse(204, "컬럼 삭제되었습니다.");
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/boards/{boardId}/columns")
    public ResponseEntity<DataCommonResponse<List<ColumnResponseDto>>> getAllColumns(
        @PathVariable(name = "boardId") Long boardId) {
        List<ColumnResponseDto> columns = columnService.getAllColumnsOrderByIndex(boardId);
        DataCommonResponse<List<ColumnResponseDto>> response = new DataCommonResponse<>(200, "컬럼 전체 조회 성공하였습니다.", columns);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }


    @PutMapping("/admin/boards/{boardId}/columns/{columnId}/transfer")
    public ResponseEntity<StatusCommonResponse> moveColumn(@PathVariable(name = "boardId") Long boardId,
        @PathVariable(name = "columnId") Long columnId,
        @RequestBody ColumnMoveRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        columnService.moveColumn(boardId, columnId, requestDto, userDetails.getUser());
        StatusCommonResponse response = new StatusCommonResponse(200, "컬럼 이동되었습니다.");
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
