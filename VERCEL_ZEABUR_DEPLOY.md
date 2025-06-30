# Vercel + Zeabur 部署指南

## 🚀 部署架構

```
Vercel (前端) ←→ Zeabur (後端 + cnchar + Oracle)
```

## 📦 第一步：Zeabur 後端部署

### 1. Zeabur 項目設置

```bash
# 1. 在 Zeabur Dashboard 創建新項目
# 2. 連接 GitHub 倉庫
# 3. 選擇 backend 目錄作為服務根目錄
```

### 2. Zeabur 環境變數配置

在 Zeabur Dashboard > Variables 中設置：

```bash
# Spring Boot 配置
SPRING_PROFILES_ACTIVE=production
PORT=8080

# 資料庫配置 (使用 Zeabur 內建 Oracle 或外部資料庫)
DATABASE_URL=jdbc:oracle:thin:@your-oracle.zeabur.app:1521:PRODDB
DATABASE_USERNAME=fate_compass
DATABASE_PASSWORD=your_secure_password

# JWT 安全配置
JWT_SECRET=your-super-secure-jwt-secret-64-characters-minimum-length-key

# cnchar 微服務 URL (部署在同一個 Zeabur 項目中)
CNCHAR_SERVICE_URL=https://cnchar-service-xxx.zeabur.app

# CORS 配置 (允許 Vercel 前端)
CORS_ALLOWED_ORIGINS=https://your-project.vercel.app,https://fortune.vercel.app

# 日誌配置
LOG_LEVEL=INFO
JPA_SHOW_SQL=false
JPA_DDL_AUTO=validate
```

### 3. Zeabur 部署配置文件

創建 `zeabur.yml`：
```yaml
# backend/zeabur.yml
services:
  backend:
    build:
      dockerfile: Dockerfile
    environment:
      SPRING_PROFILES_ACTIVE: production
    ports:
      - "8080:8080"
    
  cnchar-service:
    build:
      context: ../cnchar-service
      dockerfile: Dockerfile
    environment:
      NODE_ENV: production
    ports:
      - "3001:3001"
      
  oracle-db:
    image: gvenzl/oracle-xe:latest
    environment:
      ORACLE_PASSWORD: oracle123
    volumes:
      - oracle_data:/opt/oracle/oradata
```

### 4. Zeabur Dockerfile 優化

更新 `backend/Dockerfile`：
```dockerfile
# 多階段構建優化
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# 運行時鏡像
FROM openjdk:17-jdk-slim

WORKDIR /app
RUN mkdir -p logs

COPY --from=builder /app/target/fate-compass-backend.jar app.jar

# Zeabur 健康檢查
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD curl -f http://localhost:8080/api/actuator/health || exit 1

EXPOSE 8080

# 優化的 JVM 參數
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

## 🌐 第二步：Vercel 前端部署

### 1. Vercel 項目設置

```bash
# 1. 安裝 Vercel CLI
npm i -g vercel

# 2. 在 frontend 目錄初始化
cd frontend
vercel init
```

### 2. 創建 `vercel.json`

```json
{
  "version": 2,
  "name": "fate-compass-frontend",
  "builds": [
    {
      "src": "**/*",
      "use": "@vercel/static"
    }
  ],
  "routes": [
    {
      "src": "/(.*)",
      "dest": "/$1"
    }
  ],
  "headers": [
    {
      "source": "/(.*)",
      "headers": [
        {
          "key": "X-Content-Type-Options",
          "value": "nosniff"
        },
        {
          "key": "X-Frame-Options", 
          "value": "DENY"
        },
        {
          "key": "X-XSS-Protection",
          "value": "1; mode=block"
        }
      ]
    }
  ]
}
```

### 3. Vercel 環境變數配置

在 Vercel Dashboard > Settings > Environment Variables 中設置：

```bash
# API 配置
API_BASE_URL=https://backend-xxx.zeabur.app/api
CNCHAR_API_URL=https://cnchar-service-xxx.zeabur.app
NODE_ENV=production
```

### 4. 動態環境配置 (推薦)

更新 `frontend/js/env.js`：
```javascript
// 動態環境配置，支援多個部署環境
window.ENV = window.ENV || (function() {
    const hostname = window.location.hostname;
    
    // 環境配置映射
    const envConfigs = {
        // 本地開發
        'localhost': {
            API_BASE_URL: 'http://localhost:8080/api',
            CNCHAR_API_URL: 'http://localhost:3001',
            NODE_ENV: 'development',
            DEBUG: true
        },
        
        // Vercel 生產環境
        'your-project.vercel.app': {
            API_BASE_URL: 'https://backend-xxx.zeabur.app/api',
            CNCHAR_API_URL: 'https://cnchar-service-xxx.zeabur.app',
            NODE_ENV: 'production',
            DEBUG: false
        },
        
        // 自定義域名
        'fortune.your-domain.com': {
            API_BASE_URL: 'https://api.your-domain.com/api',
            CNCHAR_API_URL: 'https://cnchar.your-domain.com',
            NODE_ENV: 'production',
            DEBUG: false
        }
    };
    
    const config = envConfigs[hostname] || envConfigs['localhost'];
    
    // 從 Vercel 環境變數覆蓋 (如果存在)
    if (typeof process !== 'undefined' && process.env) {
        config.API_BASE_URL = process.env.API_BASE_URL || config.API_BASE_URL;
        config.CNCHAR_API_URL = process.env.CNCHAR_API_URL || config.CNCHAR_API_URL;
    }
    
    console.log('🌍 環境配置:', config);
    return config;
})();
```

### 5. Vercel 部署腳本

創建 `deploy-vercel.sh`：
```bash
#!/bin/bash
# deploy-vercel.sh

