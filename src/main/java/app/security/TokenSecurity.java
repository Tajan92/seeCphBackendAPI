package app.security;

import app.dto.user.UserDTOResponse;
import app.enums.UserRole;
import app.exceptions.SecurityValidationException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.time.Instant;

public class TokenSecurity {
    public String createToken(UserDTOResponse user, String issuer, long expirationTime, String secret) {
        if (expirationTime <= 0 || issuer == null || issuer.isBlank()) {
            throw new IllegalArgumentException("Issuer and positive expiration are required");
        }
        Instant now = Instant.now();
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(String.valueOf(user.id()))
                .withClaim("email", user.email())
                .withClaim("name", user.name())
                .withClaim("role", user.userRole().name())
                .withIssuedAt(now)
                .withExpiresAt(now.plusMillis(expirationTime))
                .sign(signingAlgorithm(secret));
    }

    public UserDTOResponse verifyAndGetUser(String token, String issuer, String secret) {
        try {
            DecodedJWT jwt = JWT.require(signingAlgorithm(secret))
                    .withIssuer(issuer)
                    .withClaimPresence("exp")
                    .withClaimPresence("email")
                    .withClaimPresence("role")
                    .build()
                    .verify(token);

            int id = Integer.parseInt(jwt.getSubject());
            UserRole role = UserRole.valueOf(jwt.getClaim("role").asString());

            return new UserDTOResponse(
                    id,
                    jwt.getClaim("name").asString(),
                    jwt.getClaim("email").asString(),
                    null, // phone is not stored in the token
                    role);

        } catch (JWTVerificationException | IllegalArgumentException | NullPointerException e) {
            throw new SecurityValidationException(401, "Invalid or expired token");
        }
    }

    private Algorithm signingAlgorithm(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("SECRET_KEY must be configured");
        }
        return Algorithm.HMAC256(secret);
    }
}
