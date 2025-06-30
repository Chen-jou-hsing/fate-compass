# 前端獨立部署配置指南

## 🎯 前端環境配置

### 1. 靜態網站託管平台部署

#### Vercel 部署
```bash
# 1. 安裝 Vercel CLI
npm i -g vercel

# 2. 在 frontend 目錄創建 vercel.json
{
  "version": 2,
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
  "env": {
    "API_BASE_URL": "https://api.your-backend.com/api",
    "CNCHAR_API_URL": "https://cnchar.your-backend.com",
    "NODE_ENV": "production"
  }
}

# 3. 建置前修改 env.js
# 在 build 之前替換環境變數
sed -i "s|http://localhost:8080/api|https://api.your-backend.com/api|g" js/env.js
sed -i "s|http://localhost:3001|https://cnchar.your-backend.com|g" js/env.js

# 4. 部署
vercel --prod
```

#### Netlify 部署
```bash
# 1. 在 frontend 目錄創建 netlify.toml
[build]
  command = "npm run build"
  publish = "dist"

[build.environment]
  API_BASE_URL = "https://api.your-backend.herokuapp.com/api"
  CNCHAR_API_URL = "https://cnchar.your-backend.herokuapp.com"
  NODE_ENV = "production"

# 2. 建置腳本 (package.json)
{
  "scripts": {
    "build": "bash build.sh"
  }
}

# 3. 創建 build.sh
#!/bin/bash
echo "🔧 配置生產環境..."

# 替換API地址
sed -i "s|http://localhost:8080/api|$API_BASE_URL|g" js/env.js
sed -i "s|http://localhost:3001|$CNCHAR_API_URL|g" js/env.js

echo "✅ 前端構建完成"

# 4. 部署
netlify deploy --prod --dir=frontend
```

#### GitHub Pages 部署
```yaml
# .github/workflows/deploy-frontend.yml
name: Deploy Frontend

on:
  push:
    branches: [ main ]
    paths: [ 'frontend/**' ]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v2
    
    - name: Configure Environment
      run: |
        cd frontend
        sed -i "s|http://localhost:8080/api|${{ secrets.API_BASE_URL }}|g" js/env.js
        sed -i "s|http://localhost:3001|${{ secrets.CNCHAR_API_URL }}|g" js/env.js
    
    - name: Deploy to GitHub Pages
      uses: JamesIves/github-pages-deploy-action@4.1.4
      with:
        branch: gh-pages
        folder: frontend

# 在 GitHub Secrets 中設置:
# API_BASE_URL: https://api.your-backend.com/api
# CNCHAR_API_URL: https://cnchar.your-backend.com
```

### 2. CDN 部署

#### AWS S3 + CloudFront
```bash
# 1. 配置生產環境
cd frontend
cp js/env.js js/env.js.bak

# 替換API地址
sed -i "s|http://localhost:8080/api|https://api.your-backend.com/api|g" js/env.js
sed -i "s|http://localhost:3001|https://cnchar.your-backend.com|g" js/env.js

# 2. 上傳到S3
aws s3 sync . s3://your-frontend-bucket --delete
aws cloudfront create-invalidation --distribution-id YOUR_DISTRIBUTION_ID --paths "/*"

# 3. 恢復本地配置
mv js/env.js.bak js/env.js
```

#### Azure Blob Storage + CDN
```bash
# 1. 配置並上傳
az storage blob upload-batch \
  --source frontend \
  --destination '$web' \
  --account-name yourfrontendstorage

# 2. 清理CDN緩存
az cdn endpoint purge \
  --resource-group your-rg \
  --name your-endpoint \
  --profile-name your-cdn-profile \
  --content-paths "/*"
```

### 3. 動態環境配置 (推薦)

