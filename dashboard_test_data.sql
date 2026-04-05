-- ============================================================
-- 机构首页Dashboard接口测试数据
-- ============================================================
-- 使用说明：在 petAdoptionSystem 数据库中执行此脚本
-- 用于测试 GET /api/org/dashboard/home 接口
-- ============================================================

USE petAdoptionSystem;
SET NAMES utf8mb4;

-- 1. 清理现有测试数据（可选）
-- DELETE FROM adoption_flow_log WHERE application_id IN (SELECT id FROM adoption_application WHERE user_id IN (SELECT id FROM sys_user WHERE username LIKE 'test_%'));
-- DELETE FROM adoption_application WHERE user_id IN (SELECT id FROM sys_user WHERE username LIKE 'test_%');
-- DELETE FROM checkin_post WHERE user_id IN (SELECT id FROM sys_user WHERE username LIKE 'test_%');
-- DELETE FROM credit_log WHERE user_id IN (SELECT id FROM sys_user WHERE username LIKE 'test_%');
-- DELETE FROM credit_account WHERE user_id IN (SELECT id FROM sys_user WHERE username LIKE 'test_%');
-- DELETE FROM user_favorite WHERE user_id IN (SELECT id FROM sys_user WHERE username LIKE 'test_%');
-- DELETE FROM user_behavior WHERE user_id IN (SELECT id FROM sys_user WHERE username LIKE 'test_%');
-- DELETE FROM pet_tag WHERE pet_id IN (SELECT id FROM pet WHERE org_user_id IN (SELECT id FROM sys_user WHERE username = 'test_org'));
-- DELETE FROM pet_media WHERE pet_id IN (SELECT id FROM pet WHERE org_user_id IN (SELECT id FROM sys_user WHERE username = 'test_org'));
-- DELETE FROM pet WHERE org_user_id IN (SELECT id FROM sys_user WHERE username = 'test_org');
-- DELETE FROM org_profile WHERE user_id IN (SELECT id FROM sys_user WHERE username = 'test_org');
-- DELETE FROM sys_user_role WHERE user_id IN (SELECT id FROM sys_user WHERE username LIKE 'test_%');
-- DELETE FROM sys_user WHERE username LIKE 'test_%';



-- ============================================================
-- 2. 创建测试机构用户（如已存在则复用）
-- ============================================================
INSERT INTO sys_user (username, password_hash, phone, email, role, avatar, status, create_time, update_time)
SELECT 'test_org',
       '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',
       '13800138001','org@test.com','ORG','https://example.com/org-avatar.jpg','NORMAL',NOW(),NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username='test_org');

SET @org_user_id = (SELECT id FROM sys_user WHERE username='test_org' LIMIT 1);

-- 机构角色绑定（防重复）
INSERT IGNORE INTO sys_user_role(user_id, role_id)
SELECT @org_user_id, id FROM sys_role WHERE role_code='ORG';

-- ============================================================
-- 3. 创建机构资料（如已存在则更新）
-- ============================================================
INSERT INTO org_profile (user_id, org_name, license_no, contact_name, contact_phone, address, province, city, district, lng, lat, cover_url, verify_status, create_time, update_time)
VALUES
    (@org_user_id,'爱心救助站','ORG20260001','王明','13800138001','北京市朝阳区建国路88号','北京市','北京市','朝阳区',116.48,39.98,'https://example.com/org-cover.jpg','APPROVED',NOW(),NOW())
ON DUPLICATE KEY UPDATE
                     org_name=VALUES(org_name),
                     license_no=VALUES(license_no),
                     contact_name=VALUES(contact_name),
                     contact_phone=VALUES(contact_phone),
                     address=VALUES(address),
                     province=VALUES(province),
                     city=VALUES(city),
                     district=VALUES(district),
                     lng=VALUES(lng),
                     lat=VALUES(lat),
                     cover_url=VALUES(cover_url),
                     verify_status=VALUES(verify_status),
                     update_time=NOW();

-- ============================================================
-- 4. 创建测试领养用户（如已存在则跳过），并绑定 USER 角色
-- ============================================================
INSERT INTO sys_user (username, password_hash, phone, email, role, avatar, status, create_time, update_time)
SELECT * FROM (
                  SELECT 'test_user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','13900139001','user1@test.com','USER','https://example.com/avatar1.jpg','NORMAL',NOW(),NOW()
                  UNION ALL SELECT 'test_user2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','13900139002','user2@test.com','USER','https://example.com/avatar2.jpg','NORMAL',NOW(),NOW()
                  UNION ALL SELECT 'test_user3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','13900139003','user3@test.com','USER','https://example.com/avatar3.jpg','NORMAL',NOW(),NOW()
                  UNION ALL SELECT 'test_user4', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','13900139004','user4@test.com','USER','https://example.com/avatar4.jpg','NORMAL',NOW(),NOW()
                  UNION ALL SELECT 'test_user5', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','13900139005','user5@test.com','USER','https://example.com/avatar5.jpg','NORMAL',NOW(),NOW()
              ) t(username,password_hash,phone,email,role,avatar,status,create_time,update_time)
