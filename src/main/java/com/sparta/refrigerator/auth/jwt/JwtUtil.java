package com.sparta.refrigerator.auth.jwt;

import com.sparta.refrigerator.auth.enumeration.UserAuth;
import com.sparta.refrigerator.exception.TokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j(topic = "JwtUtil")
@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${access.token.expiration}")
    private long ACCESS_TOKEN_TIME;

    @Value("${refresh.token.expiration}")
    private long REFRESH_TOKEN_TIME;


    public static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";


    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    private static Key key;
    private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // 딱 한 번만 받아오면 되는 값을 사용할 때마다 요청을 새로고침하는 오류를 방지하기 위해
    @PostConstruct
    public void init() {
        // Base64로 디코딩
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public static String createToken(String userName, UserAuth userAuth, long expiration) {
        Date date = new Date();

        return Jwts.builder()
            .setSubject(userName)
            .claim("userName", userName)
            .claim(AUTHORIZATION_HEADER, userAuth.toString())
            .setExpiration(new Date(date.getTime() + expiration))
            .setIssuedAt(date)
            .signWith(key, signatureAlgorithm)
            .compact();
    }

    public String createAccessToken(String userName, UserAuth userAuth){

        return createToken(userName, userAuth, ACCESS_TOKEN_TIME);
    }

    public String createRefreshToken(String userName, UserAuth userAuth){

        return createToken(userName, userAuth, REFRESH_TOKEN_TIME);
    }

    public ResponseCookie generateRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)
            .maxAge(REFRESH_TOKEN_TIME / 1000)
            .path("/")
            .sameSite("Strict")
            .build();
    }

    public static String getJwtTokenFromHeader(HttpServletRequest request) {
        // 헤더에서 'Authorization'의 값을 가져온다.
        String bearerToken = request.getHeader(JwtUtil.AUTHORIZATION_HEADER);

        // bearerToken이 null이나 빈칸이 아니고, 'Bearer '로 시작한다며
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7); // 'Bearer ' 잘라서 반환
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public void checkTokenExpiration(String token) throws TokenExpiredException {

        try {

            Claims claims = getClaimsFromToken(token);
            Date date = claims.getExpiration();
            Date now = new Date();

            if (date != null && date.before(now)) {
                throw new TokenExpiredException("토큰이 만료되었습니다.");
            }

        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException("토큰이 만료되었습니다.");
        }

    }

}
