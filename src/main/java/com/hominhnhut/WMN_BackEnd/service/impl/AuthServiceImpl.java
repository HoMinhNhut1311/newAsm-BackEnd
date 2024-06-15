package com.hominhnhut.WMN_BackEnd.service.impl;
import com.hominhnhut.WMN_BackEnd.domain.enity.Role;
import com.hominhnhut.WMN_BackEnd.domain.enity.User;
import com.hominhnhut.WMN_BackEnd.domain.enity.UserProfile;
import com.hominhnhut.WMN_BackEnd.domain.request.AuthenticationRequest;
import com.hominhnhut.WMN_BackEnd.domain.request.IntrospectRequest;
import com.hominhnhut.WMN_BackEnd.domain.request.UserGoogleInfo;
import com.hominhnhut.WMN_BackEnd.domain.response.AuthenticationResponse;
import com.hominhnhut.WMN_BackEnd.domain.response.UserDtoResponse;
import com.hominhnhut.WMN_BackEnd.exception.errorType;
import com.hominhnhut.WMN_BackEnd.exception.myException.AppException;
import com.hominhnhut.WMN_BackEnd.mapper.impl.UserMapper;
import com.hominhnhut.WMN_BackEnd.repository.RoleRepository;
import com.hominhnhut.WMN_BackEnd.repository.UserRepository;
import com.hominhnhut.WMN_BackEnd.service.Interface.AuthService;
import com.hominhnhut.WMN_BackEnd.utils.jwtUtils;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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



    public AuthenticationResponse Login(AuthenticationRequest request) {
        User user = userRepository.findUSerByUsername(request.getUsername()).orElseThrow(
                () -> new AppException(errorType.userNameNotExist)
        );
        UserDtoResponse userDtoResponse = userMapper.mapToResponese(user);
        boolean isMatches = passwordEncoder.matches(request.getPassword(),user.getPassword());
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
    public AuthenticationResponse LoginOauth2(UserGoogleInfo userGoogleInfo) {
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
            Role role = roleRepository.getRoleByRoleName("USER");
            Set<Role> roles = new HashSet<>();
            roles.add(role);
            User userOauth2 = User.builder()
                    .username(userGoogleInfo.email())
                    .password(passwordEncoder.encode("1"))
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
                    .build();
        }

    }

    public boolean Introspect(IntrospectRequest request) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(request.getToken());
        return jwtUtils.VerifyToken_isMatching(signedJWT);
    }


}