// 應用配置 - 支援環境變數配置
const APP_CONFIG = {
    API_BASE_URL: window.ENV?.API_BASE_URL || 
                  (typeof process !== 'undefined' && process.env?.VITE_API_BASE_URL) ||
                  (window.location.hostname.includes('vercel.app') ? 'https://fate-compass.zeabur.app/api' : 'http://localhost:8080/api'),
    CNCHAR_API_URL: window.ENV?.CNCHAR_API_URL || 
                    (typeof process !== 'undefined' && process.env?.VITE_CNCHAR_API_URL) ||
                    (window.location.hostname.includes('vercel.app') ? 'https://cnchar.zeabur.app' : 'http://localhost:3001'),
    USER_KEY: 'fate_compass_user'
};

// 全局變量
let currentUser = null;

// 頁面載入完成後初始化
$(document).ready(function() {
    initializeApp();
    setupEventListeners();
    checkUserLogin();
});

// 初始化應用
function initializeApp() {
    console.log('算命網站初始化...');
    showHome();
}

// 設置事件監聽器
function setupEventListeners() {
    // 登錄表單提交
    $('#loginForm').on('submit', handleLogin);
    
    // 註冊表單提交
    $('#registerForm').on('submit', handleRegister);
    
    // 生辰八字表單提交
    $('#baziForm').on('submit', handleBaziCalculation);
    
    // 姓名算命表單提交
    $('#nameFortuneForm').on('submit', handleNameFortune);
    
    // 每日運勢表單提交
    $('#dailyFortuneForm').on('submit', handleDailyFortune);
}

// 檢查用戶登錄狀態
function checkUserLogin() {
    const userData = localStorage.getItem(APP_CONFIG.USER_KEY);
    if (userData) {
        try {
            currentUser = JSON.parse(userData);
            updateUIForLoggedInUser();
        } catch (e) {
            console.error('用戶數據解析錯誤:', e);
            localStorage.removeItem(APP_CONFIG.USER_KEY);
        }
    }
}

// 更新UI為已登錄用戶
function updateUIForLoggedInUser() {
    $('#loginBtn, #registerBtn').hide();
    $('#userDropdown').show();
    $('#usernameDisplay').text(currentUser.username);
}

// 更新UI為未登錄用戶
function updateUIForLoggedOutUser() {
    $('#loginBtn, #registerBtn').show();
    $('#userDropdown').hide();
    currentUser = null;
}

// 頁面切換函數
function showPage(pageId) {
    $('.page-content').hide();
    $(`#${pageId}`).show();
}

function showHome() {
    showPage('homePage');
}

function showLogin() {
    showPage('loginPage');
    $('#loginForm')[0].reset();
}

function showRegister() {
    showPage('registerPage');
    $('#registerForm')[0].reset();
}

function showBaZi() {
    if (!checkAuthentication()) return;
    showPage('baziPage');
    $('#baziForm')[0].reset();
    $('#baziResult').hide();
}

function showNameFortune() {
    if (!checkAuthentication()) return;
    showPage('namefortunePage');
    $('#nameFortuneForm')[0].reset();
    $('#nameFortuneResult').hide();
}

function showDailyFortune() {
    if (!checkAuthentication()) return;
    showPage('dailyFortunePage');
    $('#dailyFortuneForm')[0].reset();
    $('#dailyFortuneResult').hide();
}

// 檢查用戶認證
function checkAuthentication() {
    if (!currentUser) {
        showAlert('請先登錄後再使用算命功能', 'warning');
        showLogin();
        return false;
    }
    return true;
}

