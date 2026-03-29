package com.yr.pet.adoption.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yr.pet.adoption.common.PageResult;
import com.yr.pet.adoption.exception.BizException;
import com.yr.pet.adoption.exception.ErrorCode;
import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.mapper.AdoptionApplicationMapper;
import com.yr.pet.adoption.mapper.PetMapper;
import com.yr.pet.adoption.mapper.CreditAccountMapper;
import com.yr.pet.adoption.model.entity.*;
import com.yr.pet.adoption.mapper.UserMapper;
import com.yr.pet.adoption.mapper.OrgProfileMapper;
import com.yr.pet.adoption.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


import java.io.IOException;
import java.io.OutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统用户表（领养人/机构/管理员） 服务实现类
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final CreditAccountService creditAccountService;
    private final UserFavoriteService userFavoriteService;
    private final AdoptionApplicationService adoptionApplicationService;
    private final CheckinPostService checkinPostService;
    private final OrgProfileMapper orgProfileMapper;
    private final AdoptionApplicationMapper adoptionApplicationMapper;
    private final PetMapper petMapper;
    private final CreditAccountMapper creditAccountMapper;

    @Override
    public UserEntity findByUsername(String username) {
        return lambdaQuery()
                .eq(UserEntity::getUsername, username)
                .eq(UserEntity::getDeleted, 0)
                .one();
    }

    @Override
    public UserEntity findByPhone(String phone) {
        return lambdaQuery()
                .eq(UserEntity::getPhone, phone)
                .eq(UserEntity::getDeleted, 0)
                .one();
    }

    @Override
    public UserEntity findById(Long id) {
        return lambdaQuery()
                .eq(UserEntity::getId, id)
                .eq(UserEntity::getDeleted, 0)
                .one();
    }

    @Override
    @Transactional
    public UserEntity register(RegisterRequest registerRequest) {
        // 检查用户名是否已存在
        if (existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查手机号是否已存在
        if (registerRequest.getPhone() != null && existsByPhone(registerRequest.getPhone())) {
            throw new RuntimeException("手机号已存在");
        }
        
        // 检查邮箱是否已存在
        if (registerRequest.getEmail() != null && existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }

        UserEntity user = new UserEntity();
        user.setUsername(registerRequest.getUsername());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setPhone(registerRequest.getPhone());
        user.setEmail(registerRequest.getEmail());
        user.setRole(registerRequest.getRole());
        user.setStatus("NORMAL");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        save(user);

        // 创建用户信用账户，初始信用分为60分
        createCreditAccount(user.getId());

        // 如果是机构用户，自动创建机构资料
        if ("ORG".equals(registerRequest.getRole())) {
            createOrgProfile(user.getId());
        }

        return user;
    }

    /**
     * 创建机构资料
     * @param userId 用户ID
     */
    private void createOrgProfile(Long userId) {
        OrgProfileEntity orgProfile = new OrgProfileEntity();
        orgProfile.setUserId(userId);
        orgProfile.setOrgName(""); // 初始为空，需要后续完善
        orgProfile.setVerifyStatus("PENDING");
        orgProfile.setDeleted(0);
        orgProfile.setCreateTime(LocalDateTime.now());
        orgProfile.setUpdateTime(LocalDateTime.now());
        orgProfileMapper.insert(orgProfile);
    }

    /**
     * 创建用户信用账户
     * @param userId 用户ID
     */
    private void createCreditAccount(Long userId) {
        CreditAccountEntity creditAccount = new CreditAccountEntity();
        creditAccount.setUserId(userId);
        creditAccount.setScore(60); // 初始信用分60分
        creditAccount.setLevel(0);  // 初始等级为0（新注册）
        creditAccount.setLastCalcTime(LocalDateTime.now());
        creditAccount.setUpdateTime(LocalDateTime.now());
        creditAccountMapper.insert(creditAccount);
    }

    @Override
    public boolean existsByUsername(String username) {
        return lambdaQuery()
                .eq(UserEntity::getUsername, username)
                .eq(UserEntity::getDeleted, 0)
                .count() > 0;
    }

    @Override
    public boolean existsByPhone(String phone) {
        return lambdaQuery()
                .eq(UserEntity::getPhone, phone)
                .eq(UserEntity::getDeleted, 0)
                .count() > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        return lambdaQuery()
                .eq(UserEntity::getEmail, email)
                .eq(UserEntity::getDeleted, 0)
                .count() > 0;
    }

    @Override
    public void updateLastLoginTime(Long userId) {
        lambdaUpdate()
                .eq(UserEntity::getId, userId)
                .set(UserEntity::getLastLoginTime, LocalDateTime.now())
                .update();
    }

    @Override
    public void updateUserProfile(Long userId, UserProfileUpdateRequest request) {
        lambdaUpdate()
                .eq(UserEntity::getId, userId)
                .set(request.getAvatar() != null, UserEntity::getAvatar, request.getAvatar())
                .set(request.getPhone() != null, UserEntity::getPhone, request.getPhone())
                .set(request.getEmail() != null, UserEntity::getEmail, request.getEmail())
                .set(request.getPreference() != null, UserEntity::getPreferenceJson, convertPreferenceToJson(request.getPreference()))
                .set(UserEntity::getUpdateTime, LocalDateTime.now())
                .update();
    }

    @Override
    public void addTokenToBlacklist(String username) {
        String key = "token:blacklist:" + username;
        redisTemplate.opsForValue().set(key, "true", 1, TimeUnit.HOURS);
    }

    @Override
    public UserDetailResponse getUserDetail(Long userId) {
        UserEntity user = findById(userId);
        if (user == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "用户不存在");
        }

        UserDetailResponse response = new UserDetailResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getUsername());
        response.setAvatar(user.getAvatar());
        response.setRole(user.getRole());
        response.setPhone(user.getPhone());
        response.setEmail(user.getEmail());
        response.setCreateTime(user.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        CreditInfoResponse creditInfo = creditAccountService.getCreditInfo(userId);
        if (creditInfo != null) {
            response.setCreditScore(creditInfo.getScore());
            response.setCreditLevel(getCreditLevelName(creditInfo.getLevel()));
            response.setCreditChange(calculateCreditChange(userId));
        }

        response.setMedals(getUserMedals(userId));
        response.setStats(getUserStats(userId));

        return response;
    }

    @Override
    public UserStatsResponse getUserStats(Long userId) {
        UserStatsResponse stats = new UserStatsResponse();

        stats.setApplications((int) adoptionApplicationService.getMyApplications(userId, null, 1, 1, "create_time", "desc").getTotal());
        stats.setFavorites((userFavoriteService.getMyFavorites(userId, 1, 1).getTotal().intValue()));
        stats.setCheckins(checkinPostService.getMyCheckins(userId, null, 1, 1).getTotal().intValue());
        stats.setAdoptions((int) adoptionApplicationService.getMyApplications(userId, "APPROVED", 1, 1, "create_time", "desc").getTotal());
        stats.setPendingApplications((int) adoptionApplicationService.getMyApplications(userId, "SUBMITTED", 1, 1, "create_time", "desc").getTotal());

        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        stats.setMonthlyCheckins(checkinPostService.getMyCheckins(userId, null, 1, 1000).getTotal().intValue());

        return stats;
    }

    @Override
    public UserPreferenceResponse getUserPreference(Long userId) {
        UserEntity user = findById(userId);
        if (user == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "用户不存在");
        }

        UserPreferenceResponse response = new UserPreferenceResponse();

        if (user.getPreferenceJson() != null) {
            try {
                Map<String, Object> preference = objectMapper.readValue(user.getPreferenceJson(), Map.class);
                response.setPetTypes((List<String>) preference.get("petTypes"));
                response.setAgeRange((List<Integer>) preference.get("ageRange"));
                response.setGender((String) preference.get("gender"));
                response.setTags((List<String>) preference.get("tags"));
                response.setDistance((Integer) preference.get("distance"));
                response.setSizes((List<String>) preference.get("sizes"));
                response.setHealthRequirements((List<String>) preference.get("healthRequirements"));
            } catch (JsonProcessingException e) {
                throw new BizException(ErrorCode.SYSTEM_ERROR, "用户偏好解析失败");
            }
        }

        return response;
    }

    @Override
    public void updateUserPreference(Long userId, UserPreferenceRequest request) {
        UserEntity user = findById(userId);
        if (user == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "用户不存在");
        }

        Map<String, Object> preference = new HashMap<>();
        preference.put("petTypes", request.getPetTypes());
        preference.put("ageRange", request.getAgeRange());
        preference.put("gender", request.getGender());
        preference.put("tags", request.getTags());
        preference.put("distance", request.getDistance());
        preference.put("sizes", request.getSizes());
        preference.put("healthRequirements", request.getHealthRequirements());

        try {
            String preferenceJson = objectMapper.writeValueAsString(preference);
            lambdaUpdate()
                    .eq(UserEntity::getId, userId)
                    .set(UserEntity::getPreferenceJson, preferenceJson)
                    .set(UserEntity::getUpdateTime, LocalDateTime.now())
                    .update();
        } catch (JsonProcessingException e) {
            throw new BizException(ErrorCode.SYSTEM_ERROR, "用户偏好设置失败");
        }
    }

    @Override
    public PageResult<UserAdminResponse> getUserList(UserListRequest request) {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        
        // 搜索条件
        if (StringUtils.hasText(request.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper
                    .like(UserEntity::getUsername, request.getKeyword())
                    .or()
                    .like(UserEntity::getPhone, request.getKeyword())
                    .or()
                    .like(UserEntity::getEmail, request.getKeyword()));
        }
        
        // 角色筛选
        if (StringUtils.hasText(request.getRole())) {
            queryWrapper.eq(UserEntity::getRole, request.getRole());
        }
        
        // 状态筛选
        if (StringUtils.hasText(request.getStatus())) {
            queryWrapper.eq(UserEntity::getStatus, request.getStatus());
        }
        
        // 创建时间范围
        if (StringUtils.hasText(request.getStartDate())) {
            queryWrapper.ge(UserEntity::getCreateTime, LocalDateTime.parse(request.getStartDate() + "T00:00:00"));
        }
        if (StringUtils.hasText(request.getEndDate())) {
            queryWrapper.le(UserEntity::getCreateTime, LocalDateTime.parse(request.getEndDate() + "T23:59:59"));
        }
        
        // 排序
        String sortBy = request.getSortBy();
        String order = request.getOrder();
        if ("asc".equalsIgnoreCase(order)) {
            queryWrapper.orderByAsc(UserEntity::getCreateTime);
        } else {
            queryWrapper.orderByDesc(UserEntity::getCreateTime);
        }
        
        // 排除已删除的用户
        queryWrapper.eq(UserEntity::getDeleted, 0);
        
        // 分页查询
        Page<UserEntity> page = new Page<>(request.getPage(), request.getPageSize());
        IPage<UserEntity> userPage = page(page, queryWrapper);
        
        // 转换结果
        List<UserAdminResponse> userList = userPage.getRecords().stream()
                .map(this::convertToAdminResponse)
                .collect(Collectors.toList());
        
        return new PageResult<>(
                userList,
                (int) userPage.getCurrent(),
                (int) userPage.getSize(),
                 userPage.getTotal(),
                (int) userPage.getPages()
        );
    }

    @Override
    public UserDetailAdminResponse getUserDetails(Long userId) {
        UserEntity user = findById(userId);
        if (user == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        return convertToDetailAdminResponse(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(Long userId, UserUpdateRequest request) {
        UserEntity user = findById(userId);
        if (user == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        
        // 检查手机号是否已存在（排除当前用户）
        if (StringUtils.hasText(request.getPhone()) && !request.getPhone().equals(user.getPhone())) {
            if (existsByPhone(request.getPhone())) {
                throw new BizException(ErrorCode.RESOURCE_EXIST, "手机号已存在");
            }
        }
        
        // 检查邮箱是否已存在（排除当前用户）
        if (StringUtils.hasText(request.getEmail()) && !request.getEmail().equals(user.getEmail())) {
            if (existsByEmail(request.getEmail())) {
                throw new BizException(ErrorCode.RESOURCE_EXIST, "邮箱已存在");
            }
        }
        
        lambdaUpdate()
                .eq(UserEntity::getId, userId)
                .set(StringUtils.hasText(request.getPhone()), UserEntity::getPhone, request.getPhone())
                .set(StringUtils.hasText(request.getEmail()), UserEntity::getEmail, request.getEmail())
                .set(StringUtils.hasText(request.getRole()), UserEntity::getRole, request.getRole())
                .set(StringUtils.hasText(request.getAvatar()), UserEntity::getAvatar, request.getAvatar())
                .set(StringUtils.hasText(request.getStatus()), UserEntity::getStatus, request.getStatus())
                .set(UserEntity::getUpdateTime, LocalDateTime.now())
                .update();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateUserStatus(List<Long> userIds, String status) {
        if (userIds == null || userIds.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "用户ID列表不能为空");
        }
        
        if (!"NORMAL".equals(status) && !"BANNED".equals(status)) {
            throw new BizException(ErrorCode.PARAM_ERROR, "状态值无效");
        }
        
        lambdaUpdate()
                .in(UserEntity::getId, userIds)
                .set(UserEntity::getStatus, status)
                .set(UserEntity::getUpdateTime, LocalDateTime.now())
                .update();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteUsers(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "用户ID列表不能为空");
        }
        
        lambdaUpdate()
                .in(UserEntity::getId, userIds)
                .set(UserEntity::getDeleted, 1)
                .set(UserEntity::getUpdateTime, LocalDateTime.now())
                .update();
    }

    @Override
    public void exportUsers(UserListRequest request, HttpServletResponse response) {
        // 设置不分页，获取所有数据
        request.setPage(1);
        request.setPageSize(10000);
        PageResult<UserAdminResponse> result = getUserList(request);
        
        // 这里简化处理，实际应该使用Excel导出库如EasyExcel
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("用户ID,用户名,手机号,邮箱,角色,状态,创建时间,最后登录时间\n");
            
            for (UserAdminResponse user : result.getList()) {
                sb.append(user.getId()).append(",")
                  .append(user.getUsername()).append(",")
                  .append(user.getPhone()).append(",")
                  .append(user.getEmail()).append(",")
                  .append(user.getRoleName()).append(",")
                  .append(user.getStatusName()).append(",")
                  .append(user.getCreateTime()).append(",")
                  .append(user.getLastLoginTime() != null ? user.getLastLoginTime() : "")
                  .append("\n");
            }
            
            byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
            OutputStream os = response.getOutputStream();
            os.write(new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF}); // UTF-8 BOM
            os.write(bytes);
            os.flush();
        } catch (IOException e) {
            throw new BizException(ErrorCode.SYSTEM_ERROR, "导出失败");
        }
    }

    private UserAdminResponse convertToAdminResponse(UserEntity user) {
        UserAdminResponse response = new UserAdminResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setPhone(user.getPhone());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setAvatar(user.getAvatar());
        response.setStatus(user.getStatus());
        response.setLastLoginTime(user.getLastLoginTime());
        response.setCreateTime(user.getCreateTime());
        response.setUpdateTime(user.getUpdateTime());
        return response;
    }

    private UserDetailAdminResponse convertToDetailAdminResponse(UserEntity user) {
        UserDetailAdminResponse response = new UserDetailAdminResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setPhone(user.getPhone());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setAvatar(user.getAvatar());
        response.setStatus(user.getStatus());
        response.setPreferenceJson(user.getPreferenceJson());
        response.setLastLoginTime(user.getLastLoginTime());
        response.setCreateTime(user.getCreateTime());
        response.setUpdateTime(user.getUpdateTime());
        return response;
    }

    private String getCreditLevelName(Integer level) {
        if (level == null) {
            return "新注册";
        }
        switch (level) {
            case 0:
                return "新注册";
            case 1:
                return "铜牌";
            case 2:
                return "银牌";
            case 3:
                return "金牌";
            case 4:
                return "铂金";
            default:
                return "新注册";
        }
    }

    private Integer calculateCreditChange(Long userId) {
        PageResult<CreditLogItem> logs = creditAccountService.getCreditLogs(userId, null, 1, 10);
        if (logs.getList() == null || logs.getList().isEmpty()) {
            return 0;
        }
        return logs.getList().stream()
                .mapToInt(CreditLogItem::getDelta)
                .sum();
    }

    private List<String> getUserMedals(Long userId) {
        List<String> medals = new ArrayList<>();
        CreditInfoResponse creditInfo = creditAccountService.getCreditInfo(userId);
        if (creditInfo != null && creditInfo.getLevel() != null) {
            if (creditInfo.getLevel() >= 1) {
                medals.add("guardian");
            }
            if (creditInfo.getLevel() >= 2) {
                medals.add("adopter");
            }
            if (creditInfo.getLevel() >= 3) {
                medals.add("verified");
            }
        }
        return medals;
    }

    private String convertPreferenceToJson(UserProfileUpdateRequest.UserPreference preference) {
        if (preference == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(preference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("用户偏好设置转换失败", e);
        }
    }

    @Override
    public List<UserAdoptedPetResponse> getUserAdoptedPets(Long userId) {
        // 查询用户已领养成功的申请
        List<AdoptionApplicationEntity> applications = adoptionApplicationMapper.selectList(
            new LambdaQueryWrapper<AdoptionApplicationEntity>()
                .eq(AdoptionApplicationEntity::getUserId, userId)
                .eq(AdoptionApplicationEntity::getStatus, "APPROVED")
                .eq(AdoptionApplicationEntity::getDeleted, 0)
                .orderByDesc(AdoptionApplicationEntity::getCreateTime)
        );

        if (applications.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取宠物ID列表
        List<Long> petIds = applications.stream()
            .map(AdoptionApplicationEntity::getPetId)
            .collect(Collectors.toList());

        // 查询宠物信息
        List<PetEntity> pets = petMapper.selectList(
            new LambdaQueryWrapper<PetEntity>()
                .in(PetEntity::getId, petIds)
                .eq(PetEntity::getStatus, "ADOPTED")
                .eq(PetEntity::getDeleted, 0)
        );

        // 转换为响应DTO
        return pets.stream()
            .map(pet -> new UserAdoptedPetResponse(
                pet.getId(),
                pet.getName(),
                pet.getCoverUrl()
            ))
            .collect(Collectors.toList());
    }

    @Override
    public UserCreditSummaryResponse getUserCreditSummary(Long userId) {
        // 获取信用账户信息
        CreditAccountEntity creditAccount = creditAccountMapper.selectById(userId);
        if (creditAccount == null) {
            // 如果用户没有信用账户，创建默认账户
            creditAccount = new CreditAccountEntity();
            creditAccount.setUserId(userId);
            creditAccount.setScore(600); // 默认信用分
            creditAccount.setLevel(1);
            creditAccount.setLastCalcTime(LocalDateTime.now());
            creditAccount.setUpdateTime(LocalDateTime.now());
            creditAccountMapper.insert(creditAccount);
        }

        // 获取总打卡数
        int totalCheckins = checkinPostService.getMyCheckins(userId, null, 1, 1).getTotal().intValue();

        // 计算信用等级信息
        String levelName = getCreditLevelName(creditAccount.getLevel());
        String levelIcon = getCreditLevelIcon(creditAccount.getLevel());
        int nextLevelThreshold = getNextLevelThreshold(creditAccount.getLevel());
        String ranking = calculateRanking(creditAccount.getScore());

        return new UserCreditSummaryResponse(
            creditAccount.getScore(),
            levelName,
            levelIcon,
            totalCheckins,
            nextLevelThreshold,
            ranking
        );
    }

    private String getCreditLevelIcon(Integer level) {
        if (level == null) {
            return "shield-outline";
        }
        switch (level) {
            case 1:
                return "shield-outline";
            case 2:
                return "shield-half";
            case 3:
                return "shield-check";
            case 4:
                return "shield-star";
            default:
                return "shield-outline";
        }
    }

    private int getNextLevelThreshold(Integer level) {
        if (level == null) {
            return 700;
        }
        switch (level) {
            case 1:
                return 700;
            case 2:
                return 800;
            case 3:
                return 900;
            case 4:
                return 1000;
            default:
                return 700;
        }
    }

    private String calculateRanking(Integer score) {
        if (score == null) {
            return "Top 50%";
        }

        // 简化的排名计算逻辑
        if (score >= 900) {
            return "Top 5%";
        } else if (score >= 800) {
            return "Top 15%";
        } else if (score >= 700) {
            return "Top 30%";
        } else {
            return "Top 50%";
        }
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        // 查询用户
        UserEntity user = findById(userId);
        if (user == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "用户不存在");
        }

        // 验证原密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new BizException(ErrorCode.PARAM_ERROR, "原密码错误");
        }

        // 新密码不能与原密码相同
        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new BizException(ErrorCode.PARAM_ERROR, "新密码不能与原密码相同");
        }

        // 更新密码
        String newPasswordHash = passwordEncoder.encode(request.getNewPassword());
        lambdaUpdate()
                .eq(UserEntity::getId, userId)
                .set(UserEntity::getPasswordHash, newPasswordHash)
                .set(UserEntity::getUpdateTime, LocalDateTime.now())
                .update();
    }
}