WHERE NOT EXISTS (SELECT 1 FROM sys_user u WHERE u.username=t.username);

SET @user1_id = (SELECT id FROM sys_user WHERE username='test_user1' LIMIT 1);
SET @user2_id = (SELECT id FROM sys_user WHERE username='test_user2' LIMIT 1);
SET @user3_id = (SELECT id FROM sys_user WHERE username='test_user3' LIMIT 1);
SET @user4_id = (SELECT id FROM sys_user WHERE username='test_user4' LIMIT 1);
SET @user5_id = (SELECT id FROM sys_user WHERE username='test_user5' LIMIT 1);

INSERT IGNORE INTO sys_user_role(user_id, role_id)
SELECT u.id, r.id
FROM sys_user u
         JOIN sys_role r ON r.role_code='USER'
WHERE u.username LIKE 'test_user%';

-- ============================================================
-- 5. 创建宠物（用 name 做唯一识别：同一机构下 name 不重复）
--    这里用 INSERT IGNORE 防重复（建议你也加唯一约束：org_user_id+name）
-- ============================================================

INSERT IGNORE INTO pet
(org_user_id, name, species, breed, gender, age_month, size, color, sterilized, vaccinated, dewormed,
 health_desc, personality_desc, adopt_requirements, status, audit_status, lng, lat, cover_url, published_time, deleted, create_time, update_time)
