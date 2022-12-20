package com.example.oauthjwt.configure.jwt;

import com.example.oauthjwt.configure.CustomUserDetailsService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 토큰 발급 및 검증을 위한 Service
 */
@Slf4j
@Component
public class TokenService {
    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.expiredTime}")
    private long expiredTime;

    @Value("${jwt.refreshTime}")
    private long refreshTime;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

//    Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

    /**
     * JWT Token 생성
     * @param authentication
     * @return
     */
    public Token generateToken(Authentication authentication){

        Map<String, Object> headers = new HashMap<>();

        //Type 설정
        headers.put("typ", "JWT");

        //PayLoad
        Claims claims = Jwts.claims()
                .setSubject(authentication.getName())
                .setIssuer("kks")
                ;

        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();

        claims.put("name", oAuth2User.getAttributes().get("name"));
        claims.put("picture", oAuth2User.getAttributes().get("picture"));
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expiredTime);
        Date refreshDate = new Date(now.getTime() + refreshTime);

        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        return new Token(Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact(),
            Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(refreshDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact());
    }

    public boolean validateToken(String token) throws Exception {
        try {
            Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build().parseClaimsJws(token);
            return claims.getBody()
                    .getExpiration()
                    .after(new Date());
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
            throw new Exception(ex);
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
            throw new Exception(ex);
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
            throw new Exception(ex);
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
            return false;
//            throw new Exception(ex);
        }
    }

    public Authentication getAuthentication(String token) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUserName(String token){
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        String username = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        return username;
    }
}
