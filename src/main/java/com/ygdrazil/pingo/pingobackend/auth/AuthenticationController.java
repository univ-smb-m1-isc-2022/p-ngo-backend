package com.ygdrazil.pingo.pingobackend.auth;

import com.ygdrazil.pingo.pingobackend.config.JwtService;
import com.ygdrazil.pingo.pingobackend.models.User;
import com.ygdrazil.pingo.pingobackend.requestObjects.AuthenticationRequest;
import com.ygdrazil.pingo.pingobackend.requestObjects.RegisterRequest;
import com.ygdrazil.pingo.pingobackend.responseObjects.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final String SESSION_COOKIE_NAME = "JSESSIONID";

    private final AuthenticationService service;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request
    ) {
        if(service.existsByUsername(request.getUsername())) {
            return ResponseEntity
                    .status(409)
                    .body("Error, Username already registered");
        }

        if(service.existsByEmail(request.getEmail())) {
            return ResponseEntity
                    .status(409)
                    .body("Error, Email already registered");
        }

        User user = service.register(request);

        var jwtToken = jwtService.generateToken(user);

        ResponseCookie cookie = ResponseCookie.from(SESSION_COOKIE_NAME, jwtToken)
                .httpOnly(true)
                .sameSite("Lax")
                .maxAge(Duration.ofDays(1))
                .path("/")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity
                .status(200)
                .headers(headers)
                .body("User successfully registered");
    }

    @PostMapping("/authentication")
    public ResponseEntity<?> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        Optional<User> user = service.authenticate(request);

        if(user.isEmpty()) {
            return ResponseEntity
                    .status(400)
                    .body("Error, User/Password not correct");
        }

        var jwtToken = jwtService.generateToken(user.get());

        ResponseCookie cookie = ResponseCookie.from(SESSION_COOKIE_NAME, jwtToken)
                .httpOnly(true)
                .sameSite("Lax")
                .maxAge(Duration.ofHours(24))
//                .maxAge(Duration.ofSeconds(15))
                .path("/")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity
                .status(200)
                .headers(headers)
                .body("User successfully authenticated");
    }

    @GetMapping("/user")
    public ResponseEntity<?> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() == "anonymousUser") {
            return ResponseEntity
                    .status(404)
                    .body("Error, No User authenticated");
        }

        User user = (User) authentication.getPrincipal();

        return ResponseEntity
                .status(200)
                .body(UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .build());
    }

    @PostMapping("/disconnect")
    public ResponseEntity<?> disconnect() {
        ResponseCookie cookie = ResponseCookie.from(SESSION_COOKIE_NAME, "")
                .httpOnly(true)
                .sameSite("Lax")
                .maxAge(0)
                .path("/")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity
                .status(200)
                .headers(headers)
                .body("User successfully disconnected");

    }
}
