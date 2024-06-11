package com.hominhnhut.WMN_BackEnd.restController;
import com.hominhnhut.WMN_BackEnd.domain.enity.User;
import com.hominhnhut.WMN_BackEnd.domain.response.UserDtoResponse;
import com.hominhnhut.WMN_BackEnd.mapper.impl.UserMapper;
import com.hominhnhut.WMN_BackEnd.service.impl.AuthServiceImpl;
import com.hominhnhut.WMN_BackEnd.service.Interface.AuthService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:5173/")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RestController
public class AuthController {

    AuthService authService;
    UserMapper userMapper;

    public AuthController(AuthServiceImpl authService, UserMapper userMapper) {
        this.authService = authService;
        this.userMapper = userMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDtoResponse> Login(
            @RequestBody User userLogin
    ) {
        UserDtoResponse userResponse = userMapper.
                mapToResponese(this.authService.Login(userLogin.getUsername(),userLogin.getPassword()));
        return ResponseEntity.ok(userResponse);
    }
}