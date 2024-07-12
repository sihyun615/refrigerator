package com.sparta.refrigerator.auth.service;

import com.sparta.refrigerator.auth.dto.PasswordRequestDto;
import com.sparta.refrigerator.auth.dto.SignupRequestDto;
import com.sparta.refrigerator.auth.entity.User;
import com.sparta.refrigerator.auth.enumeration.UserAuth;
import com.sparta.refrigerator.auth.jwt.JwtUtil;
import com.sparta.refrigerator.auth.repository.UserRepository;
import com.sparta.refrigerator.exception.BadRequestException;
import com.sparta.refrigerator.exception.ConflictException;
import com.sparta.refrigerator.exception.DataNotFoundException;
import com.sparta.refrigerator.exception.ErrorCode;
import com.sparta.refrigerator.exception.UnauthorizedException;
import com.sparta.refrigerator.exception.UserException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    final private UserRepository userRepository;
    final private PasswordEncoder passwordEncoder;
    final private JwtUtil jwtUtil;

    @Value("${admin.token}")
    private String ADMIN_TOKEN;

    @Transactional
    public void signup(SignupRequestDto requestDto) {
        String userName = requestDto.getUserName();
        String password = passwordEncoder.encode(requestDto.getPassword());

        Optional<User> user = userRepository.findByUserName(userName);
        if (user.isPresent()) {
            throw new ConflictException("이미 회원가입한 계정입니다.");
        }


        UserAuth auth = UserAuth.ACTIVE;
        if (requestDto.isAdmin()) {
            if (!ADMIN_TOKEN.equals(requestDto.getAdminToken())) {
                throw new UserException(ErrorCode.INCORRECT_ADMIN);
            }
            auth = UserAuth.MANAGER;
        }

        userRepository.save(new User(userName, password, auth));
    }


    @Transactional
    public void logout(String userName) {
        User user = userRepository.findByUserName(userName).orElseThrow(() -> new DataNotFoundException("해당 회원은 존재하지 않습니다."));

        user.updateRefresh(null);
    }

    @Transactional
    public void withdrawal(String userName, PasswordRequestDto requestDto) {
        User user = userRepository.findByUserName(userName).orElseThrow(() -> new DataNotFoundException("해당 회원은 존재하지 않습니다."));

        if (!checkPassword(requestDto.getPassword(), user.getPassword())) {
            throw new BadRequestException("비밀번호를 확인해주세요.");
        }

        if (user.getAuth() == UserAuth.WITHDRAW) {
            throw new ConflictException("이미 탈퇴한 회원입니다.");
        }

        UserAuth userAuth = UserAuth.WITHDRAW;
        user.updateUserAuth(userAuth);
    }

    @Transactional
    public HttpHeaders refresh(HttpServletRequest request){
        String tokenValue = request.getHeader("Refresh-Token");
        if (!StringUtils.hasText(tokenValue)) {
            throw new BadRequestException("잘못된 요청입니다.");
        }
        jwtUtil.checkTokenExpiration(tokenValue);
        if (!jwtUtil.validateToken(tokenValue)) {
            throw new UnauthorizedException("토큰 검증 실패");
        }
        Claims info = jwtUtil.getClaimsFromToken(tokenValue);
        User user = userRepository.findByUserName(info.getSubject())
            .orElseThrow(() -> new DataNotFoundException("해당 회원은 존재하지 않습니다."));
        if (!user.getRefreshToken().equals(tokenValue)) {
            throw new UnauthorizedException("토큰 검증 실패");
        }
        String accessToken = jwtUtil.createAccessToken(user.getUserName(), user.getAuth());
        String refreshToken = jwtUtil.createRefreshToken(user.getUserName(), user.getAuth());
        user.updateRefresh(refreshToken);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Refresh-Token", refreshToken);
        return headers;
    }

    public boolean checkPassword(String requestPassword, String userPassword) {
        return passwordEncoder.matches(requestPassword, userPassword);
    }

    public User findById(Long userId){
        return userRepository.findById(userId).orElseThrow(()-> new DataNotFoundException("해당 사용자를 찾을 수 없습니다."));
    }
}
