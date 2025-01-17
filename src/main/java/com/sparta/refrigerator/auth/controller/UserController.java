package com.sparta.refrigerator.auth.controller;

import com.sparta.refrigerator.auth.dto.PasswordRequestDto;
import com.sparta.refrigerator.auth.dto.SignupRequestDto;
import com.sparta.refrigerator.auth.service.UserDetailsImpl;
import com.sparta.refrigerator.auth.service.UserService;
import com.sparta.refrigerator.common.response.StatusCommonResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/login-page")
    public String loginpage(){
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }


    @PostMapping("/signup")
    public String signup(@Valid SignupRequestDto requestDto, BindingResult bindingResult) {
        // Validation 예외처리
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if(fieldErrors.size() > 0) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
            }
            return "redirect:/users/signup";
        }
        userService.signup(requestDto);

        return "redirect:/users/login-page";
    }

    @PostMapping("/logout")
    public ResponseEntity<StatusCommonResponse> logout(@AuthenticationPrincipal UserDetailsImpl userDetails){
        userService.logout(userDetails.getUser().getUserName());

        StatusCommonResponse commonResponse = new StatusCommonResponse(200, "로그아웃 성공");

        return new ResponseEntity<>(commonResponse, HttpStatus.OK);
    }

    @PutMapping("/withdrawal")
    public ResponseEntity<StatusCommonResponse> withdrawal(@Valid @RequestBody PasswordRequestDto requestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        userService.withdrawal(userDetails.getUser().getUserName(), requestDto);

        StatusCommonResponse commonResponse = new StatusCommonResponse(200, "회원탈퇴 성공");

        return new ResponseEntity<>(commonResponse, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<StatusCommonResponse> refresh(HttpServletRequest request) {
        HttpHeaders headers = userService.refresh(request);

        StatusCommonResponse commonResponse = new StatusCommonResponse(200, "RefreshToken 인증 성공");

        return new ResponseEntity<>(commonResponse, headers, HttpStatus.OK);
    }

}
