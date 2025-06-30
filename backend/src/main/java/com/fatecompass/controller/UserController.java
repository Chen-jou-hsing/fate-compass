package com.fatecompass.controller;

import com.fatecompass.entity.User;
import com.fatecompass.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 用戶控制器 - 處理用戶註冊、登錄等請求
 */
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用戶註冊
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String username = request.get("username");
            String password = request.get("password");
            String email = request.get("email");
            
            // 基本驗證
            if (username == null || username.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "用戶名不能為空");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (password == null || password.length() < 6) {
                response.put("success", false);
                response.put("message", "密碼長度至少6個字符");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 註冊用戶
            User user = userService.register(username, password, email);
            
            response.put("success", true);
            response.put("message", "註冊成功");
            response.put("userId", user.getUserId());
            response.put("username", user.getUsername());
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 用戶登錄
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String loginId = request.get("loginId");
            String password = request.get("password");
            
            if (loginId == null || loginId.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "請輸入用戶名或電子郵件");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (password == null || password.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "請輸入密碼");
                return ResponseEntity.badRequest().body(response);
            }
            
            Optional<User> userOpt = userService.login(loginId, password);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                response.put("success", true);
                response.put("message", "登錄成功");
                response.put("userId", user.getUserId());
                response.put("username", user.getUsername());
                response.put("email", user.getEmail());
                response.put("realName", user.getRealName());
                
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "用戶名或密碼錯誤");
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "登錄失敗：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 獲取用戶資料
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUserInfo(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<User> userOpt = userService.findById(userId);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                response.put("success", true);
                response.put("userId", user.getUserId());
                response.put("username", user.getUsername());
                response.put("email", user.getEmail());
                response.put("realName", user.getRealName());
                response.put("phone", user.getPhone());
                response.put("gender", user.getGender());
                response.put("birthDate", user.getBirthDate());
                response.put("birthPlace", user.getBirthPlace());
                response.put("createdAt", user.getCreatedAt());
                
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "用戶不存在");
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "獲取用戶資料失敗：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 更新用戶資料
     */
    @PutMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long userId, 
                                                         @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<User> userOpt = userService.findById(userId);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                // 更新用戶資料
                if (request.containsKey("realName")) {
                    user.setRealName((String) request.get("realName"));
                }
                if (request.containsKey("phone")) {
                    user.setPhone((String) request.get("phone"));
                }
                if (request.containsKey("gender")) {
                    String genderStr = (String) request.get("gender");
                    if ("MALE".equals(genderStr) || "FEMALE".equals(genderStr)) {
                        user.setGender(User.Gender.valueOf(genderStr));
                    }
                }
                if (request.containsKey("birthPlace")) {
                    user.setBirthPlace((String) request.get("birthPlace"));
                }
                
                User updatedUser = userService.updateUser(user);
                
                response.put("success", true);
                response.put("message", "更新成功");
                response.put("user", updatedUser);
                
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "用戶不存在");
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新失敗：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 檢查用戶名是否可用
     */
    @GetMapping("/check-username/{username}")
    public ResponseEntity<Map<String, Object>> checkUsername(@PathVariable String username) {
        Map<String, Object> response = new HashMap<>();
        boolean available = userService.isUsernameAvailable(username);
        
        response.put("available", available);
        response.put("message", available ? "用戶名可用" : "用戶名已被使用");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 檢查電子郵件是否可用
     */
    @GetMapping("/check-email/{email}")
    public ResponseEntity<Map<String, Object>> checkEmail(@PathVariable String email) {
        Map<String, Object> response = new HashMap<>();
        boolean available = userService.isEmailAvailable(email);
        
        response.put("available", available);
        response.put("message", available ? "電子郵件可用" : "電子郵件已被使用");
        
        return ResponseEntity.ok(response);
    }
} 