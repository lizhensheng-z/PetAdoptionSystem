-- ============================================================
-- 宠物管理相关权限配置
-- ============================================================
-- 使用说明：在 petAdoptionSystem 数据库中执行此脚本
-- 用于添加宠物创建和管理相关的权限
-- ============================================================

USE petAdoptionSystem;

-- 1. 添加权限到 sys_permission 表
INSERT INTO sys_permission (permission_code, permission_name, description, module, create_time, update_time) VALUES
('pet:create', '创建宠物', '机构用户创建宠物档案', '宠物管理', NOW(), NOW()),
('pet:update', '更新宠物', '机构用户更新宠物信息', '宠物管理', NOW(), NOW()),
('pet:delete', '删除宠物', '机构用户删除宠物档案', '宠物管理', NOW(), NOW()),
('pet:submit_audit', '提交审核', '机构用户提交宠物审核', '宠物管理', NOW(), NOW()),
('pet:view', '查看宠物', '查看宠物详情', '宠物管理', NOW(), NOW()),
('pet:media:upload', '上传媒体', '上传宠物图片和视频', '宠物管理', NOW(), NOW()),
('pet:media:delete', '删除媒体', '删除宠物媒体文件', '宠物管理', NOW(), NOW()),
('org:profile:read', '查看机构资料', '查看机构基本信息', '机构管理', NOW(), NOW())
ON DUPLICATE KEY UPDATE 
    permission_name = VALUES(permission_name),
    description = VALUES(description),
    module = VALUES(module),
    update_time = NOW();

-- 2. 将权限分配给 ORG 角色
-- 获取 ORG 角色ID
SET @org_role_id = (SELECT id FROM sys_role WHERE role_code = 'ORG' LIMIT 1);

-- 获取权限ID
SET @pet_create_id = (SELECT id FROM sys_permission WHERE permission_code = 'pet:create' LIMIT 1);
SET @pet_update_id = (SELECT id FROM sys_permission WHERE permission_code = 'pet:update' LIMIT 1);
SET @pet_delete_id = (SELECT id FROM sys_permission WHERE permission_code = 'pet:delete' LIMIT 1);
SET @pet_submit_audit_id = (SELECT id FROM sys_permission WHERE permission_code = 'pet:submit_audit' LIMIT 1);
SET @pet_view_id = (SELECT id FROM sys_permission WHERE permission_code = 'pet:view' LIMIT 1);
SET @pet_media_upload_id = (SELECT id FROM sys_permission WHERE permission_code = 'pet:media:upload' LIMIT 1);
SET @pet_media_delete_id = (SELECT id FROM sys_permission WHERE permission_code = 'pet:media:delete' LIMIT 1);
SET @org_profile_read_id = (SELECT id FROM sys_permission WHERE permission_code = 'org:profile:read' LIMIT 1);

-- 3. 建立角色权限关联
INSERT IGNORE INTO sys_role_permission (role_id, permission_id) VALUES
(@org_role_id, @pet_create_id),
(@org_role_id, @pet_update_id),
(@org_role_id, @pet_delete_id),
(@org_role_id, @pet_submit_audit_id),
(@org_role_id, @pet_view_id),
(@org_role_id, @pet_media_upload_id),
(@org_role_id, @pet_media_delete_id),
(@org_role_id, @org_profile_read_id);

-- 4. 添加一些常用的宠物标签数据
INSERT INTO tag (name, tag_type, enabled, sort, create_time, update_time) VALUES
-- 性格标签
('亲人', 'PERSONALITY', 1, 1, NOW(), NOW()),
('活泼', 'PERSONALITY', 1, 2, NOW(), NOW()),
('安静', 'PERSONALITY', 1, 3, NOW(), NOW()),
('粘人', 'PERSONALITY', 1, 4, NOW(), NOW()),
('独立', 'PERSONALITY', 1, 5, NOW(), NOW()),
('胆小', 'PERSONALITY', 1, 6, NOW(), NOW()),
('好奇', 'PERSONALITY', 1, 7, NOW(), NOW()),
('温顺', 'PERSONALITY', 1, 8, NOW(), NOW()),

-- 健康标签
('已绝育', 'HEALTH', 1, 1, NOW(), NOW()),
('已疫苗', 'HEALTH', 1, 2, NOW(), NOW()),
('已驱虫', 'HEALTH', 1, 3, NOW(), NOW()),
('健康良好', 'HEALTH', 1, 4, NOW(), NOW()),
('定期体检', 'HEALTH', 1, 5, NOW(), NOW()),

-- 特征标签
('短毛', 'FEATURE', 1, 1, NOW(), NOW()),
('长毛', 'FEATURE', 1, 2, NOW(), NOW()),
('卷毛', 'FEATURE', 1, 3, NOW(), NOW()),
('纯色', 'FEATURE', 1, 4, NOW(), NOW()),
('花色', 'FEATURE', 1, 5, NOW(), NOW()),
('大眼睛', 'FEATURE', 1, 6, NOW(), NOW()),
('小体型', 'FEATURE', 1, 7, NOW(), NOW()),
('大体型', 'FEATURE', 1, 8, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
    tag_type = VALUES(tag_type),
    enabled = VALUES(enabled),
    sort = VALUES(sort),
    update_time = NOW();

-- 5. 验证插入结果
SELECT '权限配置完成' AS message;
SELECT sp.permission_code, sp.permission_name, sr.role_code 
FROM sys_permission sp
JOIN sys_role_permission srp ON sp.id = srp.permission_id
JOIN sys_role sr ON srp.role_id = sr.id
WHERE sp.permission_code LIKE 'pet:%' OR sp.permission_code = 'org:profile:read';