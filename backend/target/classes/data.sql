-- 初始化用戶數據
INSERT INTO employees (username, password, full_name, email, department, security_level, active)
VALUES ('admin', '$2a$10$lTONZw9Ug.0yQD3zEPJkG.f85z7.n1yk2UXLuS0KOwbfvyeRRYdZW', '系統管理員', 'admin@example.com', '管理部門', 'LEVEL_1', true);

INSERT INTO employees (username, password, full_name, email, department, security_level, active)
VALUES ('user', '$2a$10$lTONZw9Ug.0yQD3zEPJkG.f85z7.n1yk2UXLuS0KOwbfvyeRRYdZW', '普通用戶', 'user@example.com', '業務部門', 'LEVEL_4', true);

-- 初始化測試事件數據
INSERT INTO events (title, description, start_time, end_time, location, created_by)
VALUES ('團隊會議', '討論下一季度計劃', '2025-03-25 10:00:00', '2025-03-25 11:30:00', '大會議室', 1); 