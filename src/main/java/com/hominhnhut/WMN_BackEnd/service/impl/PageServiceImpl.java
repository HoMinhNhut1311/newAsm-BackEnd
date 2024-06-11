package com.hominhnhut.WMN_BackEnd.service.impl;

import com.hominhnhut.WMN_BackEnd.domain.enity.Role;
import com.hominhnhut.WMN_BackEnd.domain.enity.User;
import com.hominhnhut.WMN_BackEnd.domain.request.UserDtoRequest;
import com.hominhnhut.WMN_BackEnd.domain.response.UserDtoResponse;
import com.hominhnhut.WMN_BackEnd.exception.errorType;
import com.hominhnhut.WMN_BackEnd.exception.myException.AppException;
import com.hominhnhut.WMN_BackEnd.mapper.MyMapperInterFace;
import com.hominhnhut.WMN_BackEnd.repository.RoleRepository;
import com.hominhnhut.WMN_BackEnd.repository.UserRepository;
import com.hominhnhut.WMN_BackEnd.service.Interface.PageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class PageServiceImpl implements PageService {

    UserRepository userRepository;
    RoleRepository roleRepository;


    MyMapperInterFace<UserDtoRequest, User, UserDtoResponse> userMapper;


    // Lấy ra User theo Role
    public Page<UserDtoResponse> getPageUserByRoleId(Integer pageNumber, Integer pageSize, Integer roleId) {
        PageRequest request = PageRequest.of(pageNumber, pageSize);
        if (roleId == 0) {
            // Lấy hết
            return userRepository.findAll(request).map(userMapper::mapToResponese);
        } else {
            // Lấy Role từ Role Id
            Role role = roleRepository.findById(roleId).orElseThrow(() -> new AppException(errorType.notFound));
            return userRepository.getUsersByRoleId(roleId, request).map(userMapper::mapToResponese);
        }
    }


}
