package com.hominhnhut.WMN_BackEnd.domain.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthenticationResponse {
    String token;
    String fullName;
    Set<String> roleNames;
}