VALUES
-- 已发布 18
(@org_user_id,'小白','CAT','中华田园猫','FEMALE',24,'M','白色',1,1,1,'健康活泼，已完成绝育疫苗','亲人可爱，喜欢被人抚摸','需要有养猫经验','PUBLISHED','APPROVED',116.48,39.98,'https://example.com/pet1.jpg',DATE_SUB(NOW(),INTERVAL 2 DAY),0,DATE_SUB(NOW(),INTERVAL 7 DAY),NOW()),
(@org_user_id,'小黑','DOG','拉布拉多','MALE',36,'L','黑色',1,1,1,'非常健康，精力充沛','活泼好动，喜欢运动','需要较大的活动空间','PUBLISHED','APPROVED',116.48,39.98,'https://example.com/pet2.jpg',DATE_SUB(NOW(),INTERVAL 3 DAY),0,DATE_SUB(NOW(),INTERVAL 10 DAY),NOW()),
(@org_user_id,'小花','CAT','橘猫','FEMALE',18,'M','橘色',1,1,1,'健康状态良好','温顺亲人，适合新手','希望主人有耐心','PUBLISHED','APPROVED',116.48,39.98,'https://example.com/pet3.jpg',DATE_SUB(NOW(),INTERVAL 1 DAY),0,DATE_SUB(NOW(),INTERVAL 5 DAY),NOW()),
(@org_user_id,'旺财','DOG','金毛','MALE',24,'L','金色',1,1,1,'非常健康','性格温顺，对人友好','需要有养狗经验','PUBLISHED','APPROVED',116.48,39.98,'https://example.com/pet4.jpg',DATE_SUB(NOW(),INTERVAL 4 DAY),0,DATE_SUB(NOW(),INTERVAL 12 DAY),NOW()),
(@org_user_id,'雪球','CAT','布偶猫','FEMALE',12,'L','白色',1,1,1,'健康漂亮','安静温顺，喜欢安静环境','适合家庭饲养','PUBLISHED','APPROVED',116.48,39.98,'https://example.com/pet5.jpg',DATE_SUB(NOW(),INTERVAL 5 DAY),0,DATE_SUB(NOW(),INTERVAL 14 DAY),NOW()),
(@org_user_id,'黑豆','DOG','哈士奇','MALE',30,'L','黑白',1,1,1,'健康强壮','活泼好动，精力旺盛','需要大量运动','PUBLISHED','APPROVED',116.48,39.98,'https://example.com/pet6.jpg',DATE_SUB(NOW(),INTERVAL 6 DAY),0,DATE_SUB(NOW(),INTERVAL 16 DAY),NOW()),
(@org_user_id,'奶茶','CAT','英短','FEMALE',15,'M','银渐层',1,1,1,'健康良好','性格温和，容易相处','希望主人细心照顾','PUBLISHED','APPROVED',116.48,39.98,'https://example.com/pet7.jpg',DATE_SUB(NOW(),INTERVAL 7 DAY),0,DATE_SUB(NOW(),INTERVAL 18 DAY),NOW()),
(@org_user_id,'大黄','DOG','柴犬','MALE',27,'M','黄色',1,1,1,'健康活泼','忠诚可爱，对主人忠诚','适合有经验的铲屎官','PUBLISHED','APPROVED',116.48,39.98,'https://example.com/pet8.jpg',DATE_SUB(NOW(),INTERVAL 8 DAY),0,DATE_SUB(NOW(),INTERVAL 20 DAY),NOW()),
(@org_user_id,'糯米','CAT','暹罗猫','FEMALE',20,'S','浅棕色',1,1,1,'健康可爱','活泼好动，喜欢互动','需要经常陪伴','PUBLISHED','APPROVED',116.48,39.98,'https://example.com/pet9.jpg',DATE_SUB(NOW(),INTERVAL 9 DAY),0,DATE_SUB(NOW(),INTERVAL 22 DAY),NOW()),
(@org_user_id,'贝贝','DOG','柯基','FEMALE',18,'M','黄白',1,1,1,'健康良好','活泼可爱，腿短短的','适合公寓饲养','PUBLISHED','APPROVED',116.48,39.98,'https://example.com/pet10.jpg',DATE_SUB(NOW(),INTERVAL 10 DAY),0,DATE_SUB(NOW(),INTERVAL 24 DAY),NOW()),
(@org_user_id,'布丁','CAT','美短','MALE',22,'M','银白',1,1,1,'健康强壮','温顺亲人，适合家庭','希望主人有责任心','PUBLISHED','APPROVED',116.48,39.98,'https://example.com/pet11.jpg',DATE_SUB(NOW(),INTERVAL 11 DAY),0,DATE_SUB(NOW(),INTERVAL 26 DAY),NOW()),
(@org_user_id,'欢欢','DOG','边牧','FEMALE',33,'M','黑白',1,1,1,'非常健康','聪明伶俐，智商很高','需要智力和运动刺激','PUBLISHED','APPROVED',116.48,39.98,'https://example.com/pet12.jpg',DATE_SUB(NOW(),INTERVAL 12 DAY),0,DATE_SUB(NOW(),INTERVAL 28 DAY),NOW()),
(@org_user_id,'豆豆','CAT','折耳猫','MALE',16,'S','棕色',1,1,1,'健康可爱','安静温顺，适合新手','希望主人有耐心','PUBLISHED','APPROVED',116.48,39.98,'https://example.com/pet13.jpg',DATE_SUB(NOW(),INTERVAL 13 DAY),0,DATE_SUB(NOW(),INTERVAL 30 DAY),NOW()),
(@org_user_id,'皮皮','DOG','贵宾','MALE',21,'S','白色',1,1,1,'健康良好','活泼聪明，容易训练','适合公寓饲养','PUBLISHED','APPROVED',116.48,39.98,'https://example.com/pet14.jpg',DATE_SUB(NOW(),INTERVAL 14 DAY),0,DATE_SUB(NOW(),INTERVAL 32 DAY),NOW()),
(@org_user_id,'糖糖','CAT','波斯猫','FEMALE',25,'L','白色',1,1,1,'健康漂亮','优雅高贵，性格温和','需要细心照料','PUBLISHED','APPROVED',116.48,39.98,'https://example.com/pet15.jpg',DATE_SUB(NOW(),INTERVAL 15 DAY),0,DATE_SUB(NOW(),INTERVAL 34 DAY),NOW()),
(@org_user_id,'乐乐','DOG','萨摩耶','MALE',28,'L','白色',1,1,1,'健康强壮','笑容甜美，性格开朗','需要较多运动','PUBLISHED','APPROVED',116.48,39.98,'https://example.com/pet16.jpg',DATE_SUB(NOW(),INTERVAL 16 DAY),0,DATE_SUB(NOW(),INTERVAL 36 DAY),NOW()),
(@org_user_id,'咪咪','CAT','缅因猫','FEMALE',30,'L','棕虎斑',1,1,1,'健康强壮','体型大但性格温和','需要较大空间','PUBLISHED','APPROVED',116.48,39.98,'https://example.com/pet17.jpg',DATE_SUB(NOW(),INTERVAL 17 DAY),0,DATE_SUB(NOW(),INTERVAL 38 DAY),NOW()),
(@org_user_id,'毛毛','DOG','比熊','MALE',19,'S','白色',1,1,1,'健康可爱','活泼亲人，不掉毛','适合公寓饲养','PUBLISHED','APPROVED',116.48,39.98,'https://example.com/pet18.jpg',DATE_SUB(NOW(),INTERVAL 18 DAY),0,DATE_SUB(NOW(),INTERVAL 40 DAY),NOW()),

-- 申请中 2
(@org_user_id,'球球','CAT','苏格兰折耳猫','MALE',14,'M','蓝灰色',1,1,1,'健康状态良好','温顺可爱，适合家庭','希望主人有耐心','APPLYING','APPROVED',116.48,39.98,'https://example.com/pet19.jpg',DATE_SUB(NOW(),INTERVAL 1 DAY),0,DATE_SUB(NOW(),INTERVAL 3 DAY),NOW()),
(@org_user_id,'点点','DOG','法国斗牛','FEMALE',20,'S','黑白',1,1,1,'健康良好','活泼可爱，性格独特','适合公寓饲养','APPLYING','APPROVED',116.48,39.98,'https://example.com/pet20.jpg',DATE_SUB(NOW(),INTERVAL 2 DAY),0,DATE_SUB(NOW(),INTERVAL 5 DAY),NOW()),

