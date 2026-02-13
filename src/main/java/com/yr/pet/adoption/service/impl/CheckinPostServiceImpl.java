package com.yr.pet.adoption.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yr.pet.adoption.common.PageResult;
import com.yr.pet.adoption.model.entity.*;
import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.mapper.*;
import com.yr.pet.adoption.service.CheckinPostService;
import com.yr.pet.adoption.service.CreditAccountService;
import com.yr.pet.adoption.exception.BizException;
import com.yr.pet.adoption.exception.ErrorCode;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 领养后打卡表 服务实现类
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Service
public class CheckinPostServiceImpl extends ServiceImpl<CheckinPostMapper, CheckinPostEntity> implements CheckinPostService {

    @Autowired
    private PetMapper petMapper;
    @Autowired
    private AdoptionApplicationMapper adoptionApplicationMapper;
    @Autowired
    private OrgProfileMapper orgProfileMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CreditAccountMapper creditAccountMapper;
    @Autowired
    private CreditLogMapper creditLogMapper;
    @Autowired
    private ConfigMapper configMapper;
    @Autowired
    private CreditAccountService creditAccountService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CheckinResponse createCheckin(Long userId, CheckinCreateRequest request) {
        // 1. 验证用户是否是该宠物的领养人
        LambdaQueryWrapper<AdoptionApplicationEntity> appQuery = new LambdaQueryWrapper<AdoptionApplicationEntity>()
                .eq(AdoptionApplicationEntity::getPetId, request.getPetId())
                .eq(AdoptionApplicationEntity::getUserId, userId)
                .eq(AdoptionApplicationEntity::getStatus, "APPROVED");
        
        AdoptionApplicationEntity adoption = adoptionApplicationMapper.selectOne(appQuery);
        if (adoption == null) {
            throw new BizException(ErrorCode.OPERATION_NOT_ALLOWED, "您不是该宠物的领养人，无法打卡");
        }

        // 2. 检查每日打卡次数上限
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);
        
        LambdaQueryWrapper<CheckinPostEntity> checkinQuery = new LambdaQueryWrapper<CheckinPostEntity>()
                .eq(CheckinPostEntity::getUserId, userId)
                .eq(CheckinPostEntity::getPetId, request.getPetId())
                .between(CheckinPostEntity::getCreateTime, todayStart, todayEnd);
        
        long todayCount = this.count(checkinQuery);
        int dailyMax = getConfigValue("credit.checkin.dailyMax", 1);
        if (todayCount >= dailyMax) {
            throw new BizException(ErrorCode.OPERATION_NOT_ALLOWED, "今日打卡次数已达上限");
        }

        // 3. 计算信用分
        boolean hasMedia = request.getMediaUrls() != null && !request.getMediaUrls().isEmpty();
        int creditScore = creditAccountService.calculateCheckinScore(request.getContent(), hasMedia);
        
        // 4. 创建打卡记录
        CheckinPostEntity entity = new CheckinPostEntity();
        entity.setUserId(userId);
        entity.setPetId(request.getPetId());
        entity.setContent(request.getContent());
        
        try {
            if (hasMedia) {
                entity.setMediaUrls(objectMapper.writeValueAsString(request.getMediaUrls()));
            }
        } catch (JsonProcessingException e) {
            throw new BizException(ErrorCode.SYSTEM_ERROR, "打卡数据处理失败");
        }
        
        this.save(entity);

        // 5. 更新信用账户
        CreditAccountEntity creditAccount = creditAccountMapper.selectById(userId);
        int beforeScore = creditAccount != null ? creditAccount.getScore() : 0;
        int afterScore = beforeScore + creditScore;
        
        if (creditAccount == null) {
            creditAccount = new CreditAccountEntity();
            creditAccount.setUserId(userId);
            creditAccount.setScore(afterScore);
            creditAccount.setLevel(calculateLevel(afterScore));
            creditAccountMapper.insert(creditAccount);
        } else {
            creditAccount.setScore(afterScore);
            creditAccount.setLevel(calculateLevel(afterScore));
            creditAccountMapper.updateById(creditAccount);
        }

        // 6. 记录信用流水
        CreditLogEntity creditLog = new CreditLogEntity();
        creditLog.setUserId(userId);
        creditLog.setDelta(creditScore);
        creditLog.setReason("CHECKIN");
        creditLog.setRefType("checkin");
        creditLog.setRefId(entity.getId());
        creditLogMapper.insert(creditLog);

        // 7. 返回响应
        CheckinResponse response = new CheckinResponse();
        response.setId(entity.getId());
        response.setPetId(entity.getPetId());
        response.setUserId(userId);
        response.setCreateTime(entity.getCreateTime());
        response.setCreditDelta(creditScore);
        response.setCreditReason(buildCreditReason(request.getContent(), hasMedia));
        response.setCreditScore(afterScore);
        
