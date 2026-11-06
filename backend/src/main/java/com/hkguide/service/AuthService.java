package com.hkguide.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hkguide.dto.AuthResponse;
import com.hkguide.dto.LoginRequest;
import com.hkguide.dto.RegisterRequest;
import com.hkguide.entity.User;
import com.hkguide.exception.BizException;
import com.hkguide.mapper.UserMapper;
import com.hkguide.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.username()));
        if (count != null && count > 0) {
            throw new BizException("用户名已存在");
        }
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setNickname(request.nickname() == null || request.nickname().isBlank()
                ? request.username() : request.nickname());
        user.setCreatedAt(LocalDateTime.now());
        userMapper.insert(user);
        return new AuthResponse(jwtService.generateToken(user.getUsername()), user.getUsername(), user.getNickname());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.username()));
        if (user == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BizException("用户名或密码错误");
        }
        return new AuthResponse(jwtService.generateToken(user.getUsername()), user.getUsername(), user.getNickname());
    }
}
