-- =============================================
-- 算命網站 Oracle 資料庫結構腳本
-- 創建時間: 2024
-- 說明: 用於面試展示的算命網站資料庫結構
-- =============================================

-- 此腳本將在 gvenzl/oracle-xe 容器中自動執行
-- 用戶 fatecompass 已由 Docker 環境變數自動創建
-- 無需手動創建用戶和連接

-- =============================================
-- 1. 創建序列 (Sequences)
-- =============================================

-- 用戶ID序列
CREATE SEQUENCE FC_USER_SEQ
    START WITH 1
    INCREMENT BY 1
    MAXVALUE 999999999
    NOCACHE
    NOCYCLE;

-- 算命歷史ID序列
CREATE SEQUENCE FC_FORTUNE_SEQ
    START WITH 1
    INCREMENT BY 1
    MAXVALUE 999999999
    NOCACHE
    NOCYCLE;

-- =============================================
-- 2. 創建表結構 (Tables)
-- =============================================

-- 用戶表
CREATE TABLE FC_USERS (
    USER_ID       NUMBER(10)     NOT NULL,
    USERNAME      VARCHAR2(50)   NOT NULL,
    PASSWORD      VARCHAR2(255)  NOT NULL,
    EMAIL         VARCHAR2(100),
    REAL_NAME     VARCHAR2(50),
    PHONE         VARCHAR2(20),
    GENDER        VARCHAR2(10),
    BIRTH_DATE    TIMESTAMP,
    BIRTH_PLACE   VARCHAR2(100),
    IS_ACTIVE     NUMBER(1)      DEFAULT 1,
    CREATED_AT    TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT    TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT PK_FC_USERS PRIMARY KEY (USER_ID),
    CONSTRAINT UK_FC_USERS_USERNAME UNIQUE (USERNAME),
    CONSTRAINT UK_FC_USERS_EMAIL UNIQUE (EMAIL),
    CONSTRAINT CK_FC_USERS_GENDER CHECK (GENDER IN ('MALE', 'FEMALE')),
    CONSTRAINT CK_FC_USERS_ACTIVE CHECK (IS_ACTIVE IN (0, 1))
);

-- 算命歷史記錄表
CREATE TABLE FC_FORTUNE_HISTORY (
    HISTORY_ID    NUMBER(10)     NOT NULL,
    USER_ID       NUMBER(10)     NOT NULL,
    FORTUNE_TYPE  VARCHAR2(20)   NOT NULL,
    INPUT_DATA    VARCHAR2(1000),
    RESULT_DATA   CLOB,
    SCORE         NUMBER(3),
    ANALYSIS      CLOB,
    CREATED_AT    TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT PK_FC_FORTUNE_HISTORY PRIMARY KEY (HISTORY_ID),
    CONSTRAINT FK_FC_FORTUNE_USER FOREIGN KEY (USER_ID) REFERENCES FC_USERS (USER_ID),
    CONSTRAINT CK_FC_FORTUNE_TYPE CHECK (FORTUNE_TYPE IN ('BAZI', 'NAME', 'DAILY', 'ZODIAC')),
    CONSTRAINT CK_FC_FORTUNE_SCORE CHECK (SCORE BETWEEN 0 AND 100)
);

-- =============================================
-- 3. 創建索引 (Indexes)
-- =============================================

-- 用戶表索引
CREATE INDEX IDX_FC_USERS_USERNAME ON FC_USERS (USERNAME);
CREATE INDEX IDX_FC_USERS_EMAIL ON FC_USERS (EMAIL);
CREATE INDEX IDX_FC_USERS_CREATED ON FC_USERS (CREATED_AT);
CREATE INDEX IDX_FC_USERS_ACTIVE ON FC_USERS (IS_ACTIVE);

-- 算命歷史表索引
CREATE INDEX IDX_FC_FORTUNE_USER ON FC_FORTUNE_HISTORY (USER_ID);
CREATE INDEX IDX_FC_FORTUNE_TYPE ON FC_FORTUNE_HISTORY (FORTUNE_TYPE);
CREATE INDEX IDX_FC_FORTUNE_CREATED ON FC_FORTUNE_HISTORY (CREATED_AT);
CREATE INDEX IDX_FC_FORTUNE_USER_TYPE ON FC_FORTUNE_HISTORY (USER_ID, FORTUNE_TYPE);
CREATE INDEX IDX_FC_FORTUNE_USER_CREATED ON FC_FORTUNE_HISTORY (USER_ID, CREATED_AT);

