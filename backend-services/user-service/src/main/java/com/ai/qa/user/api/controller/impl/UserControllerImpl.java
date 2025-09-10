package com.ai.qa.user.api.controller.impl;

import com.ai.qa.user.api.controller.UserController;
import com.ai.qa.user.api.dto.request.LoginRequest;
import com.ai.qa.user.api.dto.request.RegisterRequest;
import com.ai.qa.user.api.dto.request.UpdatePasswordRequest;
import com.ai.qa.user.api.dto.response.LoginResponse;
import com.ai.qa.user.api.dto.response.RegisterResponse;
import com.ai.qa.user.api.dto.response.UpdatePasswordResponse;
import com.ai.qa.user.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户控制器实现类
 * 处理用户相关的HTTP请求，包括登录、注册等接口
 * 作为RESTful API的入口点，负责请求转发和响应处理
 *
 * @author Rong Wen
 * @version 1.0
 */
@RestController
public class UserControllerImpl implements UserController {
    private static final Logger log = LoggerFactory.getLogger(UserControllerImpl.class);

    @Autowired
    private UserService userService;

    /**
     * 用户登录接口
     * 接收登录请求，调用服务层进行身份验证，返回登录结果
     *
     * @param loginRequest 登录请求参数，包含用户名和密码
     * @return ResponseEntity<LoginResponse> 包含登录结果的响应实体
     * @apiNote POST /api/users/login
     * @example
     *          {
     *          "username": "testuser",
     *          "password": "password123"
     *          }
     */
    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        log.info("Start login(), username:{}", loginRequest.getUsername());

        // 调用用户服务处理登录逻辑
        LoginResponse response = userService.login(loginRequest);

        log.info("End login(), username:{}", loginRequest.getUsername());

        // 根据登录成功与否返回相应的HTTP状态码
        // 成功返回200 OK，失败时业务异常已在服务层抛出，不会执行到此处
        return ResponseEntity.ok(response);
    }

    /**
     * 用户注册接口
     * 接收注册请求，调用服务层创建新用户，返回注册结果
     *
     * @param registerRequest 注册请求参数，包含用户名、昵称、密码等信息
     * @return ResponseEntity<RegisterResponse> 包含注册结果的响应实体
     * @apiNote POST /api/users/register
     * @example
     *          {
     *          "username": "newuser",
     *          "nickname": "新用户",
     *          "password": "password123",
     *          "confirmPassword": "password123"
     *          }
     */
    @Override
    public ResponseEntity<RegisterResponse> register(RegisterRequest registerRequest) {
        log.info("Start register(), username:{}", registerRequest.getUsername());

        // 调用用户服务处理注册逻辑
        RegisterResponse response = userService.register(registerRequest);

        log.info("Start register(), username:{}", registerRequest.getUsername());

        // 注册成功返回201 Created状态码
        // 失败时业务异常已在服务层抛出，不会执行到此处
        return ResponseEntity.status(201).body(response);
    }

    /**
     * 修改用户密码接口
     * 接收密码修改请求，调用服务层进行密码更新，返回操作结果
     *
     * @param updatePasswordRequest 修改密码请求参数
     * @return ResponseEntity<UpdatePasswordResponse> 包含修改结果的响应实体
     * @apiNote POST /api/users/updatePassword
     * @example
     *          {
     *          "userId": 12345,
     *          "oldPassword": "oldPassword123",
     *          "newPassword": "newPassword456",
     *          "confirmNewPassword": "newPassword456"
     *          }
     */
    @Override
    public ResponseEntity<UpdatePasswordResponse> updatePassword(UpdatePasswordRequest updatePasswordRequest) {
        log.info("Start updatePassword(), username:{}", updatePasswordRequest.getUsername());

        // 调用用户服务处理密码修改逻辑
        UpdatePasswordResponse response = userService.updatePassword(updatePasswordRequest);

        log.info("End updatePassword(), username:{}", updatePasswordRequest.getUsername());

        // 密码修改成功返回200 OK状态码
        return ResponseEntity.ok(response);
    }
}