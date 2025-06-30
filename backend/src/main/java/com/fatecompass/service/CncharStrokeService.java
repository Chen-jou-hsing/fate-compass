package com.fatecompass.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * cnchar筆劃計算服務
 * 調用Node.js cnchar微服務獲取準確的繁體中文筆劃數
 */
@Service
public class CncharStrokeService {
    
    private static final Logger logger = LoggerFactory.getLogger(CncharStrokeService.class);
    
    @Value("${cnchar.service.url:http://localhost:3001}")
    private String cncharServiceUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 筆劃緩存，避免重複調用
    private final Map<String, Integer> strokeCache = new HashMap<>();
    
    /**
     * 計算字符串的總筆劃數
     * 
     * @param text 要計算的文字
     * @return 總筆劃數
     */
    public int calculateTotalStrokes(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        
        // 檢查緩存
        String cacheKey = text.trim();
        if (strokeCache.containsKey(cacheKey)) {
            logger.debug("從緩存獲取筆劃數: {} = {}", cacheKey, strokeCache.get(cacheKey));
            return strokeCache.get(cacheKey);
        }
        
        try {
            // 調用cnchar微服務
            String url = cncharServiceUrl + "/strokes";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("text", text);
            
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                int totalStrokes = jsonNode.get("totalStrokes").asInt();
                
                // 緩存結果
                strokeCache.put(cacheKey, totalStrokes);
                
                logger.info("cnchar計算筆劃: {} = {} 劃", text, totalStrokes);
                return totalStrokes;
            } else {
                logger.error("cnchar服務回應異常: {}", response.getStatusCode());
                throw new RuntimeException("cnchar服務不可用，回應狀態: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            logger.error("調用cnchar服務失敗: {}", e.getMessage());
            throw new RuntimeException("無法連接cnchar服務: " + e.getMessage(), e);
        }
    }
    
    /**
     * 獲取單個字符的筆劃數
     * 
     * @param character 字符
     * @return 筆劃數
     */
    public int getCharacterStrokes(char character) {
        String charStr = String.valueOf(character);
        
        // 檢查緩存
        if (strokeCache.containsKey(charStr)) {
            return strokeCache.get(charStr);
        }
        
        try {
            String url = cncharServiceUrl + "/stroke/" + character;
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                int strokes = jsonNode.get("strokes").asInt();
                
                // 緩存結果
                strokeCache.put(charStr, strokes);
                
                return strokes;
            } else {
                logger.error("cnchar服務回應異常: {}", response.getStatusCode());
                throw new RuntimeException("cnchar服務不可用，回應狀態: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            logger.error("調用cnchar服務失敗: {}", e.getMessage());
            throw new RuntimeException("無法連接cnchar服務: " + e.getMessage(), e);
        }
    }
    
    /**
     * 檢查cnchar服務健康狀態
     * 
     * @return 是否健康
     */
    public boolean isServiceHealthy() {
        try {
            String url = cncharServiceUrl + "/health";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.warn("cnchar服務健康檢查失敗: {}", e.getMessage());
            return false;
        }
    }
    

    
    /**
     * 清空緩存
     */
    public void clearCache() {
        strokeCache.clear();
        logger.info("筆劃緩存已清空");
    }
    
    /**
     * 獲取緩存大小
     */
    public int getCacheSize() {
        return strokeCache.size();
    }
} 