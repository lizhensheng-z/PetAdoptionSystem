-- ============================================================
-- 机构管理接口权限 SQL 脚本
-- 对应 OrgProfileController.java 中的接口权限配置
-- ============================================================

-- 1. 获取机构资料接口权限
-- 接口: GET /api/org/profile
-- 权限注解: @PreAuthorize("hasAuthority('org:profile:read')")
INSERT INTO sys_permission(perm_code, perm_name, perm_type, http_method, api_path, parent_id, sort, enabled, remark) VALUES
('org:profile:read', '机构查看资料', 'API', 'GET', '/api/org/profile', NULL, 25, 1, '机构资料查看');

-- 2. 更新机构资料接口权限
-- 接口: PUT /api/org/profile
-- 权限注解: @PreAuthorize("hasAuthority('org:profile:update')")
INSERT INTO sys_permission(perm_code, perm_name, perm_type, http_method, api_path, parent_id, sort, enabled, remark) VALUES
('org:profile:update', '机构更新资料', 'API', 'PUT', '/api/org/profile', NULL, 26, 1, '机构资料更新');

-- 3. 获取机构统计数据接口权限
-- 接口: GET /api/org/statistics
-- 权限注解: @PreAuthorize("hasAuthority('org:statistics:read')")
INSERT INTO sys_permission(perm_code, perm_name, perm_type, http_method, api_path, parent_id, sort, enabled, remark) VALUES
('org:statistics:read', '机构查看统计', 'API', 'GET', '/api/org/statistics', NULL, 27, 1, '机构统计查看');

-- 4. 获取领养完成记录接口权限
-- 接口: GET /api/org/adoptions
-- 权限注解: @PreAuthorize("hasAuthority('org:adoptions:read')")
INSERT INTO sys_permission(perm_code, perm_name, perm_type, http_method, api_path, parent_id, sort, enabled, remark) VALUES
('org:adoptions:read', '机构查看领养记录', 'API', 'GET', '/api/org/adoptions', NULL, 28, 1, '领养记录查看');

-- 5. 获取回访提醒接口权限
-- 接口: GET /api/org/followup-reminders
-- 权限注解: @PreAuthorize("hasAuthority('org:followup:read')")
INSERT INTO sys_permission(perm_code, perm_name, perm_type, http_method, api_path, parent_id, sort, enabled, remark) VALUES
('org:followup:read', '机构查看回访提醒', 'API', 'GET', '/api/org/followup-reminders', NULL, 29, 1, '回访提醒查看');

-- ============================================================
-- 为 ORG 角色分配上述权限
-- ============================================================

-- 为 ORG 角色分配机构管理相关权限
INSERT INTO sys_role_permission(role_id, permission_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.role_code = 'ORG'
  AND p.perm_code IN (
    'org:profile:read',
    'org:profile:update',
    'org:statistics:read',
    'org:adoptions:read',
    'org:followup:read'
);

-- ============================================================
-- 验证权限分配情况的查询语句
-- ============================================================

-- 查询 ORG 角色拥有的所有权限
SELECT 
    r.role_code,
    r.role_name,
    p.perm_code,
    p.perm_name,
    p.http_method,
    p.api_path
FROM sys_role r
JOIN sys_role_permission rp ON r.id = rp.role_id
JOIN sys_permission p ON rp.permission_id = p.id
WHERE r.role_code = 'ORG'
ORDER BY p.sort;

-- 查询特定权限是否已分配给 ORG 角色
SELECT 
    CASE 
        WHEN COUNT(*) > 0 THEN '权限已分配'
        ELSE '权限未分配'
    END as permission_status,
    'org:profile:read' as permission_code
FROM sys_role r
JOIN sys_role_permission rp ON r.id = rp.role_id
JOIN sys_permission p ON rp.permission_id = p.id
WHERE r.role_code = 'ORG' AND p.perm_code = 'org:profile:read';

-- ============================================================
-- 权限说明文档
-- ============================================================
/*
权限编码说明:
- org:profile:read     - 机构查看自己的基本资料信息
- org:profile:update   - 机构更新自己的资料信息
- org:statistics:read  - 机构查看自己的统计数据（领养数量、申请数量等）
- org:adoptions:read   - 机构查看自己发布的宠物的领养完成记录
- org:followup:read    - 机构查看需要回访的领养记录提醒

权限分配原则:
- 只有 ORG 角色的用户才能访问这些接口
- 每个接口都有对应的细粒度权限控制
- 权限基于 RBAC 模型，通过角色-权限关联表实现
*/