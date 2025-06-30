-- =============================================
-- Oracle 用戶設置腳本
-- 說明: 以system用戶身份執行此腳本
-- =============================================

-- 創建用戶
CREATE USER fatecompass IDENTIFIED BY fatecompass123;

-- 授予權限
GRANT CONNECT, RESOURCE TO fatecompass;
GRANT CREATE VIEW, CREATE SEQUENCE TO fatecompass;
GRANT UNLIMITED TABLESPACE TO fatecompass;

-- 允許從任何IP連接（開發環境）
GRANT CREATE SESSION TO fatecompass;

-- 顯示用戶創建成功
SELECT 'User fatecompass created successfully!' FROM dual;

-- 連接資訊
SELECT 'Connection String: jdbc:oracle:thin:@localhost:1521:XE' FROM dual;
SELECT 'Username: fatecompass' FROM dual;
SELECT 'Password: fatecompass123' FROM dual; 