# 後端獨立部署配置指南

## 🎯 後端環境變數配置

### AWS部署
```bash
# AWS ECS Task Definition 環境變數
{
  "environment": [
    {"name": "SPRING_PROFILES_ACTIVE", "value": "production"},
    {"name": "DATABASE_URL", "value": "jdbc:oracle:thin:@your-rds.region.rds.amazonaws.com:1521:PRODDB"},
    {"name": "DATABASE_USERNAME", "value": "fate_compass"},
    {"name": "DATABASE_PASSWORD", "valueFrom": "arn:aws:ssm:region:account:parameter/fate-compass/db-password"},
    {"name": "JWT_SECRET", "valueFrom": "arn:aws:ssm:region:account:parameter/fate-compass/jwt-secret"},
    {"name": "CNCHAR_SERVICE_URL", "value": "https://cnchar-api.your-domain.com"},
    {"name": "CORS_ALLOWED_ORIGINS", "value": "https://fortune.vercel.app,https://your-frontend-domain.com"}
  ]
}

# AWS Lambda 環境變數 (如果使用Serverless)
aws lambda update-function-configuration \
  --function-name fate-compass-backend \
  --environment Variables='{
    "DATABASE_URL":"jdbc:oracle:thin:@your-rds:1521:PRODDB",
    "DATABASE_PASSWORD":"your_password",
    "CORS_ALLOWED_ORIGINS":"https://your-frontend.vercel.app"
  }'
```

### Azure部署
```bash
# Azure App Service 環境變數
az webapp config appsettings set \
  --resource-group fate-compass-rg \
  --name fate-compass-backend \
  --settings \
    SPRING_PROFILES_ACTIVE=production \
    DATABASE_URL="jdbc:oracle:thin:@your-azure-oracle:1521:PRODDB" \
    DATABASE_PASSWORD="@Microsoft.KeyVault(VaultName=fate-compass-vault;SecretName=db-password)" \
    JWT_SECRET="@Microsoft.KeyVault(VaultName=fate-compass-vault;SecretName=jwt-secret)" \
    CNCHAR_SERVICE_URL="https://cnchar.azurewebsites.net" \
    CORS_ALLOWED_ORIGINS="https://your-frontend.netlify.app"
```

### Google Cloud部署
```bash
# GCP Cloud Run 環境變數
gcloud run deploy fate-compass-backend \
  --image gcr.io/your-project/backend:latest \
  --set-env-vars "SPRING_PROFILES_ACTIVE=production" \
  --set-env-vars "DATABASE_URL=jdbc:oracle:thin:@your-cloud-sql:1521:PRODDB" \
  --set-env-vars "CNCHAR_SERVICE_URL=https://cnchar-service-hash.uc.r.appspot.com" \
  --set-env-vars "CORS_ALLOWED_ORIGINS=https://your-frontend.netlify.app"

# 敏感資訊使用Secret Manager
gcloud run deploy fate-compass-backend \
  --set-env-vars "DATABASE_PASSWORD_SECRET=projects/your-project/secrets/db-password/versions/latest" \
  --set-env-vars "JWT_SECRET_SECRET=projects/your-project/secrets/jwt-secret/versions/latest"
```

### VPS/獨立服務器
```bash
# 創建環境變數文件
sudo nano /opt/fate-compass/.env

# 填入配置
SPRING_PROFILES_ACTIVE=production
DATABASE_URL=jdbc:oracle:thin:@your-oracle-server:1521:PRODDB
DATABASE_USERNAME=fate_compass
DATABASE_PASSWORD=your_secure_password
JWT_SECRET=your-64-character-jwt-secret-key
CNCHAR_SERVICE_URL=https://cnchar.your-domain.com
CORS_ALLOWED_ORIGINS=https://your-frontend.netlify.app,https://fortune.vercel.app

# 啟動服務
cd /opt/fate-compass
java -jar backend.jar --spring.config.location=.env
```

## 🗄 資料庫配置

### AWS RDS Oracle
```bash
DATABASE_URL=jdbc:oracle:thin:@fate-compass.cluster-xyz.region.rds.amazonaws.com:1521:PRODDB
DATABASE_USERNAME=fate_compass
DATABASE_PASSWORD=your_rds_password
```

### Azure Database for Oracle
```bash
DATABASE_URL=jdbc:oracle:thin:@fate-compass.oracle.database.azure.com:1521:PRODDB
DATABASE_USERNAME=fate_compass@fate-compass
DATABASE_PASSWORD=your_azure_password
```

### Google Cloud SQL Oracle
```bash
DATABASE_URL=jdbc:oracle:thin:@google-cloud-sql-ip:1521:PRODDB
DATABASE_USERNAME=fate_compass
DATABASE_PASSWORD=your_gcp_password
```

## 🔧 cnchar微服務部署

### 獨立部署cnchar服務
```bash
# 部署到Heroku
heroku create fate-compass-cnchar
heroku config:set NODE_ENV=production
git subtree push --prefix=cnchar-service heroku main

# 部署到Vercel (serverless function)
cd cnchar-service
vercel --prod

# 部署到AWS Lambda
serverless deploy --stage prod
```

## 🌐 CORS重要配置

由於前後端在不同域名，CORS配置非常重要：

```yaml
# application-production.yml
cors:
  allowed-origins: 
    - https://your-frontend.vercel.app
    - https://your-frontend.netlify.app  
    - https://fortune.your-domain.com
    - https://www.your-domain.com
  allowed-methods: GET,POST,PUT,DELETE,OPTIONS
  allowed-headers: "*"
  allow-credentials: true
  max-age: 3600
```

## 🚀 部署腳本範例

### 一鍵部署腳本
```bash
#!/bin/bash
# deploy-backend.sh

echo "🚀 開始部署後端服務..."

# 設置環境變數
export SPRING_PROFILES_ACTIVE=production
export DATABASE_URL="jdbc:oracle:thin:@your-prod-db:1521:PRODDB"
export DATABASE_PASSWORD="your_password"
export JWT_SECRET="your_jwt_secret"
export CNCHAR_SERVICE_URL="https://cnchar.your-domain.com"
export CORS_ALLOWED_ORIGINS="https://your-frontend.netlify.app"

# 構建應用
mvn clean package -DskipTests

# 部署到雲平台 (依據你的平台選擇)
# AWS: aws ecs update-service
# Azure: az webapp deployment source config-zip
# GCP: gcloud run deploy

echo "✅ 後端部署完成！"
echo "API地址: https://api.your-domain.com"
``` 