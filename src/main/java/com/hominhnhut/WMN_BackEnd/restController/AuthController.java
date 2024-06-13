package com.hominhnhut.WMN_BackEnd.restController;
import com.hominhnhut.WMN_BackEnd.domain.enity.User;
import com.hominhnhut.WMN_BackEnd.domain.request.AuthenticationRequest;
import com.hominhnhut.WMN_BackEnd.domain.request.IntrospectRequest;
import com.hominhnhut.WMN_BackEnd.domain.response.AuthenticationResponse;
import com.hominhnhut.WMN_BackEnd.domain.response.UserDtoResponse;
import com.hominhnhut.WMN_BackEnd.mapper.impl.UserMapper;
import com.hominhnhut.WMN_BackEnd.service.impl.AuthServiceImpl;
import com.hominhnhut.WMN_BackEnd.service.Interface.AuthService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@CrossOrigin(origins = "http://localhost:5173/")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@RestController
public class AuthController {

    AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> Login(
            @RequestBody AuthenticationRequest request
    ) {
        AuthenticationResponse response = authService.Login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/introspect")
    public ResponseEntity<Boolean> Introspect(
           @RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        boolean isValid = authService.Introspect(request);
        return ResponseEntity.ok(isValid);
    }
}