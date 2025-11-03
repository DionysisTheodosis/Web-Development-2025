package gr.aegean.icsd.autorepair.shared.security.service;

import gr.aegean.icsd.autorepair.shared.security.TokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {
    private final String secretKey;
    @Value("${isHttpsEnabled}")
    private boolean isHttpsEnabled;
    public JwtService(Environment env) {
        this.secretKey = env.getProperty("JWT_SECRET_KEY");
        if (secretKey== null || secretKey.isBlank()) {
            throw new IllegalStateException("JWT_SECRET_KEY environment variable is not set!");
        }
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        Instant now = Instant.now();
        Instant expirationTime = now.plusSeconds((long)60 * 30);
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expirationTime))
                .and()
                .signWith(getKey())
                .compact();

    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        Instant expiration = extractExpiration(token).toInstant();
        return expiration.isBefore(Instant.now());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public void attachJwtCookie(HttpServletResponse response, String token) {
        Cookie jwtCookie = new Cookie("jwtToken", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(60 * 30); // 30 min
        jwtCookie.setSecure(isHttpsEnabled);
        //jwtCookie.setAttribute("SameSite", "Strict");
        response.addCookie(jwtCookie);
    }

    public String validateAndExtractUsername(String token) throws TokenExpiredException {
        if (isTokenExpired(token)) {
            throw new TokenExpiredException();
        }

        return extractUserName(token);
    }

    public void clearJwtCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwtToken", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setSecure(isHttpsEnabled);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }
}