// 處理登錄
function handleLogin(e) {
    e.preventDefault();
    
    const loginData = {
        loginId: $('#loginId').val().trim(),
        password: $('#loginPassword').val()
    };
    
    if (!loginData.loginId || !loginData.password) {
        showAlert('請填寫完整的登錄信息', 'danger');
        return;
    }
    
    const submitBtn = $('#loginForm button[type="submit"]');
    const originalText = submitBtn.html();
    submitBtn.html('<span class="loading"></span> 登錄中...').prop('disabled', true);
    
    $.ajax({
        url: `${APP_CONFIG.API_BASE_URL}/user/login`,
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(loginData),
        success: function(response) {
            if (response.success) {
                currentUser = {
                    userId: response.userId,
                    username: response.username,
                    email: response.email,
                    realName: response.realName
                };
                
                localStorage.setItem(APP_CONFIG.USER_KEY, JSON.stringify(currentUser));
                updateUIForLoggedInUser();
                showAlert('登錄成功！', 'success');
                showHome();
            } else {
                showAlert(response.message || '登錄失敗', 'danger');
            }
        },
        error: function(xhr, status, error) {
            console.error('登錄錯誤:', error);
            showAlert('登錄失敗，請檢查網絡連接', 'danger');
        },
        complete: function() {
            submitBtn.html(originalText).prop('disabled', false);
        }
    });
}

// 處理註冊
function handleRegister(e) {
    e.preventDefault();
    
    const registerData = {
        username: $('#registerUsername').val().trim(),
        email: $('#registerEmail').val().trim(),
        password: $('#registerPassword').val()
    };
    
    // 基本驗證
    if (!registerData.username || !registerData.email || !registerData.password) {
        showAlert('請填寫完整的註冊信息', 'danger');
        return;
    }
    
    if (registerData.username.length < 3) {
        showAlert('用戶名長度至少3個字符', 'danger');
        return;
    }
    
    if (registerData.password.length < 6) {
        showAlert('密碼長度至少6個字符', 'danger');
        return;
    }
    
    const submitBtn = $('#registerForm button[type="submit"]');
    const originalText = submitBtn.html();
    submitBtn.html('<span class="loading"></span> 註冊中...').prop('disabled', true);
    
    $.ajax({
        url: `${APP_CONFIG.API_BASE_URL}/user/register`,
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(registerData),
        success: function(response) {
            if (response.success) {
                showAlert('註冊成功！請登錄', 'success');
                showLogin();
                $('#loginId').val(registerData.username);
            } else {
                showAlert(response.message || '註冊失敗', 'danger');
            }
        },
        error: function(xhr, status, error) {
            console.error('註冊錯誤:', error);
            showAlert('註冊失敗，請檢查網絡連接', 'danger');
        },
        complete: function() {
            submitBtn.html(originalText).prop('disabled', false);
        }
    });
}

// 處理生辰八字算命
function handleBaziCalculation(e) {
    e.preventDefault();
    
    const baziData = {
        userId: currentUser.userId,
        birthDate: $('#birthDate').val(),
        birthTime: $('#birthTime').val(),
        birthPlace: $('#birthPlace').val().trim() || '未知'
    };
    
    if (!baziData.birthDate || !baziData.birthTime) {
        showAlert('請填寫出生日期和時間', 'danger');
        return;
    }
    
    const submitBtn = $('#baziForm button[type="submit"]');
    const originalText = submitBtn.html();
    submitBtn.html('<span class="loading"></span> 算命中...').prop('disabled', true);
    
    console.log('發送八字數據:', baziData);
    
    $.ajax({
        url: `${APP_CONFIG.API_BASE_URL}/fortune/bazi`,
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(baziData),
        success: function(response) {
            console.log('八字響應:', response);
            if (response.success) {
                displayBaziResult(response);
                showAlert('八字算命完成！', 'success');
            } else {
                showAlert(response.message || '算命失敗', 'danger');
            }
        },
        error: function(xhr, status, error) {
            console.error('八字算命錯誤:', error, xhr.responseText);
            showAlert('算命失敗：' + (xhr.responseJSON?.message || error), 'danger');
        },
        complete: function() {
            submitBtn.html(originalText).prop('disabled', false);
        }
    });
}