-- 草稿 5
(@org_user_id,'小灰','CAT','狸花猫','MALE',16,'M','灰白',1,1,1,'健康状态良好','活泼好动，亲人','希望主人有责任心','DRAFT','NONE',NULL,NULL,'https://example.com/pet21.jpg',NULL,0,DATE_SUB(NOW(),INTERVAL 1 DAY),NOW()),
(@org_user_id,'小黄','DOG','中华田园犬','MALE',22,'M','黄色',1,1,1,'健康强壮','忠诚可爱，易养','适合新手饲养','DRAFT','NONE',NULL,NULL,'https://example.com/pet22.jpg',NULL,0,DATE_SUB(NOW(),INTERVAL 2 DAY),NOW()),
(@org_user_id,'小橙','CAT','橘猫','FEMALE',13,'M','橘色',1,1,1,'健康可爱','温顺亲人，适合家庭','希望主人细心','DRAFT','NONE',NULL,NULL,'https://example.com/pet23.jpg',NULL,0,DATE_SUB(NOW(),INTERVAL 3 DAY),NOW()),
(@org_user_id,'小蓝','DOG','边境牧羊犬','MALE',28,'M','黑白',1,1,1,'非常健康','聪明伶俐，智商高','需要大量运动','DRAFT','NONE',NULL,NULL,'https://example.com/pet24.jpg',NULL,0,DATE_SUB(NOW(),INTERVAL 4 DAY),NOW()),
(@org_user_id,'小紫','CAT','俄罗斯蓝猫','FEMALE',18,'S','蓝灰色',1,1,1,'健康漂亮','安静温顺，性格独立','适合安静环境','DRAFT','NONE',NULL,NULL,'https://example.com/pet25.jpg',NULL,0,DATE_SUB(NOW(),INTERVAL 5 DAY),NOW()),

-- 待审核 2
(@org_user_id,'小棕','DOG','金毛寻回犬','MALE',26,'L','金色',1,1,1,'健康强壮','性格温顺，对人友好','需要有养狗经验','PENDING_AUDIT','PENDING',NULL,NULL,'https://example.com/pet26.jpg',NULL,0,DATE_SUB(NOW(),INTERVAL 1 DAY),NOW()),
(@org_user_id,'小青','CAT','缅因猫','FEMALE',32,'L','棕虎斑',1,1,1,'健康强壮','体型大但性格温和','需要较大空间','PENDING_AUDIT','PENDING',NULL,NULL,'https://example.com/pet27.jpg',NULL,0,DATE_SUB(NOW(),INTERVAL 2 DAY),NOW()),

-- 已领养 4
(@org_user_id,'小金','DOG','金毛','MALE',24,'L','金色',1,1,1,'非常健康','性格温顺，对人友好','需要有养狗经验','ADOPTED','APPROVED',116.48,39.98,'https://example.com/pet28.jpg',DATE_SUB(NOW(),INTERVAL 60 DAY),0,DATE_SUB(NOW(),INTERVAL 70 DAY),NOW()),
(@org_user_id,'小银','CAT','布偶猫','FEMALE',15,'L','白色',1,1,1,'健康漂亮','安静温顺，适合家庭','适合家庭饲养','ADOPTED','APPROVED',116.48,39.98,'https://example.com/pet29.jpg',DATE_SUB(NOW(),INTERVAL 30 DAY),0,DATE_SUB(NOW(),INTERVAL 40 DAY),NOW()),
(@org_user_id,'小铜','DOG','柯基','MALE',18,'M','黄白',1,1,1,'健康良好','活泼可爱，腿短短的','适合公寓饲养','ADOPTED','APPROVED',116.48,39.98,'https://example.com/pet30.jpg',DATE_SUB(NOW(),INTERVAL 15 DAY),0,DATE_SUB(NOW(),INTERVAL 25 DAY),NOW()),
(@org_user_id,'小铁','CAT','美短','MALE',20,'M','银白',1,1,1,'健康强壮','温顺亲人，适合家庭','希望主人有责任心','ADOPTED','APPROVED',116.48,39.98,'https://example.com/pet31.jpg',DATE_SUB(NOW(),INTERVAL 8 DAY),0,DATE_SUB(NOW(),INTERVAL 18 DAY),NOW());