#### 運行時環境配置
```html
<!-- 在 index.html 的 <head> 中添加 -->
<script>
  // 從 window.location.hostname 動態判斷環境
  (function() {
    const hostname = window.location.hostname;
    
    // 環境映射表
    const envConfig = {
      'localhost': {
        API_BASE_URL: 'http://localhost:8080/api',
        CNCHAR_API_URL: 'http://localhost:3001',
        NODE_ENV: 'development'
      },
      'your-frontend.vercel.app': {
        API_BASE_URL: 'https://api.your-backend.herokuapp.com/api',
        CNCHAR_API_URL: 'https://cnchar.your-backend.herokuapp.com',
        NODE_ENV: 'production'
      },
      'your-frontend.netlify.app': {
        API_BASE_URL: 'https://api.your-backend.railway.app/api',
        CNCHAR_API_URL: 'https://cnchar.your-backend.railway.app',
        NODE_ENV: 'production'
      },
      'fortune.your-domain.com': {
        API_BASE_URL: 'https://api.your-domain.com/api',
        CNCHAR_API_URL: 'https://cnchar.your-domain.com',
        NODE_ENV: 'production'
      }
    };
    
    // 設置環境變數
    window.ENV = envConfig[hostname] || envConfig['localhost'];
    
    console.log('🌍 環境配置:', window.ENV);
  })();
</script>
```

#### 配置文件注入
```javascript
// 創建 config.js (由 CI/CD 動態生成)
window.APP_CONFIG = {
  API_BASE_URL: '${API_BASE_URL}',
  CNCHAR_API_URL: '${CNCHAR_API_URL}',
  NODE_ENV: '${NODE_ENV}'
};

// 在 CI/CD 中替換
envsubst < config.template.js > config.js
```

### 4. 各平台部署腳本

#### Vercel 一鍵部署
```bash
#!/bin/bash
# deploy-frontend-vercel.sh

echo "🚀 部署前端到 Vercel..."

cd frontend

# 設置環境變數
export API_BASE_URL="https://api.your-backend.com/api"
export CNCHAR_API_URL="https://cnchar.your-backend.com"

# 配置環境
sed -i.bak "s|http://localhost:8080/api|$API_BASE_URL|g" js/env.js
sed -i.bak "s|http://localhost:3001|$CNCHAR_API_URL|g" js/env.js

# 部署
vercel --prod

# 恢復本地配置  
mv js/env.js.bak js/env.js

echo "✅ 前端部署完成！"
echo "URL: https://your-frontend.vercel.app"
```

#### Netlify 一鍵部署
```bash
#!/bin/bash
# deploy-frontend-netlify.sh

echo "🚀 部署前端到 Netlify..."

cd frontend

# 設置構建環境變數
export API_BASE_URL="https://api.your-backend.railway.app/api"
export CNCHAR_API_URL="https://cnchar.your-backend.railway.app"

# 建置並部署
npm run build
netlify deploy --prod --dir=.

echo "✅ 前端部署完成！"
echo "URL: https://your-frontend.netlify.app"
```

## 🔗 跨域配置檢查清單

### 前端需要設置的後端API地址
```javascript
// ✅ 正確的配置
window.ENV = {
  API_BASE_URL: 'https://api.your-backend.com/api',  // 後端API地址
  CNCHAR_API_URL: 'https://cnchar.your-backend.com'  // cnchar服務地址
};

// ❌ 錯誤的配置 (不要用localhost)
window.ENV = {
  API_BASE_URL: 'http://localhost:8080/api',  // 外網無法訪問
  CNCHAR_API_URL: 'http://localhost:3001'     // 外網無法訪問
};
```

### 後端需要允許的前端域名
```yaml
# application-production.yml
cors:
  allowed-origins: 
    - https://your-frontend.vercel.app      # Vercel部署地址
    - https://your-frontend.netlify.app     # Netlify部署地址
    - https://fortune.your-domain.com       # 自定義域名
```

## 🚀 完整部署流程

1. **部署後端** → 獲得API地址
2. **部署cnchar服務** → 獲得cnchar地址  
3. **配置前端環境** → 設置API地址
4. **部署前端** → 完成整個系統

這樣前後端就能在不同平台獨立運行了！ 