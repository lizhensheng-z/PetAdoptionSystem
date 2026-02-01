create database petAdoptionSystem;
use petAdoptionSystem;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =========================
-- Drop (dev only)
-- =========================
DROP TABLE IF EXISTS audit_log;
DROP TABLE IF EXISTS sys_notice;
DROP TABLE IF EXISTS sys_config;

DROP TABLE IF EXISTS credit_log;
DROP TABLE IF EXISTS credit_account;
DROP TABLE IF EXISTS checkin_post;

DROP TABLE IF EXISTS user_behavior;
DROP TABLE IF EXISTS user_favorite;

DROP TABLE IF EXISTS adoption_flow_log;
DROP TABLE IF EXISTS adoption_application;

DROP TABLE IF EXISTS pet_audit;

DROP TABLE IF EXISTS pet_tag;
DROP TABLE IF EXISTS tag;
DROP TABLE IF EXISTS pet_media;
DROP TABLE IF EXISTS pet;

DROP TABLE IF EXISTS org_profile;

DROP TABLE IF EXISTS sys_role_permission;
DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS sys_permission;
DROP TABLE IF EXISTS sys_role;

DROP TABLE IF EXISTS sys_user;

-- ============================================================
-- A. 账号与鉴权域（RBAC + 用户）
-- ============================================================

