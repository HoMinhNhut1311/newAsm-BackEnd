package com.hominhnhut.WMN_BackEnd.service.Interface;


import com.hominhnhut.WMN_BackEnd.domain.enity.User;
import com.hominhnhut.WMN_BackEnd.domain.request.AuthenticationRequest;
import com.hominhnhut.WMN_BackEnd.domain.request.IntrospectRequest;
import com.hominhnhut.WMN_BackEnd.domain.request.UserGoogleInfo;
import com.hominhnhut.WMN_BackEnd.domain.response.AuthenticationResponse;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthService {

    AuthenticationResponse Login(AuthenticationRequest request);

    AuthenticationResponse LoginOauth2(UserGoogleInfo userGoogleInfo);

    boolean Introspect(IntrospectRequest request) throws ParseException, JOSEException;

}