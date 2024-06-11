package com.hominhnhut.WMN_BackEnd.service.Interface;


import com.hominhnhut.WMN_BackEnd.domain.enity.User;

public interface AuthService {

    User Login(String username, String password);
}