        return response;
    }

    @Override
    public PageResult<CheckinListItem> getMyCheckins(Long userId, Long petId, Integer pageNo, Integer pageSize) {
        LambdaQueryWrapper<CheckinPostEntity> query = new LambdaQueryWrapper<CheckinPostEntity>()
                .eq(CheckinPostEntity::getUserId, userId);
        
        if (petId != null) {
            query.eq(CheckinPostEntity::getPetId, petId);
        }
        
        query.orderByDesc(CheckinPostEntity::getCreateTime);
        
        Page<CheckinPostEntity> page = new Page<>(pageNo, pageSize);
        Page<CheckinPostEntity> result = this.page(page, query);
        
        List<CheckinListItem> list = new ArrayList<>();
        List<Long> petIds = result.getRecords().stream()
                .map(CheckinPostEntity::getPetId)
                .distinct()
                .collect(Collectors.toList());
        
        // 批量查询宠物信息
        Map<Long, PetEntity> petMap = null;
        if (!petIds.isEmpty()) {
            List<PetEntity> pets = petMapper.selectBatchIds(petIds);
            petMap = pets.stream().collect(Collectors.toMap(PetEntity::getId, p -> p));
        }
        
        for (CheckinPostEntity entity : result.getRecords()) {
            CheckinListItem item = new CheckinListItem();
            item.setId(entity.getId());
            item.setPetId(entity.getPetId());
            item.setContent(entity.getContent());
            item.setCreateTime(entity.getCreateTime());
            
            if (petMap != null && petMap.containsKey(entity.getPetId())) {
                PetEntity pet = petMap.get(entity.getPetId());
                item.setPetName(pet.getName());
                item.setPetCoverUrl(pet.getCoverUrl());
            }
            
            // 解析mediaUrls
            if (StringUtils.hasText(entity.getMediaUrls())) {
                try {
                    List<String> urls = objectMapper.readValue(entity.getMediaUrls(), new TypeReference<List<String>>() {});
                    item.setMediaUrls(urls);
                    item.setMediaCount(urls.size());
                } catch (JsonProcessingException e) {
                    item.setMediaCount(0);
                }
            }
            
            list.add(item);
        }
        
        return new PageResult<>(list, pageNo, pageSize, result.getTotal(), 
                (int) Math.ceil((double) result.getTotal() / pageSize));
    }

    @Override
    public CheckinDetailResponse getCheckinDetail(Long userId, Long checkinId) {
        CheckinPostEntity entity = this.getById(checkinId);
        if (entity == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "打卡记录不存在");
        }
        
        CheckinDetailResponse response = new CheckinDetailResponse();
        response.setId(entity.getId());
        response.setPetId(entity.getPetId());
        response.setUserId(entity.getUserId());
        response.setContent(entity.getContent());
        response.setCreateTime(entity.getCreateTime());
        
        // 解析mediaUrls
        if (StringUtils.hasText(entity.getMediaUrls())) {
            try {
                List<String> urls = objectMapper.readValue(entity.getMediaUrls(), new TypeReference<List<String>>() {});
                response.setMediaUrls(urls);
            } catch (JsonProcessingException e) {
                response.setMediaUrls(List.of());
            }
        }
        
        // 获取宠物信息
        PetEntity pet = petMapper.selectById(entity.getPetId());
        if (pet != null) {
            CheckinDetailResponse.PetSimpleInfo petInfo = new CheckinDetailResponse.PetSimpleInfo();
            petInfo.setId(pet.getId());
            petInfo.setName(pet.getName());
            petInfo.setSpecies(pet.getSpecies());
            petInfo.setCoverUrl(pet.getCoverUrl());
            response.setPet(petInfo);
            
            // 获取机构信息
            OrgProfileEntity orgProfile = orgProfileMapper.selectOne(
                    new LambdaQueryWrapper<OrgProfileEntity>()
                            .eq(OrgProfileEntity::getUserId, pet.getOrgUserId()));
            if (orgProfile != null) {
                CheckinDetailResponse.OrgSimpleInfo orgInfo = new CheckinDetailResponse.OrgSimpleInfo();
                orgInfo.setId(orgProfile.getUserId());
                orgInfo.setOrgName(orgProfile.getOrgName());
                response.setOrg(orgInfo);
            }
        }
        
        // 获取用户信息
        UserEntity user = userMapper.selectById(entity.getUserId());
        if (user != null) {
            CheckinDetailResponse.UserSimpleInfo userInfo = new CheckinDetailResponse.UserSimpleInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            userInfo.setAvatar(user.getAvatar());
            response.setUser(userInfo);
        }
        
        // 权限判断
        Long currentUserId = userId;
        response.setCanEdit(currentUserId != null && currentUserId.equals(entity.getUserId()));
        response.setCanDelete(currentUserId != null && currentUserId.equals(entity.getUserId()));
        
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCheckin(Long userId, Long checkinId) {
        CheckinPostEntity entity = this.getById(checkinId);
        if (entity == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "打卡记录不存在");
        }
        
        if (!entity.getUserId().equals(userId)) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权限删除此打卡");
        }
        
        this.removeById(checkinId);
    }

    private int getConfigValue(String key, int defaultValue) {
        ConfigEntity config = configMapper.selectOne(
                new LambdaQueryWrapper<ConfigEntity>().eq(ConfigEntity::getConfigKey, key));
        if (config != null && StringUtils.hasText(config.getConfigValue())) {
            try {
                return Integer.parseInt(config.getConfigValue());
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private String buildCreditReason(String content, boolean hasMedia) {
        StringBuilder reason = new StringBuilder("打卡");
        if (StringUtils.hasText(content) && content.length() > 30) {
            reason.append("+内容");
        }
        if (hasMedia) {
            reason.append("+媒体");
        }
        return reason.toString();
    }

    private int calculateLevel(int score) {
        if (score >= 150) return 4;
        if (score >= 100) return 3;
        if (score >= 50) return 2;
        if (score >= 25) return 1;
        return 0;
    }
}