-- =============================================
-- 4. 創建觸發器 (Triggers)
-- =============================================

-- 用戶表自動更新時間觸發器
CREATE OR REPLACE TRIGGER TRG_FC_USERS_UPDATE
    BEFORE UPDATE ON FC_USERS
    FOR EACH ROW
BEGIN
    :NEW.UPDATED_AT := CURRENT_TIMESTAMP;
END;
/

-- 用戶表自動ID觸發器
CREATE OR REPLACE TRIGGER TRG_FC_USERS_ID
    BEFORE INSERT ON FC_USERS
    FOR EACH ROW
BEGIN
    IF :NEW.USER_ID IS NULL THEN
        SELECT FC_USER_SEQ.NEXTVAL INTO :NEW.USER_ID FROM DUAL;
    END IF;
END;
/

-- 算命歷史表自動ID觸發器
CREATE OR REPLACE TRIGGER TRG_FC_FORTUNE_ID
    BEFORE INSERT ON FC_FORTUNE_HISTORY
    FOR EACH ROW
BEGIN
    IF :NEW.HISTORY_ID IS NULL THEN
        SELECT FC_FORTUNE_SEQ.NEXTVAL INTO :NEW.HISTORY_ID FROM DUAL;
    END IF;
END;
/

-- =============================================
-- 5. 創建視圖 (Views)
-- =============================================

-- 用戶統計視圖
CREATE OR REPLACE VIEW VW_USER_STATS AS
SELECT 
    u.USER_ID,
    u.USERNAME,
    u.EMAIL,
    u.REAL_NAME,
    u.CREATED_AT,
    COUNT(fh.HISTORY_ID) AS TOTAL_FORTUNE_COUNT,
    MAX(fh.CREATED_AT) AS LAST_FORTUNE_DATE,
    ROUND(AVG(fh.SCORE), 2) AS AVG_SCORE
FROM FC_USERS u
LEFT JOIN FC_FORTUNE_HISTORY fh ON u.USER_ID = fh.USER_ID
WHERE u.IS_ACTIVE = 1
GROUP BY u.USER_ID, u.USERNAME, u.EMAIL, u.REAL_NAME, u.CREATED_AT;

-- 算命類型統計視圖
CREATE OR REPLACE VIEW VW_FORTUNE_TYPE_STATS AS
SELECT 
    FORTUNE_TYPE,
    COUNT(*) AS TOTAL_COUNT,
    ROUND(AVG(SCORE), 2) AS AVG_SCORE,
    MIN(CREATED_AT) AS FIRST_CREATED,
    MAX(CREATED_AT) AS LAST_CREATED
FROM FC_FORTUNE_HISTORY
GROUP BY FORTUNE_TYPE;

-- 每日算命統計視圖
CREATE OR REPLACE VIEW VW_DAILY_FORTUNE_STATS AS
SELECT 
    TRUNC(CREATED_AT) AS FORTUNE_DATE,
    COUNT(*) AS DAILY_COUNT,
    COUNT(DISTINCT USER_ID) AS UNIQUE_USERS,
    ROUND(AVG(SCORE), 2) AS AVG_SCORE
FROM FC_FORTUNE_HISTORY
WHERE CREATED_AT >= TRUNC(SYSDATE) - 30  -- 最近30天
GROUP BY TRUNC(CREATED_AT)
ORDER BY FORTUNE_DATE DESC;

-- =============================================
-- 6. 創建存儲過程 (Stored Procedures)
-- =============================================

