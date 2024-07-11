package com.sparta.refrigerator.column.controller;

import com.sparta.refrigerator.column.dto.ColumnRequestDto;
import com.sparta.refrigerator.column.service.ColumnService;
import com.sparta.refrigerator.common.response.StatusCommonResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
