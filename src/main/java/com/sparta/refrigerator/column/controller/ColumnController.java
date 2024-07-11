package com.sparta.refrigerator.column.controller;

import com.sparta.refrigerator.column.dto.ColumnRequestDto;
import com.sparta.refrigerator.column.service.ColumnService;
import com.sparta.refrigerator.common.response.StatusCommonResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin")
public class ColumnController {
    private ColumnService columnService;

    @PostMapping("/boards/{boardId}/column")
    public StatusCommonResponse addColumn(@PathVariable(name = "boardId") Long boardId,
                                          @Valid @RequestBody ColumnRequestDto requestDto,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails){
        columnService.addColumn(boardId,requestDto,userDetails.getUser());
        return new StatusCommonResponse(201,"컬럼 작성되었습니다.");
    }

    @DeleteMapping("columns/{columnId}")
    public StatusCommonResponse deleteColumn(@PathVariable(name = "columnId") Long columnId
                                             @AuthenticationPrincipal UserDetailsImpl userDetails){
        columnService.deleteColumn(columnId,userDetails.getUser());
        return new StatusCommonResponse(204, "컬럼 삭제되었습니다.");
    }
}