echo "🚀 部署前端到 Vercel..."

cd frontend

# 確保有正確的 Zeabur 後端地址
ZEABUR_BACKEND="https://backend-xxx.zeabur.app"
ZEABUR_CNCHAR="https://cnchar-service-xxx.zeabur.app"

# 更新環境配置
sed -i.bak "s|backend-xxx.zeabur.app|${ZEABUR_BACKEND#https://}|g" js/env.js
sed -i.bak "s|cnchar-service-xxx.zeabur.app|${ZEABUR_CNCHAR#https://}|g" js/env.js

# 部署到 Vercel
vercel --prod

# 恢復本地配置
if [ -f js/env.js.bak ]; then
    mv js/env.js.bak js/env.js
fi

echo "✅ 前端部署完成！"
echo "📱 前端地址: https://your-project.vercel.app"
echo "🔗 後端地址: $ZEABUR_BACKEND/api"
```

## 🔗 第三步：配置域名和CORS

### 1. 在 Zeabur 後端更新 CORS

更新 `backend/src/main/resources/application-production.yml`：
```yaml
cors:
  allowed-origins: 
    - https://your-project.vercel.app          # Vercel 自動域名
    - https://fortune.vercel.app               # 自定義 Vercel 域名
    - https://fortune.your-domain.com          # 如果綁定自定義域名
  allowed-methods: GET,POST,PUT,DELETE,OPTIONS
  allowed-headers: "*"
  allow-credentials: true
  max-age: 3600
```

### 2. 在 Vercel 設置自定義域名 (可選)

```bash
# 在 Vercel Dashboard > Domains 中添加
fortune.your-domain.com

# 然後在 DNS 提供商添加 CNAME 記錄
CNAME fortune cname.vercel-dns.com
```

## 🧪 第四步：測試部署

### 1. 健康檢查

```bash
# 檢查 Zeabur 後端
curl https://backend-xxx.zeabur.app/api/actuator/health

# 檢查 cnchar 服務
curl https://cnchar-service-xxx.zeabur.app/health

# 檢查 Vercel 前端
curl https://your-project.vercel.app
```

### 2. 功能測試

```javascript
// 在 Vercel 前端瀏覽器控制台測試
console.log('環境配置:', window.ENV);

// 測試 API 連接
fetch(window.ENV.API_BASE_URL + '/actuator/health')
  .then(res => res.json())
  .then(data => console.log('後端健康狀態:', data));

// 測試 cnchar 服務
fetch(window.ENV.CNCHAR_API_URL + '/health')
  .then(res => res.json())
  .then(data => console.log('cnchar 服務狀態:', data));
```

## 📋 部署檢查清單

### Zeabur 後端
- ✅ Spring Boot 應用正常啟動
- ✅ Oracle 資料庫連接成功  
- ✅ cnchar 微服務運行正常
- ✅ CORS 包含 Vercel 域名
- ✅ 健康檢查端點可訪問

### Vercel 前端  
- ✅ 靜態文件部署成功
- ✅ 環境變數配置正確
- ✅ API 地址指向 Zeabur 後端
- ✅ 跨域請求正常工作
- ✅ 自定義域名設置 (如需要)

## 🚀 一鍵部署命令

```bash
# 完整部署流程
git push origin main                    # 觸發 Zeabur 自動部署
cd frontend && vercel --prod           # 部署前端到 Vercel

# 檢查部署狀態
curl https://your-project.vercel.app
curl https://backend-xxx.zeabur.app/api/actuator/health
```

這樣就完成了 Vercel + Zeabur 的完整部署配置！🎉 