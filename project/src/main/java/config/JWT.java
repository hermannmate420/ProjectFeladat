/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package config;

import com.iakk.exception.ExceptionLogger;
import com.iakk.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

/**
 *
 * @author herma
 */
public class JWT {
    private static final String SIGN = "K4BPv/iklx4o7HQdNRiXD142y4oFiJolZMkd5pLTx1o=";
    private static final byte[] SECRET = Base64.getDecoder().decode(SIGN);
    private static ExceptionLogger exceptionLogger = new ExceptionLogger(JWT.class);

    
    
    
    public static String createJWT(User u) {
        Instant now = Instant.now();

        String token = Jwts.builder()
                .setIssuer("vintage_shop")
                .setSubject("create")
                .claim("id", u.getId())
                .claim("isAdmin", u.getIsAdmin())
                .claim("createdAt", u.getCreatedAt())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(1, ChronoUnit.DAYS)))
                .signWith(Keys.hmacShaKeyFor(SECRET), SignatureAlgorithm.HS256)
                .compact();

        return token;
    }
    
    public static int validateJWT(String jwt) {
        try {
            Jws<Claims> result;
            result = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(SECRET)).build().parseClaimsJws(jwt);
            int id = result.getBody().get("id", Integer.class);
            User u = new User(id);

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
        result = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(SECRET)).build().parseClaimsJws(jwt);
        
        Boolean isAdmin = result.getBody().get("isAdmin", Boolean.class);
        
        return isAdmin;
    }
    
    public static Integer getUserIdByToken(String jwt){
        Jws<Claims> result;
        result = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(SECRET)).build().parseClaimsJws(jwt);
        
        int userId = result.getBody().get("id", Integer.class);
        
        return userId;
    }
}