-- ============================================================
-- 5.1 把关键 pet_id 查出来（后续全部用变量）
-- ============================================================
SET @pet1  = (SELECT id FROM pet WHERE org_user_id=@org_user_id AND name='小白' LIMIT 1);
SET @pet2  = (SELECT id FROM pet WHERE org_user_id=@org_user_id AND name='小黑' LIMIT 1);
SET @pet3  = (SELECT id FROM pet WHERE org_user_id=@org_user_id AND name='小花' LIMIT 1);
SET @pet4  = (SELECT id FROM pet WHERE org_user_id=@org_user_id AND name='旺财' LIMIT 1);
SET @pet5  = (SELECT id FROM pet WHERE org_user_id=@org_user_id AND name='雪球' LIMIT 1);
SET @pet6  = (SELECT id FROM pet WHERE org_user_id=@org_user_id AND name='黑豆' LIMIT 1);
SET @pet7  = (SELECT id FROM pet WHERE org_user_id=@org_user_id AND name='奶茶' LIMIT 1);
SET @pet8  = (SELECT id FROM pet WHERE org_user_id=@org_user_id AND name='大黄' LIMIT 1);
SET @pet9  = (SELECT id FROM pet WHERE org_user_id=@org_user_id AND name='糯米' LIMIT 1);
SET @pet10 = (SELECT id FROM pet WHERE org_user_id=@org_user_id AND name='贝贝' LIMIT 1);
SET @pet11 = (SELECT id FROM pet WHERE org_user_id=@org_user_id AND name='布丁' LIMIT 1);
SET @pet12 = (SELECT id FROM pet WHERE org_user_id=@org_user_id AND name='欢欢' LIMIT 1);
SET @pet13 = (SELECT id FROM pet WHERE org_user_id=@org_user_id AND name='豆豆' LIMIT 1);
SET @pet14 = (SELECT id FROM pet WHERE org_user_id=@org_user_id AND name='皮皮' LIMIT 1);
SET @pet15 = (SELECT id FROM pet WHERE org_user_id=@org_user_id AND name='糖糖' LIMIT 1);
SET @pet16 = (SELECT id FROM pet WHERE org_user_id=@org_user_id AND name='乐乐' LIMIT 1);
SET @pet17 = (SELECT id FROM pet WHERE org_user_id=@org_user_id AND name='咪咪' LIMIT 1);
SET @pet18 = (SELECT id FROM pet WHERE org_user_id=@org_user_id AND name='毛毛' LIMIT 1);
SET @pet19 = (SELECT id FROM pet WHERE org_user_id=@org_user_id AND name='球球' LIMIT 1);
SET @pet20 = (SELECT id FROM pet WHERE org_user_id=@org_user_id AND name='点点' LIMIT 1);
SET @pet21 = (SELECT id FROM pet WHERE org_user_id=@org_user_id AND name='小灰' LIMIT 1);
SET @pet22 = (SELECT id FROM pet WHERE org_user_id=@org_user_id AND name='小黄' LIMIT 1);
SET @pet23 = (SELECT id FROM pet WHERE org_user_id=@org_user_id AND name='小橙' LIMIT 1);
SET @pet24 = (SELECT id FROM pet WHERE org_user_id=@org_user_id AND name='小蓝' LIMIT 1);
SET @pet25 = (SELECT id FROM pet WHERE org_user_id=@org_user_id AND name='小紫' LIMIT 1);
SET @pet26 = (SELECT id FROM pet WHERE org_user_id=@org_user_id AND name='小棕' LIMIT 1);
SET @pet27 = (SELECT id FROM pet WHERE org_user_id=@org_user_id AND name='小青' LIMIT 1);
SET @pet28 = (SELECT id FROM pet WHERE org_user_id=@org_user_id AND name='小金' LIMIT 1);
SET @pet29 = (SELECT id FROM pet WHERE org_user_id=@org_user_id AND name='小银' LIMIT 1);
SET @pet30 = (SELECT id FROM pet WHERE org_user_id=@org_user_id AND name='小铜' LIMIT 1);
SET @pet31 = (SELECT id FROM pet WHERE org_user_id=@org_user_id AND name='小铁' LIMIT 1);

-- ============================================================
-- 6. 创建宠物媒体（用真实 pet_id；用 INSERT IGNORE 防重复）
-- ============================================================
INSERT IGNORE INTO pet_media (pet_id, url, media_type, sort, deleted, create_time) VALUES
                                                                                       (@pet1,'https://example.com/pet1-media1.jpg','IMAGE',1,0,NOW()),
                                                                                       (@pet1,'https://example.com/pet1-media2.jpg','IMAGE',2,0,NOW()),
                                                                                       (@pet2,'https://example.com/pet2-media1.jpg','IMAGE',1,0,NOW()),
                                                                                       (@pet3,'https://example.com/pet3-media1.jpg','IMAGE',1,0,NOW()),
                                                                                       (@pet4,'https://example.com/pet4-media1.jpg','IMAGE',1,0,NOW()),
                                                                                       (@pet5,'https://example.com/pet5-media1.jpg','IMAGE',1,0,NOW());

-- ============================================================
-- 7. 领养申请数据（注意：同一 user+pet 唯一，避免重复）
-- ============================================================

