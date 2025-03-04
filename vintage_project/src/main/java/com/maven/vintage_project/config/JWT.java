/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.maven.vintage_project.config;

import com.maven.vintage_project.exceptions.ExceptionLogger;
import com.maven.vintage_project.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author herma
 */
public class JWT {
    private static final String SIGN = "K4BPv/iklx4o7HQdNRiXD142y4oFiJolZMkd5pLTx1o=";
    private static ExceptionLogger exceptionLogger = new ExceptionLogger(JWT.class);

    
    public static SecretKeySpec getSigningKey() {
        byte[] decodedKey = Base64.getDecoder().decode(SIGN);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
    }
    
    public static String createJWT(User u) {
        Instant now = Instant.now();

        String token = Jwts.builder()
                .setIssuer("vintage_shop")
                .setSubject("create")
                .claim("id", u.getId())
                .claim("isAdmin", u.getIsAdmin())
                .claim("createdAt", u.getCreatedAt())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        return token;
    }
    
    public static int validateJWT(String jwt) {
        try {
            Jws<Claims> result;
            result = Jwts.parser().setSigningKey(getSigningKey()).parseClaimsJws(jwt);
            
            Claims claims = result.getBody();
            int id = claims.get("id", Integer.class);
            User u = new User(id);
            
            Date expiration = claims.getExpiration();
            if (expiration != null && expiration.before(new Date())) {
            return 3; // Token expired
            }

            if (u.getId() == id) {
                return 1;
            } else {
                return 2; //Ez akkor történik amikor egy érvénytelen tokent akarunk validáltatni
            }
        } catch (Exception e) {
            exceptionLogger.errorLog(e);
            return 3; //Akkor történik ha lejárt a JWT-k
        }

    }
    
    public static Boolean isAdmin(String jwt){
        Jws<Claims> result;
        result = Jwts.parser().setSigningKey(getSigningKey()).parseClaimsJws(jwt);
        
        Boolean isAdmin = result.getBody().get("isAdmin", Boolean.class);
        
        return isAdmin;
    }
    
    public static Integer getUserIdByToken(String jwt){
        Jws<Claims> result;
        result = Jwts.parser().setSigningKey(getSigningKey()).parseClaimsJws(jwt);
        
        int userId = result.getBody().get("id", Integer.class);
        
        return userId;
    }
}
