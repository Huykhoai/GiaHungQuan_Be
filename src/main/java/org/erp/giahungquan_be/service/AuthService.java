package org.erp.giahungquan_be.service;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.erp.giahungquan_be.entity.Account;
import org.erp.giahungquan_be.repository.AccountRepository;
import org.erp.giahungquan_be.request.ChangePasswordRequest;
import org.erp.giahungquan_be.request.LoginRequest;
import org.erp.giahungquan_be.request.RefreshTokenRequest;
import org.erp.giahungquan_be.request.RegisterRequest;
import org.erp.giahungquan_be.response.AuthResponse;
import org.erp.giahungquan_be.response.Message;
import org.erp.giahungquan_be.security.JwtTokenProvider;
import org.erp.giahungquan_be.security.UserPrincipal;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {
    AuthenticationManager authenticationManager;
    AccountRepository accountRepository;
    PasswordEncoder passwordEncoder;
    UserDetailsService userDetailsService;
    JwtTokenProvider jwtTokenProvider;
    StringRedisTemplate redisTemplate;


    public AuthResponse login(LoginRequest request){
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()
            ));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Mật khẩu hoặc tài khoản không chính xác");
        }

        UserPrincipal userPrincipal = (UserPrincipal) userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtTokenProvider.generateToken(userPrincipal);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userPrincipal);

        return AuthResponse.builder()
                .token(token)
                .username(userPrincipal.getUsername())
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String username = jwtTokenProvider.extractUsername(request.getRefreshToken());
        UserPrincipal principal = (UserPrincipal) userDetailsService.loadUserByUsername(username);

        if (Boolean.TRUE.equals(jwtTokenProvider.validateToken(request.getRefreshToken(), principal))) {
            String newToken = jwtTokenProvider.generateToken(principal);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(principal);

            return AuthResponse.builder()
                    .token(newToken)
                    .refreshToken(newRefreshToken)
                    .username(principal.getUsername())
                    .build();
        }
        throw new RuntimeException("Invalid Refresh Token");
    }

    public Message register(RegisterRequest request) {
        org.erp.giahungquan_be.util.DataTrimUtils.trimStringFields(request);

        if (accountRepository.existsAccountByUsername(request.getUsername())) {
            return new Message(HttpStatus.BAD_REQUEST.value(), "Username này đã tồn tại");
        }

        Account account = Account.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .createdAt(LocalDateTime.now())
                .build();

        accountRepository.save(account);

        return new Message(HttpStatus.CREATED.value(), "Tạo tài khoản thành công");
    }

    public void logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            long expiration = jwtTokenProvider.getRemainingExpiration(jwt);
            if (expiration > 0) {
                redisTemplate.opsForValue().set("blacklist_token:" + jwt, "true", expiration,
                        TimeUnit.MILLISECONDS);
            }
        }
    }

    public void clearRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public Message changePassword(ChangePasswordRequest request, UserPrincipal user) {
        org.erp.giahungquan_be.util.DataTrimUtils.trimStringFields(request);

        Account account = accountRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản id: " + user.getId()));

        if (!passwordEncoder.matches(request.getOldPassword(), account.getPassword())) {
            return new Message(HttpStatus.BAD_REQUEST.value(), "Mật khẩu cũ không chính xác");
        }

        if (passwordEncoder.matches(request.getNewPassword(), account.getPassword())) {
            return new Message(HttpStatus.BAD_REQUEST.value(),
                    "Mật khẩu mới không được trùng với mật khẩu cũ");
        }

        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.save(account);

        return new Message(HttpStatus.CREATED.value(), "Thay đổi tài khoản thành công");
    }

    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(jwtTokenProvider.getRefreshExpiration() / 1000)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
