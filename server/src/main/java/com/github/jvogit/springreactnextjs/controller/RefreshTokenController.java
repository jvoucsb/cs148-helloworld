package com.github.jvogit.springreactnextjs.controller;

import com.github.jvogit.springreactnextjs.model.User;
import com.github.jvogit.springreactnextjs.model.response.RefreshTokenResponse;
import com.github.jvogit.springreactnextjs.service.RefreshTokenService;
import com.github.jvogit.springreactnextjs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    @Autowired
    public RefreshTokenController(
            final RefreshTokenService refreshTokenService,
            final UserService userService
    ) {
        this.refreshTokenService = refreshTokenService;
        this.userService = userService;
    }

    @PostMapping("/refresh_token")
    public RefreshTokenResponse refreshToken(
            @CookieValue("jid") String refreshToken,
            final HttpServletResponse response
    ) {
        return refreshTokenService.verify(refreshToken)
                .map(user -> regenerateTokens(user, response))
                .orElseGet(() -> clearTokens(response));
    }

    private RefreshTokenResponse regenerateTokens(final User user, final HttpServletResponse response) {
        final String newRefreshToken = refreshTokenService.generateRefreshToken(user);
        final String accessToken = userService.generateAccessToken(user);

        return refreshTokenService.setTokensResponse(response, accessToken, newRefreshToken);
    }

    private RefreshTokenResponse clearTokens(final HttpServletResponse response) {
        return refreshTokenService.setTokensResponse(response, null, null);
    }
}