-- 待审核 8 条
INSERT IGNORE INTO adoption_application
(pet_id, user_id, questionnaire_json, status, reject_reason, org_remark, decided_time, deleted, create_time, update_time)
VALUES
    (@pet1, @user1_id, '{"housing":"自有住房","experience":"有3年养猫经验"}', 'SUBMITTED', NULL, NULL, NULL, 0, DATE_SUB(NOW(),INTERVAL 1 DAY), NOW()),
    (@pet2, @user2_id, '{"housing":"租房","experience":"有2年养狗经验"}', 'UNDER_REVIEW', NULL, '资料审核中', NULL, 0, DATE_SUB(NOW(),INTERVAL 2 DAY), NOW()),
    (@pet3, @user3_id, '{"housing":"自有住房","experience":"首次养宠"}', 'SUBMITTED', NULL, NULL, NULL, 0, DATE_SUB(NOW(),INTERVAL 3 DAY), NOW()),
    (@pet4, @user4_id, '{"housing":"租房","experience":"有5年养狗经验"}', 'UNDER_REVIEW', NULL, '等待面谈', NULL, 0, DATE_SUB(NOW(),INTERVAL 4 DAY), NOW()),
    (@pet5, @user5_id, '{"housing":"自有住房","experience":"有1年养猫经验"}', 'SUBMITTED', NULL, NULL, NULL, 0, DATE_SUB(NOW(),INTERVAL 5 DAY), NOW()),
    (@pet6, @user1_id, '{"housing":"租房","experience":"有3年养猫经验"}', 'UNDER_REVIEW', NULL, '资料审核中', NULL, 0, DATE_SUB(NOW(),INTERVAL 6 DAY), NOW()),
    (@pet7, @user2_id, '{"housing":"自有住房","experience":"首次养宠"}', 'SUBMITTED', NULL, NULL, NULL, 0, DATE_SUB(NOW(),INTERVAL 7 DAY), NOW()),
    (@pet8, @user3_id, '{"housing":"租房","experience":"有2年养狗经验"}', 'UNDER_REVIEW', NULL, '等待审核', NULL, 0, DATE_SUB(NOW(),INTERVAL 8 DAY), NOW());

-- 本月通过 12 条
INSERT IGNORE INTO adoption_application
(pet_id, user_id, questionnaire_json, status, reject_reason, org_remark, decided_time, deleted, create_time, update_time)
VALUES
    (@pet9,  @user4_id, '{"housing":"自有住房","experience":"有3年养猫经验"}', 'APPROVED', NULL, '审核通过', DATE_SUB(NOW(),INTERVAL 1 DAY), 0, DATE_SUB(NOW(),INTERVAL 5 DAY), NOW()),
    (@pet10, @user5_id, '{"housing":"租房","experience":"有2年养狗经验"}', 'APPROVED', NULL, '审核通过', DATE_SUB(NOW(),INTERVAL 2 DAY), 0, DATE_SUB(NOW(),INTERVAL 6 DAY), NOW()),
    (@pet11, @user1_id, '{"housing":"自有住房","experience":"首次养宠"}', 'APPROVED', NULL, '审核通过', DATE_SUB(NOW(),INTERVAL 3 DAY), 0, DATE_SUB(NOW(),INTERVAL 7 DAY), NOW()),
    (@pet12, @user2_id, '{"housing":"租房","experience":"有5年养狗经验"}', 'APPROVED', NULL, '审核通过', DATE_SUB(NOW(),INTERVAL 4 DAY), 0, DATE_SUB(NOW(),INTERVAL 8 DAY), NOW()),
    (@pet13, @user3_id, '{"housing":"自有住房","experience":"有1年养猫经验"}', 'APPROVED', NULL, '审核通过', DATE_SUB(NOW(),INTERVAL 5 DAY), 0, DATE_SUB(NOW(),INTERVAL 9 DAY), NOW()),
    (@pet14, @user4_id, '{"housing":"租房","experience":"有3年养猫经验"}', 'APPROVED', NULL, '审核通过', DATE_SUB(NOW(),INTERVAL 6 DAY), 0, DATE_SUB(NOW(),INTERVAL 10 DAY), NOW()),
    (@pet15, @user5_id, '{"housing":"自有住房","experience":"首次养宠"}', 'APPROVED', NULL, '审核通过', DATE_SUB(NOW(),INTERVAL 7 DAY), 0, DATE_SUB(NOW(),INTERVAL 11 DAY), NOW()),
    (@pet16, @user1_id, '{"housing":"租房","experience":"有2年养狗经验"}', 'APPROVED', NULL, '审核通过', DATE_SUB(NOW(),INTERVAL 8 DAY), 0, DATE_SUB(NOW(),INTERVAL 12 DAY), NOW()),
    (@pet17, @user2_id, '{"housing":"自有住房","experience":"有5年养狗经验"}', 'APPROVED', NULL, '审核通过', DATE_SUB(NOW(),INTERVAL 9 DAY), 0, DATE_SUB(NOW(),INTERVAL 13 DAY), NOW()),
    (@pet18, @user3_id, '{"housing":"租房","experience":"有1年养猫经验"}', 'APPROVED', NULL, '审核通过', DATE_SUB(NOW(),INTERVAL 10 DAY), 0, DATE_SUB(NOW(),INTERVAL 14 DAY), NOW()),
    (@pet19, @user4_id, '{"housing":"自有住房","experience":"有3年养猫经验"}', 'APPROVED', NULL, '审核通过', DATE_SUB(NOW(),INTERVAL 11 DAY), 0, DATE_SUB(NOW(),INTERVAL 15 DAY), NOW()),
    (@pet20, @user5_id, '{"housing":"租房","experience":"有2年养狗经验"}', 'APPROVED', NULL, '审核通过', DATE_SUB(NOW(),INTERVAL 12 DAY), 0, DATE_SUB(NOW(),INTERVAL 16 DAY), NOW());

