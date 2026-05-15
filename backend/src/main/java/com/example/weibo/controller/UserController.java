package com.example.weibo.controller;

import com.example.weibo.common.Result;
import com.example.weibo.dto.LoginRequest;
import com.example.weibo.dto.RegisterRequest;
import com.example.weibo.dto.response.UserInfoResponse;
import com.example.weibo.entity.User;
import com.example.weibo.exception.BusinessException;
import com.example.weibo.service.UserService;
import com.example.weibo.util.FileUploadUtil;
import com.example.weibo.util.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    @PostMapping("/login")
    public Result<UserInfoResponse> login(@RequestBody LoginRequest request, HttpSession session) {
        User user = userService.login(request.getUsername(), request.getPassword());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        SessionUtil.setUserId(session, user.getId());
        return Result.success("登录成功", convertToUserInfoResponse(user));
    }

    @PostMapping("/register")
    public Result<UserInfoResponse> register(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar,
            HttpSession session) {
        
        if (!password.equals(confirmPassword)) {
            throw new BusinessException("两次输入的密码不一致");
        }
        
        if (userService.findByUsername(username) != null) {
            throw new BusinessException("用户名已存在");
        }
        
        User user = userService.register(username, password);
        
        if (avatar != null && !avatar.isEmpty()) {
            try {
                String avatarPath = fileUploadUtil.uploadAvatar(avatar);
                user.setAvatar(avatarPath);
                userService.update(user);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("头像上传失败: " + e.getMessage());
            }
        }
        
        SessionUtil.setUserId(session, user.getId());
        
        return Result.success("注册成功", convertToUserInfoResponse(user));
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpSession session) {
        session.invalidate();
        return Result.success("退出成功");
    }

    @GetMapping("/info")
    public Result<UserInfoResponse> getCurrentUser(HttpSession session) {
        Integer userId = SessionUtil.getUserId(session);
        User user = userService.findById(userId);
        if (user == null) {
            throw new BusinessException(401, "请先登录");
        }
        return Result.success(convertToUserInfoResponse(user));
    }

    @GetMapping("/profile/{userId}")
    public Result<UserInfoResponse> getUserProfile(@PathVariable Integer userId) {
        User user = userService.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return Result.success(convertToUserInfoResponse(user));
    }

    private UserInfoResponse convertToUserInfoResponse(User user) {
        return new UserInfoResponse(
                user.getId(),
                user.getUsername(),
                user.getAvatar(),
                user.getCreateTime()
        );
    }
}