# 環境變數配置指南

## 🌍 概述

本項目支援透過環境變數進行配置，適合不同部署環境（開發、測試、生產）。

## 📋 必要環境變數

### 後端服務 (Java Spring Boot)

```bash
# 服務器配置
SERVER_PORT=8080
CONTEXT_PATH=/api

# 資料庫配置
DATABASE_URL=jdbc:oracle:thin:@your-oracle-host:1521:XE
DATABASE_USERNAME=fate_compass
DATABASE_PASSWORD=your_secure_password

# JWT安全配置
JWT_SECRET=your-very-long-and-secure-jwt-secret-key-here
JWT_EXPIRATION=86400000

# cnchar微服務配置
CNCHAR_SERVICE_URL=http://your-cnchar-service:3001
CNCHAR_TIMEOUT=5000

# CORS配置 (多個域名用逗號分隔)
CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com,https://www.your-domain.com
```

### 前端配置 (JavaScript)

前端是純JavaScript，不是React，所以不需要REACT_APP前綴。

**配置方式1 - 直接編輯 `frontend/js/env.js`**:
```javascript
window.ENV = {
    API_BASE_URL: 'https://your-backend-domain.com/api',
    CNCHAR_API_URL: 'https://your-cnchar-service:3001',
    NODE_ENV: 'production',
    DEBUG: false
};
```

**配置方式2 - 透過建置腳本替換**:
```bash
# 用sed替換env.js中的URL
sed -i "s|http://localhost:8080/api|https://your-domain.com/api|g" frontend/js/env.js
```

## 🔧 部署環境範例

### 1. 本地開發環境

```bash
# .env.local
DATABASE_URL=jdbc:oracle:thin:@localhost:1521:XE
DATABASE_USERNAME=fate_compass
DATABASE_PASSWORD=oracle123
CNCHAR_SERVICE_URL=http://localhost:3001
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:8081
LOG_LEVEL=DEBUG
JPA_SHOW_SQL=true
```

### 2. 生產環境

```bash
# .env.production
DATABASE_URL=jdbc:oracle:thin:@prod-oracle.your-domain.com:1521:PRODDB
DATABASE_USERNAME=fate_compass_prod
DATABASE_PASSWORD=your_very_secure_production_password
CNCHAR_SERVICE_URL=https://cnchar.your-domain.com
CORS_ALLOWED_ORIGINS=https://fortune.your-domain.com
JWT_SECRET=your-super-secure-production-jwt-secret-with-at-least-64-characters
LOG_LEVEL=INFO
JPA_SHOW_SQL=false
JPA_DDL_AUTO=validate
```

### 3. Docker Compose 環境

```yaml
# docker-compose.yml
environment:
  - DATABASE_URL=jdbc:oracle:thin:@oracle:1521:XE
  - DATABASE_USERNAME=fate_compass
  - DATABASE_PASSWORD=${ORACLE_PASSWORD}
  - CNCHAR_SERVICE_URL=http://cnchar:3001
  - CORS_ALLOWED_ORIGINS=http://localhost,https://your-domain.com
```

## 📄 .env文件位置和使用

### .env文件結構
```
fate-compass/
├── .env                    # 主要環境變數文件（用於Docker Compose）
├── .env.local             # 本地開發環境變數
├── .env.production        # 生產環境變數
├── backend/
│   └── src/main/resources/
│       ├── application.yml              # 開發配置
│       └── application-production.yml   # 生產配置
└── frontend/
    └── js/
        └── env.js         # 前端環境配置
```

### 創建.env文件
```bash
# 在項目根目錄創建.env文件
cd fate-compass
cat > .env << 'EOF'
# 資料庫配置
DATABASE_URL=jdbc:oracle:thin:@localhost:1521:XE
DATABASE_USERNAME=fate_compass
DATABASE_PASSWORD=oracle123

# JWT配置
JWT_SECRET=your-secure-jwt-secret-key-here

# cnchar服務
CNCHAR_SERVICE_URL=http://localhost:3001

# CORS配置
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:8081
EOF
```

## 🚀 啟動配置

### Spring Boot Profile

```bash
# 使用生產配置
java -jar backend.jar --spring.profiles.active=production

# 使用環境變數覆蓋
export DATABASE_PASSWORD=your_password
java -jar backend.jar --spring.profiles.active=production
```

### Docker 部署

```bash
# 設定環境變數檔案
cp ENV_CONFIG.md .env
nano .env  # 編輯環境變數

# 使用環境變數啟動
docker-compose --env-file .env up -d
```

### Kubernetes 部署

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: fate-compass-config
data:
  DATABASE_URL: "jdbc:oracle:thin:@oracle-service:1521:XE"
  CNCHAR_SERVICE_URL: "http://cnchar-service:3001"
  LOG_LEVEL: "INFO"
  
---
apiVersion: v1
kind: Secret
metadata:
  name: fate-compass-secrets
data:
  DATABASE_PASSWORD: base64-encoded-password
  JWT_SECRET: base64-encoded-jwt-secret
```

## 🔐 安全注意事項

### 1. 敏感資訊保護
```bash
# 絕對不要將這些資訊提交到版本控制
DATABASE_PASSWORD=*****
JWT_SECRET=*****
ORACLE_PASSWORD=*****
```

### 2. JWT Secret 要求
- 至少64個字符
- 包含字母、數字、特殊字符
- 每個環境使用不同的secret

### 3. 資料庫安全
- 使用強密碼
- 限制資料庫連接來源IP
- 定期更換密碼

## 📊 配置驗證

### 檢查配置是否正確

```bash
# 檢查後端健康狀態
curl http://your-backend:8080/api/actuator/health

# 檢查cnchar微服務
curl http://your-cnchar:3001/health

# 檢查前端配置
# 在瀏覽器控制台查看 window.ENV
```

### 常見問題排除

1. **CORS錯誤**: 檢查 `CORS_ALLOWED_ORIGINS` 是否包含前端域名
2. **資料庫連接失敗**: 驗證 `DATABASE_URL` 和認證資訊
3. **cnchar服務連接失敗**: 確認 `CNCHAR_SERVICE_URL` 可達性
4. **JWT錯誤**: 檢查 `JWT_SECRET` 配置

## 🌐 雲端部署建議

### AWS
```bash
# 使用AWS Systems Manager Parameter Store
aws ssm put-parameter --name "/fate-compass/DATABASE_PASSWORD" --value "secure-password" --type "SecureString"
```

### Azure
```bash
# 使用Azure Key Vault
az keyvault secret set --vault-name "fate-compass-vault" --name "database-password" --value "secure-password"
```

### Google Cloud
```bash
# 使用Secret Manager
gcloud secrets create database-password --data-file=password.txt
```

---

**重要**: 請根據您的部署環境調整這些配置，並確保所有敏感資訊都得到適當保護。 