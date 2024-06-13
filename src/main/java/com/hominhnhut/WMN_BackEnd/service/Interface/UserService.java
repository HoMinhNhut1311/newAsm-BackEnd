package com.hominhnhut.WMN_BackEnd.service.Interface;

import com.hominhnhut.WMN_BackEnd.domain.request.UserDtoRequest;
import com.hominhnhut.WMN_BackEnd.domain.response.UserDtoResponse;


import java.util.List;
import java.util.Set;

public interface UserService {

    List<UserDtoResponse> getAll();

    UserDtoResponse findById(String id);

    UserDtoResponse save(UserDtoRequest request);

    UserDtoResponse update(String idUpdate, UserDtoRequest userUpdate);

    void delete(String idDelete);

    Set<UserDtoResponse> getUserByRoleId(Integer roleId);

    Set<UserDtoResponse> getUserByUsernameContaining(String usernameContain);

    UserDtoResponse getUserByUsername(String username);
}
