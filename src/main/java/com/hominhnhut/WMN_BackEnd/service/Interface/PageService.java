package com.hominhnhut.WMN_BackEnd.service.Interface;


import com.hominhnhut.WMN_BackEnd.domain.response.UserDtoResponse;
import org.springframework.data.domain.Page;

public interface PageService {

    Page<UserDtoResponse> getPageUserByRoleId(Integer pageNumber, Integer pageSize, Integer roleId);

}