-- 獲取用戶算命歷史的存儲過程
CREATE OR REPLACE PROCEDURE SP_GET_USER_FORTUNE_HISTORY(
    p_user_id IN NUMBER,
    p_limit IN NUMBER DEFAULT 10,
    p_cursor OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN p_cursor FOR
        SELECT 
            HISTORY_ID,
            FORTUNE_TYPE,
            INPUT_DATA,
            RESULT_DATA,
            SCORE,
            CREATED_AT
        FROM FC_FORTUNE_HISTORY
        WHERE USER_ID = p_user_id
        ORDER BY CREATED_AT DESC
        FETCH FIRST p_limit ROWS ONLY;
END SP_GET_USER_FORTUNE_HISTORY;
/

-- 清理舊數據的存儲過程
CREATE OR REPLACE PROCEDURE SP_CLEANUP_OLD_DATA(
    p_days_to_keep IN NUMBER DEFAULT 365
) AS
    v_count NUMBER;
BEGIN
    -- 刪除超過指定天數的算命記錄
    DELETE FROM FC_FORTUNE_HISTORY 
    WHERE CREATED_AT < SYSDATE - p_days_to_keep;
    
    v_count := SQL%ROWCOUNT;
    
    DBMS_OUTPUT.PUT_LINE('清理了 ' || v_count || ' 條舊的算命記錄');
    
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END SP_CLEANUP_OLD_DATA;
/

-- =============================================
-- 7. 創建函數 (Functions)
-- =============================================

-- 計算用戶等級的函數
CREATE OR REPLACE FUNCTION FN_GET_USER_LEVEL(
    p_user_id IN NUMBER
) RETURN VARCHAR2 AS
    v_count NUMBER;
    v_level VARCHAR2(20);
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM FC_FORTUNE_HISTORY
    WHERE USER_ID = p_user_id;
    
    CASE 
        WHEN v_count >= 100 THEN v_level := 'VIP';
        WHEN v_count >= 50 THEN v_level := '高級用戶';
        WHEN v_count >= 20 THEN v_level := '中級用戶';
        WHEN v_count >= 5 THEN v_level := '初級用戶';
        ELSE v_level := '新手';
    END CASE;
    
    RETURN v_level;
END FN_GET_USER_LEVEL;
/

-- =============================================
-- 8. 權限設置
-- =============================================

-- 如果有其他用戶需要訪問，可以授權
-- GRANT SELECT ON FC_USERS TO read_only_user;
-- GRANT SELECT ON FC_FORTUNE_HISTORY TO read_only_user;
-- GRANT SELECT ON VW_USER_STATS TO read_only_user;

-- =============================================
-- 9. 註釋說明
-- =============================================

COMMENT ON TABLE FC_USERS IS '用戶基本信息表';
COMMENT ON COLUMN FC_USERS.USER_ID IS '用戶唯一標識';
COMMENT ON COLUMN FC_USERS.USERNAME IS '用戶名';
COMMENT ON COLUMN FC_USERS.PASSWORD IS '加密後的密碼';
COMMENT ON COLUMN FC_USERS.EMAIL IS '電子郵件地址';
COMMENT ON COLUMN FC_USERS.REAL_NAME IS '真實姓名';
COMMENT ON COLUMN FC_USERS.PHONE IS '手機號碼';
COMMENT ON COLUMN FC_USERS.GENDER IS '性別：MALE/FEMALE';
COMMENT ON COLUMN FC_USERS.BIRTH_DATE IS '出生日期時間';
COMMENT ON COLUMN FC_USERS.BIRTH_PLACE IS '出生地點';
COMMENT ON COLUMN FC_USERS.IS_ACTIVE IS '是否啟用：1-啟用，0-禁用';

COMMENT ON TABLE FC_FORTUNE_HISTORY IS '算命歷史記錄表';
COMMENT ON COLUMN FC_FORTUNE_HISTORY.HISTORY_ID IS '記錄唯一標識';
COMMENT ON COLUMN FC_FORTUNE_HISTORY.USER_ID IS '用戶ID';
COMMENT ON COLUMN FC_FORTUNE_HISTORY.FORTUNE_TYPE IS '算命類型：BAZI-八字，NAME-姓名，DAILY-每日，ZODIAC-生肖';
COMMENT ON COLUMN FC_FORTUNE_HISTORY.INPUT_DATA IS '輸入數據';
COMMENT ON COLUMN FC_FORTUNE_HISTORY.RESULT_DATA IS '算命結果詳細數據';
COMMENT ON COLUMN FC_FORTUNE_HISTORY.SCORE IS '算命評分(0-100)';
COMMENT ON COLUMN FC_FORTUNE_HISTORY.ANALYSIS IS '算命分析結果';

-- =============================================
-- 腳本執行完成
-- =============================================

PROMPT '==========================================';
PROMPT '算命網站資料庫結構創建完成！';
PROMPT '已創建的對象：';
PROMPT '- 2個序列 (FC_USER_SEQ, FC_FORTUNE_SEQ)';
PROMPT '- 2個表 (FC_USERS, FC_FORTUNE_HISTORY)';
PROMPT '- 8個索引';
PROMPT '- 3個觸發器';
PROMPT '- 3個視圖';
PROMPT '- 2個存儲過程';
PROMPT '- 1個函數';
PROMPT '==========================================';

-- 提交事務
COMMIT; 