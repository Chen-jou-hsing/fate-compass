package com.fatecompass.controller;

import com.fatecompass.entity.FortuneHistory;
import com.fatecompass.entity.User;
import com.fatecompass.service.FortuneService;
import com.fatecompass.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 算命控制器 - 處理算命相關請求
 */
@RestController
@RequestMapping("/fortune")
@CrossOrigin(origins = "*")
public class FortuneController {
    
    @Autowired
    private FortuneService fortuneService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 生辰八字算命
     */
    @PostMapping("/bazi")
    public ResponseEntity<Map<String, Object>> calculateBaZi(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            String birthDateStr = (String) request.get("birthDate");
            String birthTimeStr = (String) request.get("birthTime");
            String birthPlace = (String) request.get("birthPlace");
            
            // 驗證用戶
            Optional<User> userOpt = userService.findById(userId);
            if (!userOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "用戶不存在");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 解析生辰日期時間
            LocalDateTime birthDateTime;
            try {
                String fullDateTime = birthDateStr + " " + birthTimeStr;
                birthDateTime = LocalDateTime.parse(fullDateTime, 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            } catch (Exception e) {
                response.put("success", false);
                response.put("message", "日期時間格式錯誤，請使用 yyyy-MM-dd HH:mm 格式");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 執行八字算命
            Map<String, Object> result = fortuneService.calculateBaZi(userOpt.get(), birthDateTime, birthPlace);
            
            response.put("success", true);
            response.put("message", "八字算命完成");
            response.putAll(result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "算命失敗：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 姓名算命
     */
    @PostMapping("/name")
    public ResponseEntity<Map<String, Object>> calculateNameFortune(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = Long.valueOf(request.get("userId"));
            String fullName = request.get("fullName");
            
            if (fullName == null || fullName.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "請輸入姓名");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 驗證用戶
            Optional<User> userOpt = userService.findById(userId);
            if (!userOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "用戶不存在");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 執行姓名算命
            Map<String, Object> result = fortuneService.calculateNameFortune(userOpt.get(), fullName);
            
            response.put("success", true);
            response.put("message", "姓名算命完成");
            response.putAll(result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "算命失敗：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 每日運勢查詢
     */
    @GetMapping("/daily/{userId}/{zodiac}")
    public ResponseEntity<Map<String, Object>> getDailyFortune(@PathVariable Long userId, 
                                                              @PathVariable String zodiac) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 驗證用戶
            Optional<User> userOpt = userService.findById(userId);
            if (!userOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "用戶不存在");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 執行每日運勢查詢
            Map<String, Object> result = fortuneService.getDailyFortune(userOpt.get(), zodiac);
            
            response.put("success", true);
            response.put("message", "每日運勢查詢完成");
            response.putAll(result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "查詢失敗：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 獲取用戶算命歷史記錄
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<Map<String, Object>> getFortuneHistory(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 驗證用戶
            Optional<User> userOpt = userService.findById(userId);
            if (!userOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "用戶不存在");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 獲取算命歷史
            List<FortuneHistory> histories = fortuneService.getUserFortuneHistory(userId);
            
            response.put("success", true);
            response.put("message", "獲取歷史記錄成功");
            response.put("histories", histories);
            response.put("total", histories.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "獲取歷史記錄失敗：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 獲取支援的生肖列表
     */
    @GetMapping("/zodiac-list")
    public ResponseEntity<Map<String, Object>> getZodiacList() {
        Map<String, Object> response = new HashMap<>();
        
        String[] zodiacs = {"鼠", "牛", "虎", "兔", "龍", "蛇", "馬", "羊", "猴", "雞", "狗", "豬"};
        
        response.put("success", true);
        response.put("zodiacs", zodiacs);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 獲取算命統計資訊
     */
    @GetMapping("/stats/{userId}")
    public ResponseEntity<Map<String, Object>> getFortuneStats(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 驗證用戶
            Optional<User> userOpt = userService.findById(userId);
            if (!userOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "用戶不存在");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 獲取算命歷史進行統計
            List<FortuneHistory> histories = fortuneService.getUserFortuneHistory(userId);
            
            // 統計各類型算命次數
            Map<String, Integer> typeCount = new HashMap<>();
            int totalScore = 0;
            
            for (FortuneHistory history : histories) {
                String type = history.getFortuneType().getDescription();
                typeCount.put(type, typeCount.getOrDefault(type, 0) + 1);
                if (history.getScore() != null) {
                    totalScore += history.getScore();
                }
            }
            
            double averageScore = histories.isEmpty() ? 0 : (double) totalScore / histories.size();
            
            response.put("success", true);
            response.put("totalCount", histories.size());
            response.put("typeCount", typeCount);
            response.put("averageScore", Math.round(averageScore * 100.0) / 100.0);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "獲取統計資訊失敗：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 