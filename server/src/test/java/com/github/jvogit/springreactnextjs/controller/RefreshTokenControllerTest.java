package com.github.jvogit.springreactnextjs.controller;

import com.github.jvogit.springreactnextjs.model.User;
import com.github.jvogit.springreactnextjs.model.response.RefreshTokenResponse;
import com.github.jvogit.springreactnextjs.service.RefreshTokenService;
import com.github.jvogit.springreactnextjs.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenControllerTest {

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserService userService;

    @InjectMocks
    private RefreshTokenController refreshTokenController;

    @Test
    void refreshToken_regenerated() {
        final User user = User.builder()
                .id(UUID.randomUUID())
                .username("testUsername")
                .password("testPassword")
                .email("testEmail")
                .build();
        final String testRefreshToken = "testRefreshToken";
        final String expectedAccessToken = "testAccessToken";
        final RefreshTokenResponse expectedResponse = RefreshTokenResponse.builder()
                .accessToken(expectedAccessToken)
                .build();

        when(userService.generateAccessToken(user)).thenReturn(expectedAccessToken);
        when(refreshTokenService.verify(testRefreshToken)).thenReturn(Optional.of(user));

        final RefreshTokenResponse actualRefreshTokenResponse = refreshTokenController
                .refreshToken(testRefreshToken);

        assertThat(actualRefreshTokenResponse, is(expectedResponse));
        verify(refreshTokenService, times(1)).setRefreshTokenCookie(any(), eq(null));
    }

    @Test
    void refreshToken_cleared() {
        final RefreshTokenResponse expectedResponse = RefreshTokenResponse.builder()
                .accessToken(null)
                .build();

        when(refreshTokenService.verify(any())).thenReturn(Optional.empty());

        final RefreshTokenResponse actualResponse = refreshTokenController.refreshToken("testRefreshToken");

        assertThat(actualResponse, is(expectedResponse));
        verify(refreshTokenService, times(1)).setRefreshTokenCookie(any(), eq(null));
    }
}
