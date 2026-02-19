-- 第五阶段新增接口权限配置
-- 添加用户已领养宠物列表和信用摘要相关权限

-- 1. 添加权限记录
INSERT INTO `sys_permission` (`perm_code`, `perm_name`, `perm_type`, `http_method`, `api_path`, `parent_id`, `sort`, `enabled`, `remark`, `create_time`, `update_time`) VALUES
('user:adopted:pets', '获取已领养宠物列表', 'API', 'GET', '/api/user/pets/adopted', 0, 1, 1, '获取当前用户已领养成功的宠物列表', NOW(), NOW()),
('user:credit:summary', '获取信用摘要', 'API', 'GET', '/api/user/credit/summary', 0, 2, 1, '获取当前用户的信用摘要信息', NOW(), NOW());

-- 2. 为USER角色添加权限（默认所有用户都有这些权限）
-- 假设USER角色的ID为2（需要根据实际数据调整）
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`, `create_time`, `update_time`) VALUES
(2, (SELECT id FROM sys_permission WHERE perm_code = 'user:adopted:pets'), NOW(), NOW()),
(2, (SELECT id FROM sys_permission WHERE perm_code = 'user:credit:summary'), NOW(), NOW());