// 處理姓名算命
function handleNameFortune(e) {
    e.preventDefault();
    
    const nameData = {
        userId: currentUser.userId,
        fullName: $('#fullName').val().trim()
    };
    
    if (!nameData.fullName) {
        showAlert('請輸入您的姓名', 'danger');
        return;
    }
    
    const submitBtn = $('#nameFortuneForm button[type="submit"]');
    const originalText = submitBtn.html();
    submitBtn.html('<span class="loading"></span> 算命中...').prop('disabled', true);
    
    console.log('發送姓名數據:', nameData);
    
    $.ajax({
        url: `${APP_CONFIG.API_BASE_URL}/fortune/name`,
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(nameData),
        success: function(response) {
            console.log('姓名響應:', response);
            if (response.success) {
                displayNameFortuneResult(response);
                showAlert('姓名算命完成！', 'success');
            } else {
                showAlert(response.message || '算命失敗', 'danger');
            }
        },
        error: function(xhr, status, error) {
            console.error('姓名算命錯誤:', error, xhr.responseText);
            showAlert('算命失敗：' + (xhr.responseJSON?.message || error), 'danger');
        },
        complete: function() {
            submitBtn.html(originalText).prop('disabled', false);
        }
    });
}

// 處理每日運勢
function handleDailyFortune(e) {
    e.preventDefault();
    
    const zodiac = $('#zodiacSelect').val();
    
    if (!zodiac) {
        showAlert('請選擇您的生肖', 'danger');
        return;
    }
    
    const submitBtn = $('#dailyFortuneForm button[type="submit"]');
    const originalText = submitBtn.html();
    submitBtn.html('<span class="loading"></span> 查詢中...').prop('disabled', true);
    
    console.log('查詢每日運勢 - 用戶ID:', currentUser.userId, '生肖:', zodiac);
    console.log('請求URL:', `${APP_CONFIG.API_BASE_URL}/fortune/daily/${currentUser.userId}/${zodiac}`);
    
    $.ajax({
        url: `${APP_CONFIG.API_BASE_URL}/fortune/daily/${currentUser.userId}/${zodiac}`,
        method: 'GET',
        success: function(response) {
            console.log('每日運勢響應:', response);
            if (response.success) {
                displayDailyFortuneResult(response);
                showAlert('每日運勢查詢完成！', 'success');
            } else {
                showAlert(response.message || '查詢失敗', 'danger');
            }
        },
        error: function(xhr, status, error) {
            console.error('每日運勢錯誤:', error, xhr.responseText);
            showAlert('查詢失敗：' + (xhr.responseJSON?.message || error), 'danger');
        },
        complete: function() {
            submitBtn.html(originalText).prop('disabled', false);
        }
    });
}

// 顯示八字算命結果
function displayBaziResult(data) {
    const resultHtml = `
        <div class="result-card">
            <div class="result-title">
                <i class="fas fa-yin-yang me-2"></i>生辰八字算命結果
            </div>
            <div class="result-content">
                <div class="row mb-3">
                    <div class="col-md-6">
                        <h6>基本信息</h6>
                        <p><strong>出生時間：</strong>${data.birthTime}</p>
                        <p><strong>出生地點：</strong>${data.birthPlace}</p>
                        <p><strong>生肖：</strong>${data.zodiac}</p>
                    </div>
                    <div class="col-md-6">
                        <h6>四柱八字</h6>
                        <p><strong>年柱：</strong>${data.yearPillar}</p>
                        <p><strong>月柱：</strong>${data.monthPillar}</p>
                        <p><strong>日柱：</strong>${data.dayPillar}</p>
                        <p><strong>時柱：</strong>${data.hourPillar}</p>
                    </div>
                </div>
                <div class="score-display">${data.score}分</div>
                <div class="mt-3">
                    <h6>命理分析</h6>
                    <p style="white-space: pre-line;">${data.analysis}</p>
                </div>
            </div>
        </div>
    `;
    
    $('#baziResult').html(resultHtml).show();
    $('html, body').animate({
        scrollTop: $('#baziResult').offset().top - 100
    }, 800);
}

