-- =============================================
-- 算命網站 Oracle 測試數據腳本
-- 創建時間: 2024
-- 說明: 用於面試展示的測試數據
-- =============================================

-- 清空現有數據 (謹慎執行)
-- DELETE FROM FC_FORTUNE_HISTORY;
-- DELETE FROM FC_USERS;
-- COMMIT;

-- =============================================
-- 1. 插入測試用戶數據
-- =============================================

-- 管理員用戶
INSERT INTO FC_USERS (USERNAME, PASSWORD, EMAIL, REAL_NAME, PHONE, GENDER, BIRTH_DATE, BIRTH_PLACE, IS_ACTIVE)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKk1hWxs5dQs7WwFynJEKCjX2', 'admin@fatecompass.com', '系統管理員', '0912345678', 'MALE', TIMESTAMP '1990-01-01 08:00:00', '台北市', 1);

-- 測試用戶1
INSERT INTO FC_USERS (USERNAME, PASSWORD, EMAIL, REAL_NAME, PHONE, GENDER, BIRTH_DATE, BIRTH_PLACE, IS_ACTIVE)
VALUES ('testuser1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKk1hWxs5dQs7WwFynJEKCjX2', 'test1@example.com', '測試用戶一', '0987654321', 'FEMALE', TIMESTAMP '1985-06-15 14:30:00', '台中市', 1);

-- 測試用戶2
INSERT INTO FC_USERS (USERNAME, PASSWORD, EMAIL, REAL_NAME, PHONE, GENDER, BIRTH_DATE, BIRTH_PLACE, IS_ACTIVE)
VALUES ('testuser2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKk1hWxs5dQs7WwFynJEKCjX2', 'test2@example.com', '測試用戶二', '0923456789', 'MALE', TIMESTAMP '1992-03-20 10:15:00', '高雄市', 1);

-- 測試用戶3
INSERT INTO FC_USERS (USERNAME, PASSWORD, EMAIL, REAL_NAME, PHONE, GENDER, BIRTH_DATE, BIRTH_PLACE, IS_ACTIVE)
VALUES ('testuser3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKk1hWxs5dQs7WwFynJEKCjX2', 'test3@example.com', '測試用戶三', '0934567890', 'FEMALE', TIMESTAMP '1988-11-08 16:45:00', '新竹市', 1);

