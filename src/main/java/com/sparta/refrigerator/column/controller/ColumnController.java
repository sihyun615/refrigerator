package com.sparta.refrigerator.column.controller;

import com.sparta.refrigerator.column.dto.ColumnMoveRequestDto;
import com.sparta.refrigerator.column.dto.ColumnResponseDto;
import com.sparta.refrigerator.column.entity.StatusEnum;
import com.sparta.refrigerator.column.service.ColumnService;
import com.sparta.refrigerator.common.response.DataCommonResponse;
import com.sparta.refrigerator.common.response.StatusCommonResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ColumnController {
    private ColumnService columnService;

    @PostMapping("/admin/boards/{boardId}/columns")
    public StatusCommonResponse addColumn(@PathVariable(name = "boardId") Long boardId,
                                          @RequestBody StatusEnum statusEnum,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails){
        columnService.addColumn(boardId,statusEnum,userDetails.getUser());
        return new StatusCommonResponse(201,"컬럼 추가되었습니다.");
    }

    @DeleteMapping("/admin/columns/{columnId}")
    public StatusCommonResponse deleteColumn(@PathVariable(name = "columnId") Long columnId,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails){
        columnService.deleteColumn(columnId,userDetails.getUser());
        return new StatusCommonResponse(204, "컬럼 삭제되었습니다.");
    }

    @GetMapping("/boards/{boardId}/columns")
    public DataCommonResponse<List<ColumnResponseDto>> getAllColumns(@PathVariable(name = "boardId") Long boardId) {
        List<ColumnResponseDto> columns = columnService.getAllColumnsOrderByStatus(boardId);
        return new DataCommonResponse<>(200, "컬럼 전체 조회 성공하였습니다.", columns);
    }


    @PutMapping("/admin/columns/{columnId}/transfer")
    public StatusCommonResponse moveColumn(@PathVariable(name = "columnId") Long columnId,
                                           @RequestBody ColumnMoveRequestDto requestDto,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails){
        columnService.moveColumn(columnId,requestDto,userDetails.getUser());
        return new StatusCommonResponse(200,"컬럼 이동되었습니다.");
    }
}