// 顯示姓名算命結果  
function displayNameFortuneResult(data) {
    const resultHtml = `
        <div class="result-card">
            <div class="result-title">
                <i class="fas fa-signature me-2"></i>姓名算命結果
            </div>
            <div class="result-content">
                <div class="row mb-3">
                    <div class="col-md-6">
                        <h6>基本信息</h6>
                        <p><strong>姓名：</strong>${data.name}</p>
                        <p><strong>總筆劃：</strong>${data.totalStrokes}畫</p>
                        <p><strong>五行：</strong>${data.element}</p>
                    </div>
                    <div class="col-md-6">
                        <div class="score-display">${data.score}分</div>
                    </div>
                </div>
                <div class="mt-3">
                    <h6>姓名分析</h6>
                    <p style="white-space: pre-line;">${data.analysis}</p>
                </div>
            </div>
        </div>
    `;
    
    $('#nameFortuneResult').html(resultHtml).show();
    $('html, body').animate({
        scrollTop: $('#nameFortuneResult').offset().top - 100
    }, 800);
}

// 顯示每日運勢結果
function displayDailyFortuneResult(data) {
    const resultHtml = `
        <div class="result-card">
            <div class="result-title">
                <i class="fas fa-calendar-day me-2"></i>每日運勢 - ${data.zodiac}
            </div>
            <div class="result-content">
                <div class="row mb-3">
                    <div class="col-md-6">
                        <h6>今日運勢 (${data.date})</h6>
                        <p><strong>整體運勢：</strong><span class="badge bg-primary">${data.overallLuck}</span></p>
                    </div>
                    <div class="col-md-6">
                        <h6>各項運勢評分</h6>
                        <div class="mb-2">
                            <span>愛情運勢</span>
                            <div class="score-bar">
                                <div class="score-fill ${getScoreClass(data.loveScore)}" style="width: ${data.loveScore}%"></div>
                            </div>
                            <small>${data.loveScore}分</small>
                        </div>
                        <div class="mb-2">
                            <span>事業運勢</span>
                            <div class="score-bar">
                                <div class="score-fill ${getScoreClass(data.careerScore)}" style="width: ${data.careerScore}%"></div>
                            </div>
                            <small>${data.careerScore}分</small>
                        </div>
                        <div class="mb-2">
                            <span>財運</span>
                            <div class="score-bar">
                                <div class="score-fill ${getScoreClass(data.wealthScore)}" style="width: ${data.wealthScore}%"></div>
                            </div>
                            <small>${data.wealthScore}分</small>
                        </div>
                        <div class="mb-2">
                            <span>健康運勢</span>  
                            <div class="score-bar">
                                <div class="score-fill ${getScoreClass(data.healthScore)}" style="width: ${data.healthScore}%"></div>
                            </div>
                            <small>${data.healthScore}分</small>
                        </div>
                    </div>
                </div>
                <div class="mt-3">
                    <h6>今日建議</h6>
                    <p style="white-space: pre-line;">${data.suggestion}</p>
                </div>
            </div>
        </div>
    `;
    
    $('#dailyFortuneResult').html(resultHtml).show();
    $('html, body').animate({
        scrollTop: $('#dailyFortuneResult').offset().top - 100
    }, 800);
}

// 獲取分數對應的樣式類
function getScoreClass(score) {
    if (score >= 80) return 'excellent';
    if (score >= 60) return 'good';
    return 'average';
}

// 登出功能
function logout() {
    localStorage.removeItem(APP_CONFIG.USER_KEY);
    updateUIForLoggedOutUser();
    showAlert('已安全登出', 'info');
    showHome();
}

// 顯示提示訊息
function showAlert(message, type = 'info') {
    const alertHtml = `
        <div class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    
    // 移除舊的提示
    $('.alert').remove();
    
    // 添加新提示到頁面頂部
    $('main').prepend(alertHtml);
    
    // 3秒後自動隱藏
    setTimeout(() => {
        $('.alert').fadeOut(() => {
            $('.alert').remove();
        });
    }, 3000);
}

// 實用工具函數
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('zh-TW');
}

function formatDateTime(dateString) {
    const date = new Date(dateString);
    return date.toLocaleString('zh-TW');
}

// 錯誤處理
window.addEventListener('error', function(e) {
    console.error('頁面錯誤:', e.error);
});

// 網絡連接檢查
function checkNetworkConnection() {
    return navigator.onLine;
}

// 調試模式
const DEBUG_MODE = true;

function debugLog(message, data = null) {
    if (DEBUG_MODE) {
        console.log(`[DEBUG] ${message}`, data);
    }
} 