-- Demo用戶 (用於演示)
INSERT INTO FC_USERS (USERNAME, PASSWORD, EMAIL, REAL_NAME, PHONE, GENDER, BIRTH_DATE, BIRTH_PLACE, IS_ACTIVE)
VALUES ('demo', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKk1hWxs5dQs7WwFynJEKCjX2', 'demo@fatecompass.com', 'Demo用戶', '0945678901', 'MALE', TIMESTAMP '1995-09-12 12:00:00', '桃園市', 1);

-- =============================================
-- 2. 插入算命歷史測試數據
-- =============================================

-- 為測試用戶1插入生辰八字算命記錄
INSERT INTO FC_FORTUNE_HISTORY (USER_ID, FORTUNE_TYPE, INPUT_DATA, RESULT_DATA, SCORE, ANALYSIS)
VALUES (
    (SELECT USER_ID FROM FC_USERS WHERE USERNAME = 'testuser1'),
    'BAZI',
    '1985-06-15|14:30|台中市',
    '{"yearPillar":"乙丑","monthPillar":"壬午","dayPillar":"丁卯","hourPillar":"丁未","zodiac":"牛","elements":{"金":1,"木":2,"水":1,"火":3,"土":1}}',
    85,
    '您的生辰八字為：乙丑 壬午 丁卯 丁未
生肖：牛

五行分析：
金：1 木：2 水：1 火：3 土：1 

性格特點：根據您的八字分析，您是一個聰明智慧的人。
事業運勢：工作穩定，有升遷機會。
感情運勢：感情和諧美滿。
健康運勢：身體健康，注意休息。'
);

-- 為測試用戶1插入姓名算命記錄
INSERT INTO FC_FORTUNE_HISTORY (USER_ID, FORTUNE_TYPE, INPUT_DATA, RESULT_DATA, SCORE, ANALYSIS)
VALUES (
    (SELECT USER_ID FROM FC_USERS WHERE USERNAME = 'testuser1'),
    'NAME',
    '王小明',
    '{"name":"王小明","totalStrokes":12,"element":"火","score":78}',
    78,
    '姓名：王小明
總筆劃：12畫
主要五行：火

姓名寓意：您的姓名數理暗示富貴榮華。
性格影響：性格溫和，富有同情心。
運勢影響：運勢佳，前景光明。'
);

-- 為測試用戶1插入每日運勢記錄
INSERT INTO FC_FORTUNE_HISTORY (USER_ID, FORTUNE_TYPE, INPUT_DATA, RESULT_DATA, SCORE, ANALYSIS)
VALUES (
    (SELECT USER_ID FROM FC_USERS WHERE USERNAME = 'testuser1'),
    'DAILY',
    '牛|2024-01-15',
    '{"zodiac":"牛","date":"2024-01-15","overallLuck":"中吉","loveScore":75,"careerScore":82,"wealthScore":68,"healthScore":71}',
    74,
    '今日運勢：中吉

建議：
• 感情方面需要多溝通理解
• 財運一般，避免大額投資
• 注意身體健康，多休息
• 保持積極樂觀的心態'
);

-- 為測試用戶2插入多條記錄
INSERT INTO FC_FORTUNE_HISTORY (USER_ID, FORTUNE_TYPE, INPUT_DATA, RESULT_DATA, SCORE, ANALYSIS)
VALUES (
    (SELECT USER_ID FROM FC_USERS WHERE USERNAME = 'testuser2'),
    'BAZI',
    '1992-03-20|10:15|高雄市',
    '{"yearPillar":"壬申","monthPillar":"癸卯","dayPillar":"己亥","hourPillar":"己巳","zodiac":"猴","elements":{"金":1,"木":1,"水":2,"火":1,"土":3}}',
    92,
    '您的生辰八字為：壬申 癸卯 己亥 己巳
生肖：猴

五行分析：
金：1 木：1 水：2 火：1 土：3 

性格特點：根據您的八字分析，您是一個聰明智慧的人。
事業運勢：事業運勢良好，適合發展。
感情運勢：桃花運旺盛。
健康運勢：適合運動養生。'
);

INSERT INTO FC_FORTUNE_HISTORY (USER_ID, FORTUNE_TYPE, INPUT_DATA, RESULT_DATA, SCORE, ANALYSIS)
VALUES (
    (SELECT USER_ID FROM FC_USERS WHERE USERNAME = 'testuser2'),
    'NAME',
    '李志強',
    '{"name":"李志強","totalStrokes":18,"element":"木","score":88}',
    88,
    '姓名：李志強
總筆劃：18畫
主要五行：木

姓名寓意：您的姓名數理暗示聰明才智。
性格影響：性格溫和，富有同情心。
運勢影響：前景光明。'
);

-- 為測試用戶3插入記錄
INSERT INTO FC_FORTUNE_HISTORY (USER_ID, FORTUNE_TYPE, INPUT_DATA, RESULT_DATA, SCORE, ANALYSIS)
VALUES (
    (SELECT USER_ID FROM FC_USERS WHERE USERNAME = 'testuser3'),
    'DAILY',
    '龍|2024-01-15',
    '{"zodiac":"龍","date":"2024-01-15","overallLuck":"大吉","loveScore":85,"careerScore":90,"wealthScore":88,"healthScore":82}',
    86,
    '今日運勢：大吉

建議：
• 保持積極樂觀的心態
• 今日適合進行重要決策
• 財運亨通，投資有利
• 身體狀況良好'
);

-- 為Demo用戶插入多種類型記錄
INSERT INTO FC_FORTUNE_HISTORY (USER_ID, FORTUNE_TYPE, INPUT_DATA, RESULT_DATA, SCORE, ANALYSIS)
VALUES (
    (SELECT USER_ID FROM FC_USERS WHERE USERNAME = 'demo'),
    'BAZI',
    '1995-09-12|12:00|桃園市',
    '{"yearPillar":"乙亥","monthPillar":"乙酉","dayPillar":"戊戌","hourPillar":"戊午","zodiac":"豬","elements":{"金":1,"木":2,"水":1,"火":1,"土":3}}',
    79,
    '您的生辰八字為：乙亥 乙酉 戊戌 戊午
生肖：豬

五行分析：
金：1 木：2 水：1 火：1 土：3 

性格特點：根據您的八字分析，您是一個勤勞踏實的人。
事業運勢：需要努力拼搏。
感情運勢：需要經營感情。
健康運勢：保持良好作息。'
);

INSERT INTO FC_FORTUNE_HISTORY (USER_ID, FORTUNE_TYPE, INPUT_DATA, RESULT_DATA, SCORE, ANALYSIS)
VALUES (
    (SELECT USER_ID FROM FC_USERS WHERE USERNAME = 'demo'),
    'NAME',
    '陳美華',
    '{"name":"陳美華","totalStrokes":25,"element":"水","score":82}',
    82,
    '姓名：陳美華
總筆劃：25畫
主要五行：水

姓名寓意：您的姓名數理暗示平安健康。
性格影響：性格靈活，適應力強。
運勢影響：小有起伏。'
);

-- 添加一些歷史數據 (不同日期)
INSERT INTO FC_FORTUNE_HISTORY (USER_ID, FORTUNE_TYPE, INPUT_DATA, RESULT_DATA, SCORE, ANALYSIS, CREATED_AT)
VALUES (
    (SELECT USER_ID FROM FC_USERS WHERE USERNAME = 'testuser1'),
    'DAILY',
    '牛|2024-01-10',
    '{"zodiac":"牛","date":"2024-01-10","overallLuck":"小吉","loveScore":65,"careerScore":70,"wealthScore":60,"healthScore":68}',
    66,
    '今日運勢：小吉

建議：
• 感情方面需要多溝通理解
• 工作上宜謹慎行事
• 財運一般，避免大額投資
• 保持積極樂觀的心態',
    TIMESTAMP '2024-01-10 09:30:00'
);

INSERT INTO FC_FORTUNE_HISTORY (USER_ID, FORTUNE_TYPE, INPUT_DATA, RESULT_DATA, SCORE, ANALYSIS, CREATED_AT)
VALUES (
    (SELECT USER_ID FROM FC_USERS WHERE USERNAME = 'testuser2'),
    'DAILY',
    '猴|2024-01-12',
    '{"zodiac":"猴","date":"2024-01-12","overallLuck":"中吉","loveScore":78,"careerScore":85,"wealthScore":72,"healthScore":76}',
    78,
    '今日運勢：中吉

建議：
• 事業運勢良好，適合發展
• 愛情甜蜜，單身者有機會
• 投資理財需謹慎
• 注意勞逸結合',
    TIMESTAMP '2024-01-12 14:20:00'
);

-- =============================================
-- 3. 更新序列到正確的值
-- =============================================

-- 重置序列以避免主鍵衝突
DECLARE
    v_max_user_id NUMBER;
    v_max_history_id NUMBER;
BEGIN
    -- 獲取當前最大ID
    SELECT NVL(MAX(USER_ID), 0) INTO v_max_user_id FROM FC_USERS;
    SELECT NVL(MAX(HISTORY_ID), 0) INTO v_max_history_id FROM FC_FORTUNE_HISTORY;
    
    -- 調整序列
    EXECUTE IMMEDIATE 'ALTER SEQUENCE FC_USER_SEQ INCREMENT BY ' || (v_max_user_id + 1 - FC_USER_SEQ.NEXTVAL);
    SELECT FC_USER_SEQ.NEXTVAL INTO v_max_user_id FROM DUAL;
    EXECUTE IMMEDIATE 'ALTER SEQUENCE FC_USER_SEQ INCREMENT BY 1';
    
    EXECUTE IMMEDIATE 'ALTER SEQUENCE FC_FORTUNE_SEQ INCREMENT BY ' || (v_max_history_id + 1 - FC_FORTUNE_SEQ.NEXTVAL);
    SELECT FC_FORTUNE_SEQ.NEXTVAL INTO v_max_history_id FROM DUAL;
    EXECUTE IMMEDIATE 'ALTER SEQUENCE FC_FORTUNE_SEQ INCREMENT BY 1';
END;
/

-- =============================================
-- 4. 驗證數據
-- =============================================

-- 查看插入的用戶數據
SELECT '用戶數據驗證:' AS INFO FROM DUAL;
SELECT USERNAME, EMAIL, REAL_NAME, GENDER, TO_CHAR(CREATED_AT, 'YYYY-MM-DD') AS CREATED_DATE
FROM FC_USERS
ORDER BY CREATED_AT;

-- 查看插入的算命記錄
SELECT '算命記錄驗證:' AS INFO FROM DUAL;
SELECT 
    u.USERNAME,
    fh.FORTUNE_TYPE,
    fh.SCORE,
    TO_CHAR(fh.CREATED_AT, 'YYYY-MM-DD HH24:MI') AS CREATED_TIME
FROM FC_FORTUNE_HISTORY fh
JOIN FC_USERS u ON fh.USER_ID = u.USER_ID
ORDER BY fh.CREATED_AT DESC;

-- 統計信息
SELECT '統計信息:' AS INFO FROM DUAL;
SELECT 
    '總用戶數' AS METRIC,
    COUNT(*) AS VALUE
FROM FC_USERS
UNION ALL
SELECT 
    '總算命記錄數' AS METRIC,
    COUNT(*) AS VALUE
FROM FC_FORTUNE_HISTORY
UNION ALL
SELECT 
    '平均算命評分' AS METRIC,
    ROUND(AVG(SCORE), 2) AS VALUE
FROM FC_FORTUNE_HISTORY;

-- =============================================
-- 5. 測試查詢
-- =============================================

-- 測試用戶統計視圖
SELECT '用戶統計視圖測試:' AS INFO FROM DUAL;
SELECT * FROM VW_USER_STATS;

-- 測試算命類型統計視圖
SELECT '算命類型統計視圖測試:' AS INFO FROM DUAL;
SELECT * FROM VW_FORTUNE_TYPE_STATS;

-- 測試用戶等級函數
SELECT '用戶等級函數測試:' AS INFO FROM DUAL;
SELECT 
    u.USERNAME,
    FN_GET_USER_LEVEL(u.USER_ID) AS USER_LEVEL
FROM FC_USERS u;

-- =============================================
-- 腳本執行完成
-- =============================================

PROMPT '==========================================';
PROMPT '算命網站測試數據插入完成！';
PROMPT '已插入數據：';
PROMPT '- 5個測試用戶 (包含admin和demo)';
PROMPT '- 10條算命歷史記錄';
PROMPT '- 包含各種算命類型的示例數據';
PROMPT '==========================================';
PROMPT '默認密碼: 123456 (BCrypt加密)';
PROMPT '測試用戶: admin, demo, testuser1-3';
PROMPT '==========================================';

-- 提交事務
COMMIT; 