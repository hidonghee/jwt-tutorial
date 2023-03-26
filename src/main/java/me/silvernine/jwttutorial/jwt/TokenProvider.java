package me.silvernine.jwttutorial.jwt;



import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

// TokenProvider 클래스는 Token의 생성, 유효성 검증을 담당한다.
// 즉 Token의 관리자로 특정 티켓의 판매센터라고 생각하면 쉬움.

@Component
public class TokenProvider implements InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
    private static final String AUTHORITIES_KEY = "auth";


    private final String secret;
    private final long tokenValidityInMilliseconds;
    private Key key;
    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds){
        this.secret = secret;
        this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
    }

    /* InitializingBean 인터페이스를 구현해서 아래의 함수를 구현을 해야한다.
    빈이 생성이 되고 주입(secretkey?)을 받은 후에 secret 값을 Base64 Decode해서 key 변수에 할당.
    application.yml에 정의했던 jwt에서 hs512을 사용하기 위한 64byte의 base64로 인코딩된 값을 디코딩 한다.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        byte [] keyBytes = Decoders.BASE64.decode(secret);
        this.key= Keys.hmacShaKeyFor(keyBytes);

    }

    /*
    Authentication 객체의 권한정보를 이용해서 토큰을 생성하는 createToken 메소드를 추가
     */
    public String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));


        // 토큰이 생성될 때 그 시간에서 설정한 만료기간을 더해 토큰 만료기한 설정.
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);


        // JWT 토큰을 생성해서 return
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    // Token에 담겨있는 정보를 이용해서 Authentication 객체를 리턴하는 메소드 생성
    public Authentication getAuthentication(String token){
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        Collection <? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails를 구현한 User 객체를 생성
        User principal = new User(claims.getSubject(),"",authorities);
        return new UsernamePasswordAuthenticationToken(principal,token,authorities);
    }

    /*
    토큰의 유효성 검사를 수행 하는 validateToken 메소드를 추가한다.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            logger.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            logger.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            logger.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            logger.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}
