package com.sparta.refrigerator.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.refrigerator.auth.dto.LoginRequestDto;
import com.sparta.refrigerator.auth.entity.User;
import com.sparta.refrigerator.auth.enumeration.UserAuth;
import com.sparta.refrigerator.auth.jwt.JwtUtil;
import com.sparta.refrigerator.auth.repository.UserRepository;
import com.sparta.refrigerator.auth.service.UserDetailsImpl;
import com.sparta.refrigerator.common.response.StatusCommonResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Slf4j(topic = "JwtAuthenticationFilter")
@Component
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;

        setFilterProcessesUrl("/users/login");
    }

    @Override
    public Authentication attemptAuthentication(
        HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("잘못된 http 요청입니다.");
        }

        try {

            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            return getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.getUserName(), requestDto.getPassword(), null)
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
        FilterChain chain, Authentication authResult) throws IOException {

        String userName = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
        UserAuth userAuth = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getAuth();

        Optional<User> user = userRepository.findByUserName(userName);

        if (user.isEmpty() || user.get().getAuth().equals(UserAuth.WITHDRAW)) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("유효하지 않은 사용자 정보입니다.");

            return;
        }

        String accessToken = jwtUtil.createAccessToken(userName, userAuth);
        String refreshToken = jwtUtil.createRefreshToken(userName, userAuth);
        ResponseCookie refreshTokenCookie = jwtUtil.generateRefreshTokenCookie(refreshToken);

        user.get().updateRefresh(refreshToken);
        userRepository.save(user.get());

        StatusCommonResponse commonResponse = new StatusCommonResponse(200, "로그인 성공");

        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(commonResponse));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        responseSetting(response, 401, "아이디와 비밀번호가 일치하지 않습니다.");
    }


    private void responseSetting(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write(message);

    }

}
