// 環境變數配置 - Vercel + Zeabur 部署優化
// 動態環境配置，自動適配不同部署平台

window.ENV = window.ENV || (function() {
    const hostname = window.location.hostname;
    const protocol = window.location.protocol;
    
    // 環境配置映射表
    const envConfigs = {
        // 本地開發環境
        'localhost': {
            API_BASE_URL: 'http://localhost:8080/api',
            CNCHAR_API_URL: 'http://localhost:3001',
            NODE_ENV: 'development',
            DEBUG: true
        },
        '127.0.0.1': {
            API_BASE_URL: 'http://localhost:8080/api',
            CNCHAR_API_URL: 'http://localhost:3001',
            NODE_ENV: 'development',
            DEBUG: true
        },
        
        // Vercel 部署環境 (需要替換為實際的 Zeabur 後端地址)
        'default_vercel': {
            API_BASE_URL: 'https://backend-xxx.zeabur.app/api',
            CNCHAR_API_URL: 'https://cnchar-service-xxx.zeabur.app',
            NODE_ENV: 'production',
            DEBUG: false
        }
    };
    
    // 獲取環境配置
    let config = envConfigs[hostname] || envConfigs['default_vercel'];
    
    // Vercel 自動檢測（如果是 .vercel.app 域名）
    if (hostname.includes('.vercel.app')) {
        config = {
            ...envConfigs['default_vercel'],
            NODE_ENV: 'production',
            DEBUG: false
        };
    }
    
    // 自定義域名檢測
    if (protocol === 'https:' && !hostname.includes('localhost') && !hostname.includes('127.0.0.1')) {
        config.NODE_ENV = 'production';
        config.DEBUG = false;
    }
    
    return config;
})();

// 除錯訊息 (僅在開發環境或DEBUG模式下顯示)
if (window.ENV.DEBUG) {
    console.log('🌍 環境配置:', window.ENV);
    console.log('📍 當前域名:', window.location.hostname);
    console.log('🔗 API地址:', window.ENV.API_BASE_URL);
    console.log('🔤 cnchar API:', window.ENV.CNCHAR_API_URL);
    console.log('🚀 平台: Vercel (前端) + Zeabur (後端)');
} 