CREATE TABLE sys_user (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                          username VARCHAR(64) NOT NULL UNIQUE COMMENT '用户名（唯一）',
                          password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希',
                          phone VARCHAR(32) NULL COMMENT '手机号',
                          email VARCHAR(128) NULL COMMENT '邮箱',
                          role VARCHAR(16) NOT NULL COMMENT '冗余角色：USER/ORG/ADMIN（可用于快速判断，实际以RBAC为准）',
                          avatar VARCHAR(512) NULL COMMENT '头像URL',
                          status VARCHAR(16) NOT NULL DEFAULT 'NORMAL' COMMENT '账号状态：NORMAL/BANNED',
                          preference_json JSON NULL COMMENT '用户偏好JSON（智能匹配用，可选）',
                          last_login_time DATETIME NULL COMMENT '最后登录时间',
                          deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否1是',
                          create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          INDEX idx_role_status(role, status) COMMENT '角色与状态索引',
                          INDEX idx_phone(phone) COMMENT '手机号索引',
                          INDEX idx_email(email) COMMENT '邮箱索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表（领养人/机构/管理员）';

CREATE TABLE sys_role (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                          role_code VARCHAR(32) NOT NULL UNIQUE COMMENT '角色编码：USER/ORG/ADMIN 等',
                          role_name VARCHAR(64) NOT NULL COMMENT '角色名称',
                          enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用：0否1是',
                          remark VARCHAR(255) NULL COMMENT '备注',
                          create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

CREATE TABLE sys_permission (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                                perm_code VARCHAR(128) NOT NULL UNIQUE COMMENT '权限编码（唯一）：如 pet:create / admin:pet:audit',
                                perm_name VARCHAR(128) NOT NULL COMMENT '权限名称',
                                perm_type VARCHAR(16) NOT NULL COMMENT '权限类型：API/MENU/BUTTON',
                                http_method VARCHAR(16) NULL COMMENT 'HTTP方法（API类型可填）：GET/POST/PUT/DELETE',
                                api_path VARCHAR(255) NULL COMMENT 'API路径（API类型可填）：/api/org/pets/**',
                                parent_id BIGINT NULL COMMENT '父权限ID（用于菜单树）',
                                sort INT NOT NULL DEFAULT 0 COMMENT '排序',
                                enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用：0否1是',
                                remark VARCHAR(255) NULL COMMENT '备注',
                                create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                INDEX idx_type(perm_type) COMMENT '类型索引',
                                INDEX idx_parent(parent_id) COMMENT '父级索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表（API/菜单/按钮）';

CREATE TABLE sys_user_role (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                               user_id BIGINT NOT NULL COMMENT '用户ID（sys_user.id）',
                               role_id BIGINT NOT NULL COMMENT '角色ID（sys_role.id）',
                               create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               UNIQUE KEY uk_user_role(user_id, role_id),
                               INDEX idx_user(user_id),
                               INDEX idx_role(role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-角色关联表';

CREATE TABLE sys_role_permission (
                                     id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                                     role_id BIGINT NOT NULL COMMENT '角色ID（sys_role.id）',
                                     permission_id BIGINT NOT NULL COMMENT '权限ID（sys_permission.id）',
                                     create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     UNIQUE KEY uk_role_perm(role_id, permission_id),
                                     INDEX idx_role(role_id),
                                     INDEX idx_perm(permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-权限关联表';

-- ============================================================
-- B. 机构域
-- ============================================================

CREATE TABLE org_profile (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                             user_id BIGINT NOT NULL UNIQUE COMMENT '机构用户ID（sys_user.id，role=ORG）',
                             org_name VARCHAR(128) NOT NULL COMMENT '机构名称',
                             license_no VARCHAR(64) NULL COMMENT '机构资质/登记号（可选）',
                             contact_name VARCHAR(64) NULL COMMENT '联系人姓名',
                             contact_phone VARCHAR(32) NULL COMMENT '联系人电话',
                             address VARCHAR(255) NULL COMMENT '详细地址',
                             province VARCHAR(64) NULL COMMENT '省',
                             city VARCHAR(64) NULL COMMENT '市',
                             district VARCHAR(64) NULL COMMENT '区/县',
                             lng DECIMAL(10,6) NULL COMMENT '经度',
                             lat DECIMAL(10,6) NULL COMMENT '纬度',
                             verify_status VARCHAR(16) NOT NULL DEFAULT 'PENDING' COMMENT '机构认证状态：PENDING/APPROVED/REJECTED（可选启用）',
                             verify_remark VARCHAR(255) NULL COMMENT '认证备注/驳回原因',
                             deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否1是',
                             create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             INDEX idx_verify_status(verify_status) COMMENT '认证状态索引',
                             INDEX idx_city(city) COMMENT '城市索引',
                             INDEX idx_lng_lat(lng, lat) COMMENT '经纬度索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='救助机构资料表';

-- ============================================================
-- C. 宠物信息域
-- ============================================================

CREATE TABLE pet (
                     id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                     org_user_id BIGINT NOT NULL COMMENT '发布机构用户ID（sys_user.id，role=ORG）',
                     name VARCHAR(64) NULL COMMENT '宠物名字/昵称',
                     species VARCHAR(16) NOT NULL COMMENT '物种：CAT/DOG/OTHER',
                     breed VARCHAR(64) NULL COMMENT '品种',
                     gender VARCHAR(16) NULL COMMENT '性别：MALE/FEMALE/UNKNOWN',
                     age_month INT NULL COMMENT '年龄（月）',
                     size VARCHAR(8) NULL COMMENT '体型：S/M/L',
                     color VARCHAR(32) NULL COMMENT '毛色/颜色',
                     sterilized TINYINT NULL COMMENT '是否绝育：0否1是',
                     vaccinated TINYINT NULL COMMENT '是否疫苗：0否1是',
                     dewormed TINYINT NULL COMMENT '是否驱虫：0否1是',
                     health_desc VARCHAR(1000) NULL COMMENT '健康描述',
                     personality_desc VARCHAR(1000) NULL COMMENT '性格描述（文本）',
                     adopt_requirements VARCHAR(1000) NULL COMMENT '领养要求（文本）',
                     status VARCHAR(16) NOT NULL DEFAULT 'DRAFT' COMMENT '宠物状态：DRAFT/PENDING_AUDIT/PUBLISHED/APPLYING/ADOPTED/REMOVED',
                     audit_status VARCHAR(16) NOT NULL DEFAULT 'NONE' COMMENT '审核状态：NONE/PENDING/APPROVED/REJECTED',
                     lng DECIMAL(10,6) NULL COMMENT '经度（默认继承机构坐标）',
                     lat DECIMAL(10,6) NULL COMMENT '纬度（默认继承机构坐标）',
                     cover_url VARCHAR(512) NULL COMMENT '封面URL',
                     published_time DATETIME NULL COMMENT '发布时间（审核通过后写入）',
                     deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否1是',
                     create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                     update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                     INDEX idx_org_status(org_user_id, status) COMMENT '机构与宠物状态索引',
                     INDEX idx_status_species(status, species) COMMENT '状态与物种索引',
                     INDEX idx_audit_status(audit_status) COMMENT '审核状态索引',
                     INDEX idx_published_time(published_time) COMMENT '发布时间索引',
                     INDEX idx_lng_lat(lng, lat) COMMENT '经纬度索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物档案表';

CREATE TABLE pet_media (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                           pet_id BIGINT NOT NULL COMMENT '宠物ID（pet.id）',
                           url VARCHAR(512) NOT NULL COMMENT '媒体URL',
                           media_type VARCHAR(16) NOT NULL COMMENT '媒体类型：IMAGE/VIDEO',
                           sort INT NOT NULL DEFAULT 0 COMMENT '排序号（越小越靠前）',
                           deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否1是',
                           create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           INDEX idx_pet_id(pet_id) COMMENT '宠物媒体索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物媒体表（图/视频）';

CREATE TABLE tag (
                     id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                     name VARCHAR(64) NOT NULL COMMENT '标签名称',
                     tag_type VARCHAR(32) NOT NULL COMMENT '标签类型：SPECIES/PERSONALITY/HEALTH/FEATURE',
                     enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用：0否1是',
                     deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否1是',
                     create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                     UNIQUE KEY uk_type_name(tag_type, name),
                     INDEX idx_tag_type(tag_type) COMMENT '标签类型索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签字典表';

CREATE TABLE pet_tag (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                         pet_id BIGINT NOT NULL COMMENT '宠物ID（pet.id）',
                         tag_id BIGINT NOT NULL COMMENT '标签ID（tag.id）',
                         deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否1是',
                         create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         UNIQUE KEY uk_pet_tag(pet_id, tag_id),
                         INDEX idx_pet_id(pet_id) COMMENT '宠物索引',
                         INDEX idx_tag_id(tag_id) COMMENT '标签索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物-标签关联表';

-- ============================================================
-- D. 宠物审核域（管理员审核发布）
-- ============================================================

CREATE TABLE pet_audit (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                           pet_id BIGINT NOT NULL COMMENT '宠物ID（pet.id）',
                           org_user_id BIGINT NOT NULL COMMENT '提交审核的机构用户ID（sys_user.id）',
                           status VARCHAR(16) NOT NULL DEFAULT 'PENDING' COMMENT '审核状态：PENDING/APPROVED/REJECTED',
                           submit_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
                           audit_time DATETIME NULL COMMENT '审核时间',
                           auditor_id BIGINT NULL COMMENT '审核人用户ID（sys_user.id，role=ADMIN）',
                           remark VARCHAR(255) NULL COMMENT '审核备注/驳回原因',
                           deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否1是',
                           create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           INDEX idx_pet_id(pet_id) COMMENT '宠物审核索引',
                           INDEX idx_status_time(status, submit_time) COMMENT '状态与时间索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物发布审核记录表';

-- ============================================================
-- E. 领养流程域
-- ============================================================

CREATE TABLE adoption_application (
                                      id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                                      pet_id BIGINT NOT NULL COMMENT '宠物ID（pet.id）',
                                      user_id BIGINT NOT NULL COMMENT '领养人用户ID（sys_user.id，role=USER）',
                                      questionnaire_json JSON NULL COMMENT '申请问卷JSON',
                                      status VARCHAR(24) NOT NULL DEFAULT 'SUBMITTED'
                                          COMMENT '申请状态：SUBMITTED/UNDER_REVIEW/INTERVIEW/HOME_VISIT/APPROVED/REJECTED/CANCELLED',
                                      reject_reason VARCHAR(255) NULL COMMENT '拒绝原因（机构填写）',
                                      org_remark VARCHAR(255) NULL COMMENT '机构备注',
                                      decided_time DATETIME NULL COMMENT '最终决定时间（通过/拒绝）',
                                      deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否1是',
                                      create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      UNIQUE KEY uk_user_pet(user_id, pet_id) COMMENT '同一用户同一宠物不允许重复申请',
                                      INDEX idx_user_status(user_id, status) COMMENT '用户申请状态索引',
                                      INDEX idx_pet_status(pet_id, status) COMMENT '宠物申请状态索引',
                                      INDEX idx_create_time(create_time) COMMENT '创建时间索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='领养申请表';

CREATE TABLE adoption_flow_log (
                                   id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                                   application_id BIGINT NOT NULL COMMENT '申请ID（adoption_application.id）',
                                   from_status VARCHAR(24) NULL COMMENT '变更前状态',
                                   to_status VARCHAR(24) NOT NULL COMMENT '变更后状态',
                                   operator_id BIGINT NOT NULL COMMENT '操作者用户ID（机构或管理员 sys_user.id）',
                                   remark VARCHAR(255) NULL COMMENT '流转备注',
                                   create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   INDEX idx_app_id(application_id) COMMENT '申请流转索引',
                                   INDEX idx_operator_time(operator_id, create_time) COMMENT '操作者时间索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='申请状态流转日志表';

-- ============================================================
-- F. 推荐与行为数据域
-- ============================================================

CREATE TABLE user_favorite (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                               user_id BIGINT NOT NULL COMMENT '用户ID（sys_user.id）',
                               pet_id BIGINT NOT NULL COMMENT '宠物ID（pet.id）',
                               deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否1是',
                               create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               UNIQUE KEY uk_user_pet(user_id, pet_id) COMMENT '同一用户同一宠物唯一收藏',
                               INDEX idx_user_time(user_id, create_time) COMMENT '用户收藏时间索引',
                               INDEX idx_pet_time(pet_id, create_time) COMMENT '宠物被收藏时间索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

CREATE TABLE user_behavior (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                               user_id BIGINT NOT NULL COMMENT '用户ID（sys_user.id）',
                               pet_id BIGINT NOT NULL COMMENT '宠物ID（pet.id）',
                               behavior_type VARCHAR(16) NOT NULL COMMENT '行为类型：VIEW/FAVORITE/APPLY/SHARE',
                               weight INT NOT NULL DEFAULT 1 COMMENT '行为权重（用于推荐）',
                               create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               INDEX idx_user_time(user_id, create_time) COMMENT '用户行为时间索引',
                               INDEX idx_pet_time(pet_id, create_time) COMMENT '宠物行为时间索引',
                               INDEX idx_user_pet_type(user_id, pet_id, behavior_type) COMMENT '用户-宠物-行为复合索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为埋点表（推荐数据源）';

-- ============================================================
-- G. 领养后回访与信用域
-- ============================================================

CREATE TABLE checkin_post (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                              user_id BIGINT NOT NULL COMMENT '用户ID（sys_user.id）',
                              pet_id BIGINT NOT NULL COMMENT '宠物ID（pet.id）',
                              content VARCHAR(2000) NULL COMMENT '打卡内容',
                              media_urls JSON NULL COMMENT '媒体URL数组JSON',
                              deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否1是',
                              create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              INDEX idx_user_time(user_id, create_time) COMMENT '用户打卡时间索引',
                              INDEX idx_pet_time(pet_id, create_time) COMMENT '宠物打卡时间索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='领养后打卡表';

CREATE TABLE credit_account (
                                user_id BIGINT PRIMARY KEY COMMENT '用户ID（sys_user.id）',
                                score INT NOT NULL DEFAULT 0 COMMENT '信用分',
                                level INT NOT NULL DEFAULT 0 COMMENT '信用等级',
                                last_calc_time DATETIME NULL COMMENT '最近一次结算时间',
                                update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信用账户表';

CREATE TABLE credit_log (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                            user_id BIGINT NOT NULL COMMENT '用户ID（sys_user.id）',
                            delta INT NOT NULL COMMENT '分数变化（可正可负）',
                            reason VARCHAR(64) NOT NULL COMMENT '变更原因（如CHECKIN/VIOLATION）',
                            ref_type VARCHAR(32) NULL COMMENT '关联类型（checkin/application等）',
                            ref_id BIGINT NULL COMMENT '关联ID',
                            create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            INDEX idx_user_time(user_id, create_time) COMMENT '用户信用流水索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='信用分变更流水表';

-- ============================================================
-- H. 运营配置与审计域
-- ============================================================

CREATE TABLE sys_config (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                            config_key VARCHAR(64) NOT NULL UNIQUE COMMENT '配置键',
                            config_value VARCHAR(2000) NOT NULL COMMENT '配置值（字符串/JSON）',
                            remark VARCHAR(255) NULL COMMENT '备注',
                            update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表（推荐权重/信用规则等）';

CREATE TABLE sys_notice (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                            title VARCHAR(128) NOT NULL COMMENT '公告标题',
                            content TEXT NOT NULL COMMENT '公告内容',
                            status VARCHAR(16) NOT NULL DEFAULT 'PUBLISHED' COMMENT '公告状态：DRAFT/PUBLISHED/REMOVED',
                            create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            INDEX idx_status(status) COMMENT '公告状态索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统公告表';

CREATE TABLE audit_log (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                           actor_id BIGINT NOT NULL COMMENT '操作者用户ID（sys_user.id）',
                           action VARCHAR(64) NOT NULL COMMENT '动作（如BAN_USER/REVIEW_PET/DELETE_PET）',
                           target_type VARCHAR(32) NOT NULL COMMENT '目标类型（USER/PET/APPLICATION/CONFIG等）',
                           target_id BIGINT NULL COMMENT '目标ID',
                           detail_json JSON NULL COMMENT '操作详情JSON',
                           ip VARCHAR(64) NULL COMMENT '操作者IP',
                           create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           INDEX idx_actor_time(actor_id, create_time) COMMENT '操作者时间索引',
                           INDEX idx_action(action) COMMENT '动作索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审计日志表（关键操作留痕）';

-- ============================================================
-- Init Data (RBAC + Tags + Config)
-- ============================================================

-- Roles
INSERT INTO sys_role(role_code, role_name, enabled, remark) VALUES
                                                                ('USER','领养人',1,'默认领养人角色'),
                                                                ('ORG','救助机构',1,'机构用户角色'),
                                                                ('ADMIN','管理员',1,'平台管理员角色');

-- Permissions (API)
INSERT INTO sys_permission(perm_code, perm_name, perm_type, http_method, api_path, parent_id, sort, enabled, remark) VALUES
                                                                                                                         ('pet:read','浏览宠物','API','GET','/api/pets/**',NULL,1,1,'用户/游客浏览宠物'),
                                                                                                                         ('favorite:manage','收藏/取消收藏','API',NULL,'/api/favorites/**',NULL,2,1,'收藏相关'),
                                                                                                                         ('behavior:write','写入行为埋点','API','POST','/api/behavior',NULL,3,1,'推荐埋点'),

                                                                                                                         ('pet:create','机构创建宠物','API','POST','/api/org/pets',NULL,10,1,'机构创建宠物档案'),
                                                                                                                         ('pet:update','机构修改宠物','API','PUT','/api/org/pets/**',NULL,11,1,'机构修改宠物档案'),
                                                                                                                         ('pet:submit_audit','机构提交宠物审核','API','POST','/api/org/pets/**/submit-audit',NULL,12,1,'提交审核'),
                                                                                                                         ('pet:remove','机构下架/删除宠物','API',NULL,'/api/org/pets/**',NULL,13,1,'下架或删除'),

                                                                                                                         ('app:apply','用户发起领养申请','API','POST','/api/applications',NULL,20,1,'用户申请'),
                                                                                                                         ('app:my','用户查看我的申请','API','GET','/api/applications/my',NULL,21,1,'用户申请列表'),
                                                                                                                         ('app:cancel','用户撤回申请','API','POST','/api/applications/**/cancel',NULL,22,1,'撤回申请'),

                                                                                                                         ('app:org:list','机构查看申请列表','API','GET','/api/org/applications',NULL,30,1,'机构申请管理'),
                                                                                                                         ('app:org:detail','机构查看申请详情','API','GET','/api/org/applications/**',NULL,31,1,'申请详情'),
                                                                                                                         ('app:org:status','机构流转申请状态','API','POST','/api/org/applications/**/status',NULL,32,1,'状态流转'),

                                                                                                                         ('checkin:create','用户发布打卡','API','POST','/api/checkins',NULL,40,1,'领养后打卡'),
                                                                                                                         ('checkin:my','用户查看我的打卡','API','GET','/api/checkins/my',NULL,41,1,'打卡列表'),
                                                                                                                         ('credit:me','用户查看信用','API','GET','/api/credit/me',NULL,42,1,'信用查看'),

                                                                                                                         ('admin:pet_audit:list','管理员查看宠物审核列表','API','GET','/api/admin/pet-audits',NULL,90,1,'审核列表'),
                                                                                                                         ('admin:pet_audit:decision','管理员审核宠物（通过/驳回）','API','POST','/api/admin/pet-audits/**/decision',NULL,91,1,'审核操作'),
                                                                                                                         ('admin:tag:manage','管理员管理标签库','API',NULL,'/api/admin/tags/**',NULL,92,1,'标签维护'),
                                                                                                                         ('admin:config:manage','管理员配置参数','API',NULL,'/api/admin/config/**',NULL,93,1,'运营参数'),
                                                                                                                         ('admin:user:manage','管理员用户管理','API',NULL,'/api/admin/users/**',NULL,94,1,'封禁/解封等');

-- Role -> Permission mapping

-- USER
INSERT INTO sys_role_permission(role_id, permission_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.role_code='USER'
  AND p.perm_code IN (
                      'pet:read','favorite:manage','behavior:write',
                      'app:apply','app:my','app:cancel',
                      'checkin:create','checkin:my','credit:me'
    );

-- ORG
INSERT INTO sys_role_permission(role_id, permission_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.role_code='ORG'
  AND p.perm_code IN (
                      'pet:read','behavior:write',
                      'pet:create','pet:update','pet:submit_audit','pet:remove',
                      'app:org:list','app:org:detail','app:org:status'
    );

-- ADMIN (all permissions)
INSERT INTO sys_role_permission(role_id, permission_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.role_code='ADMIN';

-- Tags
INSERT INTO tag(name, tag_type, enabled) VALUES
                                             ('亲人','PERSONALITY',1),
                                             ('胆小','PERSONALITY',1),
                                             ('活泼','PERSONALITY',1),
                                             ('安静','PERSONALITY',1),
                                             ('亲猫','PERSONALITY',1),
                                             ('亲狗','PERSONALITY',1),
                                             ('需陪伴','FEATURE',1),
                                             ('可独处','FEATURE',1),
                                             ('已绝育','HEALTH',1),
                                             ('已疫苗','HEALTH',1),
                                             ('已驱虫','HEALTH',1);

-- Config
INSERT INTO sys_config(config_key, config_value, remark) VALUES
                                                             ('recommend.alpha', '0.55', '偏好/标签匹配权重'),
                                                             ('recommend.beta',  '0.20', '协同过滤权重'),
                                                             ('recommend.gamma', '0.20', '距离权重'),
                                                             ('recommend.delta', '0.05', '新鲜度权重'),
                                                             ('recommend.maxDistanceKm', '50', '默认附近最大距离(km)'),
                                                             ('credit.checkin.dailyMax', '1', '每日计分打卡次数上限'),
                                                             ('credit.checkin.base', '2', '打卡基础分'),
                                                             ('credit.checkin.mediaBonus', '1', '含媒体加分'),
                                                             ('credit.checkin.textBonus', '1', '文本>30字加分'),
                                                             ('credit.violation.penalty', '-20', '违规扣分基准');


