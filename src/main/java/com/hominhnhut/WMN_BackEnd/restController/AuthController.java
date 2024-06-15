package com.hominhnhut.WMN_BackEnd.restController;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.hominhnhut.WMN_BackEnd.domain.request.AuthenticationRequest;
import com.hominhnhut.WMN_BackEnd.domain.request.IntrospectRequest;
import com.hominhnhut.WMN_BackEnd.domain.request.UserGoogleInfo;
import com.hominhnhut.WMN_BackEnd.domain.response.AuthenticationResponse;
import com.hominhnhut.WMN_BackEnd.service.Interface.AuthService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;

@CrossOrigin(origins = "http://localhost:5173/")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@RestController
public class AuthController {

    AuthService authService;
    @NonFinal
    @Value("${spring.security.oauth2.resourceserver.opaquetoken.client-id}")
    String clientId;

    @NonFinal
    @Value("${spring.security.oauth2.resourceserver.opaquetoken.client-secret}")
    String clientSecret;

    WebClient webClient;

    @GetMapping("/auth/url")
    public ResponseEntity<String> getGoogleAuthorizationUrl() {
        String url =  new GoogleAuthorizationCodeRequestUrl(
                clientId,
                "http://localhost:5173/login",
                Arrays.asList("email","profile","openid")
        ).build();
        return ResponseEntity.ok(url);
    }

        @GetMapping("/auth/callback")
    public ResponseEntity<AuthenticationResponse> callback(
            @RequestParam("code") String code
    ) throws IOException {
        String token  = new GoogleAuthorizationCodeTokenRequest(
                new NetHttpTransport(),
                new GsonFactory(),
                clientId,
                clientSecret,
                code,
                "http://localhost:5173/login"
        ).execute().getAccessToken();

            UserGoogleInfo userInfor = webClient.get().
              uri(uriBuilder -> uriBuilder.path("/oauth2/v3/userinfo").
                        queryParam("access_token",token).build()).retrieve()
                .bodyToMono(UserGoogleInfo.class).block();

        AuthenticationResponse response = authService.LoginOauth2(userInfor);

        return ResponseEntity.ok(response);
    }



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

    @PostMapping("/google")
    public ResponseEntity<?> LoginWithGoogle(
            @RequestBody IntrospectRequest request
    ) {
        System.out.println(request.getToken());
        return ResponseEntity.ok(request.getToken());
    }
}