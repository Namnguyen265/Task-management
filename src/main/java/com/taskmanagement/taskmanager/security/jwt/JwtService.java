package com.taskmanagement.taskmanager.security.jwt;

import com.taskmanagement.taskmanager.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

//    @Value("${app.jwt.secret}")
//    private String jwtSecret;
//
//    @Value("${app.jwt.expiration}") // milliseconds
//    private long jwtExpirationMs;
//
//    private Key getSigningKey() {
//        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
//    }
//
//    // ===============================
//    // Generate token
//    // ===============================
//    public String generateToken(String username) {
//        return Jwts.builder()
//                .setSubject(username)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
//                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    // ===============================
//    // Extract Username
//    // ===============================
//    public String extractUsername(String token) {
//        return getClaims(token).getSubject();
//    }
//
//    // ===============================
//    // Validate token
//    // ===============================
//    public boolean validateToken(String token) {
//        try {
//            getClaims(token);
//            return true;
//        } catch (ExpiredJwtException e) {
//            System.out.println("JWT expired: " + e.getMessage());
//        } catch (UnsupportedJwtException e) {
//            System.out.println("JWT unsupported: " + e.getMessage());
//        } catch (MalformedJwtException e) {
//            System.out.println("Malformed JWT: " + e.getMessage());
//        } catch (SecurityException e) {
//            System.out.println("Invalid signature: " + e.getMessage());
//        } catch (IllegalArgumentException e) {
//            System.out.println("JWT claims string empty: " + e.getMessage());
//        }
//        return false;
//    }
//
//    // ===============================
//    // Parse claims
//    // ===============================
//    private Claims getClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(getSigningKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    public boolean isTokenValid(String token, UserDetails userDetails) {
//        final String username = extractUsername(token);
//        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }
//
//    private boolean isTokenExpired(String token) {
//        return extractExpiration(token).before(new Date());
//    }
//
//    private Date extractExpiration(String token) {
//        return extractClaim(token, Claims::getExpiration);
//    }
//
//    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }

    private final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250655368566D5970";

    // 🔑 Lấy key ký JWT
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 📌 Tạo token
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // 24h
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(User user) {

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(new ArrayList<>())   // nếu có roles thì thêm vào
                .build();

        return generateToken(userDetails);
    }

    public String generateAccessToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000)) // 15 phút
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000)) // 7 ngày
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 📌 Extract username từ token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 📌 Kiểm tra token hợp lệ
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // 📌 Kiểm tra token hết hạn
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 📌 Lấy 1 claim bất kỳ
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 📌 ⭐ Quan trọng – phương thức này bạn đang thiếu
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