-- 历史通过（示例 10 条；你要 156 条我可以给你用循环/递归CTE批量生成）
INSERT IGNORE INTO adoption_application
(pet_id, user_id, questionnaire_json, status, reject_reason, org_remark, decided_time, deleted, create_time, update_time)
VALUES
    (@pet21, @user1_id, '{"housing":"自有住房","experience":"首次养宠"}', 'APPROVED', NULL, '审核通过', DATE_SUB(NOW(),INTERVAL 25 DAY), 0, DATE_SUB(NOW(),INTERVAL 30 DAY), NOW()),
    (@pet22, @user2_id, '{"housing":"租房","experience":"有2年养狗经验"}', 'APPROVED', NULL, '审核通过', DATE_SUB(NOW(),INTERVAL 30 DAY), 0, DATE_SUB(NOW(),INTERVAL 35 DAY), NOW()),
    (@pet23, @user3_id, '{"housing":"自有住房","experience":"有1年养猫经验"}', 'APPROVED', NULL, '审核通过', DATE_SUB(NOW(),INTERVAL 35 DAY), 0, DATE_SUB(NOW(),INTERVAL 40 DAY), NOW()),
    (@pet24, @user4_id, '{"housing":"租房","experience":"有3年养猫经验"}', 'APPROVED', NULL, '审核通过', DATE_SUB(NOW(),INTERVAL 40 DAY), 0, DATE_SUB(NOW(),INTERVAL 45 DAY), NOW()),
    (@pet25, @user5_id, '{"housing":"自有住房","experience":"首次养宠"}', 'APPROVED', NULL, '审核通过', DATE_SUB(NOW(),INTERVAL 45 DAY), 0, DATE_SUB(NOW(),INTERVAL 50 DAY), NOW()),
    (@pet26, @user1_id, '{"housing":"租房","experience":"有2年养狗经验"}', 'APPROVED', NULL, '审核通过', DATE_SUB(NOW(),INTERVAL 50 DAY), 0, DATE_SUB(NOW(),INTERVAL 55 DAY), NOW()),
    (@pet27, @user2_id, '{"housing":"自有住房","experience":"有5年养狗经验"}', 'APPROVED', NULL, '审核通过', DATE_SUB(NOW(),INTERVAL 55 DAY), 0, DATE_SUB(NOW(),INTERVAL 60 DAY), NOW()),
    (@pet28, @user3_id, '{"housing":"租房","experience":"有1年养猫经验"}', 'APPROVED', NULL, '审核通过', DATE_SUB(NOW(),INTERVAL 60 DAY), 0, DATE_SUB(NOW(),INTERVAL 65 DAY), NOW()),
    (@pet29, @user4_id, '{"housing":"自有住房","experience":"有3年养猫经验"}', 'APPROVED', NULL, '审核通过', DATE_SUB(NOW(),INTERVAL 65 DAY), 0, DATE_SUB(NOW(),INTERVAL 70 DAY), NOW()),
    (@pet30, @user5_id, '{"housing":"租房","experience":"有2年养狗经验"}', 'APPROVED', NULL, '审核通过', DATE_SUB(NOW(),INTERVAL 70 DAY), 0, DATE_SUB(NOW(),INTERVAL 75 DAY), NOW());

-- ============================================================
-- 8. 回访打卡（用真实 pet_id）
-- ============================================================
INSERT IGNORE INTO checkin_post (user_id, pet_id, content, media_urls, deleted, create_time) VALUES
                                                                                                 (@user1_id, @pet28, '小金现在越来越可爱了，完全融入了我们的家庭！', '["https://example.com/checkin1-1.jpg","https://example.com/checkin1-2.jpg"]', 0, DATE_SUB(NOW(),INTERVAL 50 DAY)),
                                                                                                 (@user1_id, @pet28, '今天带小金去公园玩，它特别开心', '["https://example.com/checkin1-3.jpg"]', 0, DATE_SUB(NOW(),INTERVAL 30 DAY)),
                                                                                                 (@user1_id, @pet28, '小金现在很健康，体重也正常', NULL, 0, DATE_SUB(NOW(),INTERVAL 10 DAY)),
                                                                                                 (@user2_id, @pet29, '小银刚到家有点害羞，现在已经很亲人了', '["https://example.com/checkin2-1.jpg"]', 0, DATE_SUB(NOW(),INTERVAL 20 DAY)),
                                                                                                 (@user2_id, @pet29, '小银越来越漂亮了，性格也很好', '["https://example.com/checkin2-2.jpg"]', 0, DATE_SUB(NOW(),INTERVAL 5 DAY)),
                                                                                                 (@user3_id, @pet30, '小铜刚到新家，还在适应中', '["https://example.com/checkin3-1.jpg"]', 0, DATE_SUB(NOW(),INTERVAL 10 DAY));

