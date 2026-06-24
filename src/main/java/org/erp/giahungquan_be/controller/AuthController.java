package org.erp.giahungquan_be.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.erp.giahungquan_be.request.ChangePasswordRequest;
import org.erp.giahungquan_be.request.LoginRequest;
import org.erp.giahungquan_be.request.RefreshTokenRequest;
import org.erp.giahungquan_be.request.RegisterRequest;
import org.erp.giahungquan_be.response.AuthResponse;
import org.erp.giahungquan_be.security.UserPrincipal;
import org.erp.giahungquan_be.service.AuthService;
import org.erp.giahungquan_be.common.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    AuthService authService;

    @PostMapping("/auth/login")
    public ApiResponse<AuthResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(request);
        authService.setRefreshTokenCookie(response, authResponse.getRefreshToken());
        authResponse.setRefreshToken(null);
        return ApiResponse.success("Login successful", authResponse);
    }

    @PostMapping("/auth/register-account")
    public ApiResponse<?> register(
            @Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(authService.register(request));
    }

    @PostMapping("/auth/refresh-token")
    public ApiResponse<AuthResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = Arrays.stream(request.getCookies() != null ? request.getCookies() : new Cookie[0])
                .filter(cookie -> "refresh_token".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Refresh token missing"));

        AuthResponse authResponse = authService.refreshToken(new RefreshTokenRequest(refreshToken));
        authService.setRefreshTokenCookie(response, authResponse.getRefreshToken());
        authResponse.setRefreshToken(null);
        return ApiResponse.success("Token refreshed successfully", authResponse);
    }

    @PostMapping("/account/logout")
    public ApiResponse<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String authHeader = request.getHeader("Authorization");
        authService.logout(authHeader);
        authService.clearRefreshTokenCookie(response);
        return ApiResponse.success("Logged out successfully", null);
    }

    @PostMapping("/account/change-password")
    public ApiResponse<?> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UserPrincipal user) {
        return ApiResponse.success(authService.changePassword(request, user));
    }
}
