package com.github.jvogit.springreactnextjs.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.github.jvogit.springreactnextjs.model.User;

import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public final class TestUtil {

    public static final UUID TEST_ID = UUID.randomUUID();
    public static final String TEST_USERNAME = "testUsername";
    public static final String TEST_EMAIL = "testEmail";
    public static final String TEST_PASSWORD = "testEmail";
    public static final int TEST_TOKEN_VERSION = 1;
    public static final String TEST_ISSUER = "testIssuer";
    public static final Algorithm TEST_REFRESH_TOKEN_ALGO = Algorithm.HMAC512("secretRefreshToken");
    public static final Algorithm TEST_ACCESS_TOKEN_ALGO = Algorithm.HMAC512("secretAccessToken");
    public static final JWTVerifier TEST_REFRESH_TOKEN_VERIFIER = JWT.require(TEST_REFRESH_TOKEN_ALGO)
            .withIssuer(TEST_ISSUER)
            .build();
    public static final JWTVerifier TEST_ACCESS_TOKEN_VERIFIER = JWT.require(TEST_ACCESS_TOKEN_ALGO)
            .withIssuer(TEST_ISSUER)
            .build();

    private TestUtil() {
    }

    public static User mockUser() {
        return User.builder()
                .id(TEST_ID)
                .username(TEST_USERNAME)
                .password(TEST_PASSWORD)
                .email(TEST_EMAIL)
                .tokenVersion(TEST_TOKEN_VERSION)
                .build();
    }

    public static String generateMockRefreshToken() {
        final Instant now = Instant.now();
        final Instant expiresAt = now.plus(7, ChronoUnit.DAYS);

        return JWT.create()
                .withIssuer(TEST_ISSUER)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiresAt))
                .withSubject(TEST_ID.toString())
                .withClaim("tokenVersion", TEST_TOKEN_VERSION)
                .sign(TEST_REFRESH_TOKEN_ALGO);
    }
}
