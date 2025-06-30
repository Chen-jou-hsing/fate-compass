package com.fatecompass.service;

import com.fatecompass.entity.User;
import com.fatecompass.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 用戶服務類
 */
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * 用戶註冊
     */
    public User register(String username, String password, String email) {
        // 檢查用戶名是否已存在
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用戶名已存在");
        }
        
        // 檢查電子郵件是否已存在
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("電子郵件已存在");
        }
        
        // 創建新用戶
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setIsActive(true);
        
        return userRepository.save(user);
    }
    
    /**
     * 用戶登錄驗證
     */
    public Optional<User> login(String loginId, String password) {
        Optional<User> userOpt = userRepository.findActiveUserByUsernameOrEmail(loginId);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return Optional.of(user);
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * 根據用戶名查找用戶
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * 根據ID查找用戶
     */
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }
    
    /**
     * 更新用戶資料
     */
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    /**
     * 檢查用戶名是否可用
     */
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }
    
    /**
     * 檢查電子郵件是否可用
     */
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
} 