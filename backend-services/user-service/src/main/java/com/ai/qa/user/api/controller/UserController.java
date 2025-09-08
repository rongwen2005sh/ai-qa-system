package com.ai.qa.user.api.controller;

import com.ai.qa.user.api.dto.request.LoginRequest;
import com.ai.qa.user.api.dto.request.RegisterRequest;
import com.ai.qa.user.api.dto.response.LoginResponse;
import com.ai.qa.user.api.dto.response.RegisterResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 用户控制器接口
 * 定义用户相关的RESTful API接口，包括登录、注册等功能
 * 使用接口与实现分离的设计模式，便于维护和扩展
 *
 * @author Rong Wen
 * @version 1.0
 */
@Tag(name = "用户管理", description = "用户登录、注册等身份认证相关接口")
@RequestMapping("/api/users")
public interface UserController {

    /**
     * 用户登录接口
     * 验证用户身份信息，返回访问令牌和用户信息
     *
     * @param loginRequest 登录请求参数
     * @return ResponseEntity<LoginResponse> 登录响应结果
     * @see LoginRequest
     * @see LoginResponse
     */
    @Operation(summary = "用户登录", description = "通过用户名和密码进行身份验证")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "登录成功"),
            @ApiResponse(responseCode = "400", description = "用户名或密码错误"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(
            @Parameter(description = "登录请求参数", required = true)
            @RequestBody LoginRequest loginRequest
    );

    /**
     * 用户注册接口
     * 创建新的用户账户，验证用户名唯一性
     *
     * @param registerRequest 注册请求参数
     * @return ResponseEntity<RegisterResponse> 注册响应结果
     * @see RegisterRequest
     * @see RegisterResponse
     */
    @Operation(summary = "用户注册", description = "创建新的用户账户")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "注册成功"),
            @ApiResponse(responseCode = "400", description = "用户名已存在或密码不匹配"),
            @ApiResponse(responseCode = "409", description = "用户已存在")
    })
    @PostMapping("/register")
    ResponseEntity<RegisterResponse> register(
            @Parameter(description = "注册请求参数", required = true)
            @RequestBody RegisterRequest registerRequest
    );
}