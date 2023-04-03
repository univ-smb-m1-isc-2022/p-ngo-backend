package com.ygdrazil.pingo.pingobackend.auth;

import com.ygdrazil.pingo.pingobackend.config.JwtService;
import com.ygdrazil.pingo.pingobackend.models.User;
import com.ygdrazil.pingo.pingobackend.requestObjects.AuthenticationRequest;
import com.ygdrazil.pingo.pingobackend.requestObjects.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

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

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, "JSESSIONID=" + jwtToken + "; Path=/; HttpOnly");
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

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, "JSESSIONID=" + jwtToken + "; Path=/; HttpOnly");
        return ResponseEntity
                .status(200)
                .headers(headers)
                .body("User successfully authenticated");
    }

//    @Get("/user")
//    public ResponseEntity<>
}
