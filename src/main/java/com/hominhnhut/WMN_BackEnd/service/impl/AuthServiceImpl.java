package com.hominhnhut.WMN_BackEnd.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.hominhnhut.WMN_BackEnd.domain.enity.Role;
import com.hominhnhut.WMN_BackEnd.domain.enity.User;
import com.hominhnhut.WMN_BackEnd.domain.enity.UserProfile;
import com.hominhnhut.WMN_BackEnd.domain.request.AuthenticationRequest;
import com.hominhnhut.WMN_BackEnd.domain.request.IntrospectRequest;
import com.hominhnhut.WMN_BackEnd.domain.request.RegisterRequest;
import com.hominhnhut.WMN_BackEnd.domain.request.UserGoogleInfo;
import com.hominhnhut.WMN_BackEnd.domain.response.AuthenticationResponse;
import com.hominhnhut.WMN_BackEnd.domain.response.UserDtoResponse;
import com.hominhnhut.WMN_BackEnd.exception.errorType;
import com.hominhnhut.WMN_BackEnd.exception.myException.AppException;
import com.hominhnhut.WMN_BackEnd.mapper.impl.UserMapper;
import com.hominhnhut.WMN_BackEnd.repository.RoleRepository;
import com.hominhnhut.WMN_BackEnd.repository.UserProfileRepository;
import com.hominhnhut.WMN_BackEnd.repository.UserRepository;
import com.hominhnhut.WMN_BackEnd.service.Interface.AuthService;
import com.hominhnhut.WMN_BackEnd.service.Interface.MyGmailService;
import com.hominhnhut.WMN_BackEnd.utils.RanDomUtils;
import com.hominhnhut.WMN_BackEnd.utils.jwtUtils;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ExecutorService;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    RoleRepository roleRepository;

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    jwtUtils jwtUtils;
    WebClient webClient;
    MyGmailService myGmailService;
    UserProfileRepository userProfileRepository;
    ExecutorService executorService;
    RanDomUtils ranDomUtils;

    @NonFinal
    @Value("${security.oauth2.resourceserver.opaquetoken.client-id}")
    String clientId;

    @NonFinal
    @Value("${security.oauth2.resourceserver.opaquetoken.client-secret}")
    String clientSecret;

    public AuthenticationResponse Login(AuthenticationRequest request) {
        User user = userRepository.findUSerByUsername(request.getUsername()).orElseThrow(
                () -> new AppException(errorType.userNameNotExist));
        UserDtoResponse userDtoResponse = userMapper.mapToResponese(user);
        boolean isMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!isMatches) {
            throw new AppException(errorType.PasswordIsNotCorrect);
        }
        String token = jwtUtils.generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .fullName(userDtoResponse.getFullName())
                .roleNames(userDtoResponse.getRoleNames())
                .build();
    }

    @Override
    public String getUserToUrlOauth2() {
        String url = new GoogleAuthorizationCodeRequestUrl(
                clientId,
                "http://localhost:5173/login",
                Arrays.asList("email", "profile", "openid")).build();
        return url;
    }

    @Override
    public AuthenticationResponse LoginOauth2(String code) throws IOException {

        String accessToken = new GoogleAuthorizationCodeTokenRequest(
                new NetHttpTransport(),
                new GsonFactory(),
                clientId,
                clientSecret,
                code,
                "http://localhost:5173/login").execute().getAccessToken();

        try {
            UserGoogleInfo userGoogleInfo = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/oauth2/v3/userinfo").queryParam("access_token", accessToken)
                            .build())
                    .retrieve()
                    .bodyToMono(UserGoogleInfo.class).block();
            assert userGoogleInfo != null;
            return getUserOauth2(userGoogleInfo);
        } catch (Exception e) {
            log.info("Invalid Graint");
            return null;
        }
    }

    private AuthenticationResponse getUserOauth2(UserGoogleInfo userGoogleInfo) {
        // Sau khi lấy dđược Thông tin GoogleInfor
        Optional<User> user = userRepository.findUSerByUsername(userGoogleInfo.email());
        if (user.isPresent()) {
            UserDtoResponse response = userMapper.mapToResponese(user.get());
            String token = jwtUtils.generateToken(user.get());
            return AuthenticationResponse.builder()
                    .token(token)
                    .fullName(response.getFullName())
                    .roleNames(response.getRoleNames())
                    .build();
        }
        // Nếu UserOauth2 Chưa tồn tại -> tạo User với username = Email, password = 1
        else {
            String randomPassword = ranDomUtils.generateRandomNumber(5);
            executorService.execute(() -> {
                myGmailService.sendEmail(userGoogleInfo.email(),
                        "Username : " + userGoogleInfo.email() +
                                "\nMật khẩu mặc định của bạn :" + randomPassword,
                        "NH-Application - Chào mừng bạn gia nhập");
            });
            Role role = roleRepository.getRoleByRoleName("USER");
            Set<Role> roles = new HashSet<>();
            roles.add(role);
            User userOauth2 = User.builder()
                    .username(userGoogleInfo.email())
                    .password(passwordEncoder.encode(randomPassword))
                    .roles(roles)
                    .build();
            UserProfile userProfile = new UserProfile();

            userProfile.setProfileFullName(userGoogleInfo.name());
            userOauth2.setUserProfile(userProfile);
            userProfile.setUser(userOauth2);

            UserDtoResponse response = userMapper.mapToResponese(userOauth2);
            String token = jwtUtils.generateToken(userRepository.save(userOauth2));
            return AuthenticationResponse.builder()
                    .token(token)
                    .fullName(userGoogleInfo.name())
                    .roleNames(response.getRoleNames())
                    .firstOauth2(true)
                    .build();
        }

    }

    public boolean Introspect(IntrospectRequest request) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(request.getToken());
        return jwtUtils.VerifyToken_isMatching(signedJWT);
    }

    public UserDtoResponse register(RegisterRequest registerRequest) {
        System.out.println("Register request received: " + registerRequest);

        boolean usernameExists = userRepository.existsByUsername(registerRequest.getUsername());
        boolean emailExists = userProfileRepository.existsByProfileEmail(registerRequest.getEmail());

        System.out.println("Username exists: " + usernameExists);
        System.out.println("Email exists: " + emailExists);

        if (usernameExists || emailExists) {
            System.out.println("Returning null due to existing username or email");
            return null;
        } else {
            User user = new User();
            String password = passwordEncoder.encode("acdb");
            user.setPassword(password);
            user.setUsername(registerRequest.getUsername());

            UserProfile userProfile = new UserProfile();
            userProfile.setProfileEmail(registerRequest.getEmail());
            userProfile.setUser(user);
            user.setUserProfile(userProfile);

            List<Role> roleSet = registerRequest.getRoleNames().stream()
                    .map(this.roleRepository::getRoleByRoleName)
                    .toList();
            user.setRoles(new HashSet<>(roleSet));
            System.out.println("Saving user: " + user);

            User savedUser = userRepository.save(user);

            System.out.println("Saved user: " + savedUser);

            return this.userMapper.mapToResponese(savedUser);
        }
    }

}