package com.github.jvogit.springreactnextjs.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
public class JwtController {

    @GetMapping("/refresh_token")
    public String refreshToken(final HttpServletResponse response) {
        final ResponseCookie cookie = ResponseCookie.from("jid", "test")
                .httpOnly(true)
                .path("/refresh_token")
                // allow cross site cookie sending
                // useful when backend is on heroku domain and frontend is in nextjs domain
                // normally they should be under one domain name and therefore
                // this would not be needed
                // needs third party cookies to be enabled in browsers
                .sameSite("none")
                .secure(true)
                .build();

        // because javax Cookie does not support same-site attribute
        // must use addHeader instead of addCookie
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return "Hello, World!";
    }
}
