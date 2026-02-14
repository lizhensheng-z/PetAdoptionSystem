package com.yr.pet.adoption.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yr.pet.adoption.common.PageResult;
import com.yr.pet.adoption.exception.BizException;
import com.yr.pet.adoption.exception.ErrorCode;
import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.model.entity.UserEntity;
import com.yr.pet.adoption.mapper.UserMapper;
import com.yr.pet.adoption.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

    @Override
    public UserEntity findByUsername(String username) {
        return lambdaQuery()
                .eq(UserEntity::getUsername, username)
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
        return user;
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

        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
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
}
