package at.ac.tuwien.sepr.groupphase.backend.security;

import at.ac.tuwien.sepr.groupphase.backend.config.properties.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JwtTokenizer {

    private final SecurityProperties securityProperties;

    public JwtTokenizer(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    public String getAuthToken(String user, List<String> roles) {
        byte[] signingKey = securityProperties.getJwtSecret().getBytes();
        String token = Jwts.builder()
            .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS512)
            .setHeaderParam("typ", securityProperties.getJwtType())
            .setIssuer(securityProperties.getJwtIssuer())
            .setAudience(securityProperties.getJwtAudience())
            .setSubject(user)
            .setExpiration(new Date(System.currentTimeMillis() + securityProperties.getJwtExpirationTime()))
            .claim("rol", roles)
            .compact();
        return securityProperties.getAuthTokenPrefix() + token;
    }

    public String buildVerificationToken(
        String useremail
    ) {
        byte[] signingKey = securityProperties.getJwtSecret().getBytes();
        return Jwts
                   .builder()
                   .setIssuer(securityProperties.getJwtIssuer())
                   .setSubject(useremail)
                   .setIssuedAt(new Date(System.currentTimeMillis()))
                   .setExpiration(new Date(System.currentTimeMillis() + securityProperties.getJwtExpirationTime()))
                   .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS512)
                   .compact();
    }

    public String extractUsernameFromVerificationToken(String token) {
        byte[] signingKey = securityProperties.getJwtSecret().getBytes();
        Claims claims = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(signingKey)).build()
                            .parseSignedClaims(token)
                            .getPayload();
        Date expiration = claims.getExpiration();
        String username = claims.getSubject();
        if (expiration == null || username == null || expiration.before(new Date(System.currentTimeMillis()))) {
            return null;
        }
        return username;
    }


}
