package com.ai.qa.user.application.service.impl;

import com.ai.qa.user.api.dto.request.LoginRequest;
import com.ai.qa.user.api.dto.request.RegisterRequest;
import com.ai.qa.user.api.dto.response.LoginResponse;
import com.ai.qa.user.api.dto.response.RegisterResponse;
import com.ai.qa.user.api.exception.BusinessException;
import com.ai.qa.user.api.exception.ErrCode;
import com.ai.qa.user.application.service.UserService;
import com.ai.qa.user.common.JwtUtil;
import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 用户服务实现类
 * 处理用户相关的业务逻辑，包括登录、注册等功能
 *
 * @author Rong Wen
 * @version 1.0
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户登录处理
     * 验证用户名和密码，生成访问令牌
     *
     * @param loginRequest 登录请求参数，包含用户名和密码
     * @return LoginResponse 登录响应结果，包含令牌和用户信息
     * @throws BusinessException 当用户不存在或密码错误时抛出业务异常
     */
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        // 根据用户名查询用户信息
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());

        // 验证用户是否存在
        if (!userOptional.isPresent()) {
            throw BusinessException.userNotFound();
        }

        User user = userOptional.get();

        // 验证密码是否正确
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw BusinessException.passwordIncorrect();
        }

        // 更新用户最后登录时间
        user.setUpdateDate(LocalDateTime.now());
        userRepository.save(user);

        // 生成访问令牌（模拟实现，实际应使用JWT）
        String token = generateToken(user);

        // 构建登录成功响应
        LoginResponse response = new LoginResponse();
        response.setSuccess(true);
        response.setMessage(ErrCode.MSG_SUCCESS);
        response.setErrorCode(ErrCode.SUCCESS);
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setLoginTime(LocalDateTime.now());

        return response;
    }

    /**
     * 用户注册处理
     * 创建新用户账户，验证用户名唯一性和密码一致性
     *
     * @param registerRequest 注册请求参数，包含用户名、昵称、密码等信息
     * @return RegisterResponse 注册响应结果，包含新创建的用户信息
     * @throws BusinessException 当用户名已存在或密码不匹配时抛出业务异常
     */
    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest registerRequest) {
        // 检查用户名是否已被注册
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw BusinessException.userAlreadyExists();
        }

        // 验证两次输入的密码是否一致
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw BusinessException.passwordMismatch();
        }

        // 创建新用户对象并设置属性
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setNickname(registerRequest.getNickname());
        // 对密码进行加密存储
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreateDate(LocalDateTime.now());
        user.setUpdateDate(LocalDateTime.now());

        // 保存用户到数据库
        User savedUser = userRepository.save(user);

        // 构建注册成功响应
        RegisterResponse response = new RegisterResponse();
        response.setSuccess(true);
        response.setMessage(ErrCode.MSG_CREATED);
        response.setErrorCode(ErrCode.CREATED);
        response.setUserId(savedUser.getId());
        response.setUsername(savedUser.getUsername());
        response.setNickname(savedUser.getNickname());
        response.setRegisterTime(LocalDateTime.now());

        return response;
    }

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return User 用户实体对象，如果不存在则返回null
     */
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    /**
     * 检查用户名是否已存在
     *
     * @param username 需要检查的用户名
     * @return Boolean true表示用户名已存在，false表示不存在
     */
    @Override
    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * 生成用户访问令牌（模拟实现）
     * 实际项目中应使用JWT等标准token生成机制
     *
     * @param user 用户实体对象
     * @return String 生成的访问令牌
     */
    private String generateToken(User user) {
        // 调用JwtUtil生成真实token
        return jwtUtil.generateToken(user.getUsername());
    }

    /**
     * 验证用户密码是否正确
     *
     * @param rawPassword     原始密码（用户输入）
     * @param encodedPassword 加密后的密码（数据库存储）
     * @return Boolean true表示密码正确，false表示密码错误
     */
    private Boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 加密用户密码
     *
     * @param rawPassword 原始密码
     * @return String 加密后的密码
     */
    private String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }


}