-- ============================================================
-- 9. 信用账户（主键 user_id，重复则更新）
-- ============================================================
INSERT INTO credit_account (user_id, score, level, last_calc_time, update_time) VALUES
                                                                                    (@user1_id,85,2,NOW(),NOW()),
                                                                                    (@user2_id,92,3,NOW(),NOW()),
                                                                                    (@user3_id,78,2,NOW(),NOW()),
                                                                                    (@user4_id,88,2,NOW(),NOW()),
                                                                                    (@user5_id,95,3,NOW(),NOW())
ON DUPLICATE KEY UPDATE
                     score=VALUES(score),
                     level=VALUES(level),
                     last_calc_time=VALUES(last_calc_time),
                     update_time=NOW();

-- ============================================================
-- 10. 用户收藏（uk_user_pet，重复则忽略）
-- ============================================================
INSERT IGNORE INTO user_favorite (user_id, pet_id, deleted, create_time) VALUES
                                                                             (@user1_id, @pet1, 0, DATE_SUB(NOW(),INTERVAL 5 DAY)),
                                                                             (@user1_id, @pet3, 0, DATE_SUB(NOW(),INTERVAL 3 DAY)),
                                                                             (@user2_id, @pet2, 0, DATE_SUB(NOW(),INTERVAL 4 DAY)),
                                                                             (@user2_id, @pet4, 0, DATE_SUB(NOW(),INTERVAL 2 DAY)),
                                                                             (@user3_id, @pet1, 0, DATE_SUB(NOW(),INTERVAL 6 DAY)),
                                                                             (@user3_id, @pet5, 0, DATE_SUB(NOW(),INTERVAL 1 DAY)),
                                                                             (@user4_id, @pet3, 0, DATE_SUB(NOW(),INTERVAL 7 DAY)),
                                                                             (@user5_id, @pet2, 0, DATE_SUB(NOW(),INTERVAL 5 DAY));

-- ============================================================
-- 11. 用户行为（可重复插入会产生多条，通常允许；如你要幂等可自己加唯一键）
-- ============================================================
INSERT INTO user_behavior (user_id, pet_id, behavior_type, weight, create_time) VALUES
                                                                                    (@user1_id, @pet1, 'VIEW', 1, DATE_SUB(NOW(),INTERVAL 5 DAY)),
                                                                                    (@user1_id, @pet1, 'FAVORITE', 3, DATE_SUB(NOW(),INTERVAL 5 DAY)),
                                                                                    (@user1_id, @pet3, 'VIEW', 1, DATE_SUB(NOW(),INTERVAL 3 DAY)),
                                                                                    (@user2_id, @pet2, 'VIEW', 1, DATE_SUB(NOW(),INTERVAL 4 DAY)),
                                                                                    (@user2_id, @pet2, 'FAVORITE', 3, DATE_SUB(NOW(),INTERVAL 4 DAY)),
                                                                                    (@user2_id, @pet4, 'VIEW', 1, DATE_SUB(NOW(),INTERVAL 2 DAY)),
                                                                                    (@user3_id, @pet1, 'VIEW', 1, DATE_SUB(NOW(),INTERVAL 6 DAY)),
                                                                                    (@user3_id, @pet5, 'FAVORITE', 3, DATE_SUB(NOW(),INTERVAL 1 DAY));

-- ============================================================
-- 12. 申请流转日志（必须先拿到 application_id）
-- ============================================================
SET @app1 = (SELECT id FROM adoption_application WHERE user_id=@user1_id AND pet_id=@pet1 LIMIT 1);
SET @app2 = (SELECT id FROM adoption_application WHERE user_id=@user2_id AND pet_id=@pet2 LIMIT 1);
SET @app9 = (SELECT id FROM adoption_application WHERE user_id=@user4_id AND pet_id=@pet9 LIMIT 1);

INSERT INTO adoption_flow_log (application_id, from_status, to_status, operator_id, remark, create_time) VALUES
                                                                                                             (@app1, NULL, 'SUBMITTED', @org_user_id, '用户提交申请', DATE_SUB(NOW(),INTERVAL 1 DAY)),
                                                                                                             (@app2, 'SUBMITTED', 'UNDER_REVIEW', @org_user_id, '机构开始审核', DATE_SUB(NOW(),INTERVAL 2 DAY)),
                                                                                                             (@app9, 'SUBMITTED', 'UNDER_REVIEW', @org_user_id, '机构开始审核', DATE_SUB(NOW(),INTERVAL 4 DAY)),
                                                                                                             (@app9, 'UNDER_REVIEW', 'INTERVIEW', @org_user_id, '安排面谈', DATE_SUB(NOW(),INTERVAL 3 DAY)),
                                                                                                             (@app9, 'INTERVIEW', 'APPROVED', @org_user_id, '审核通过', DATE_SUB(NOW(),INTERVAL 1 DAY));
