package com.yr.pet.adoption.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yr.pet.adoption.common.PageResult;
import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.model.entity.NoticeEntity;
import com.yr.pet.adoption.model.entity.TagEntity;
import com.yr.pet.adoption.mapper.NoticeMapper;
import com.yr.pet.adoption.mapper.TagMapper;
import com.yr.pet.adoption.service.PublicService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 公共接口服务实现类
 */
@Service
@RequiredArgsConstructor
public class PublicServiceImpl implements PublicService {
    
    private final NoticeMapper noticeMapper;
    private final TagMapper tagMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    
    @Override
    public SystemConfigResponse getSystemConfig() {
        SystemConfigResponse config = new SystemConfigResponse();
        
        // 信用等级配置
        config.setCreditLevels(Arrays.asList(
            createCreditLevel(0, 0, 24, "新注册", "https://example.com/icons/credit-new.png"),
            createCreditLevel(1, 25, 49, "铜牌", "https://example.com/icons/credit-bronze.png"),
            createCreditLevel(2, 50, 99, "银牌", "https://example.com/icons/credit-silver.png"),
            createCreditLevel(3, 100, 149, "金牌", "https://example.com/icons/credit-gold.png"),
            createCreditLevel(4, 150, 10000, "铂金", "https://example.com/icons/credit-platinum.png")
        ));
        
        // 宠物种类
        config.setPetSpecies(Arrays.asList(
            createDictItem("CAT", "猫咪"),
            createDictItem("DOG", "狗狗"),
            createDictItem("OTHER", "其他")
        ));
        
        // 宠物品种
        Map<String, List<String>> petBreeds = new HashMap<>();
        petBreeds.put("CAT", Arrays.asList("英短", "美短", "加菲", "暹罗", "其他"));
        petBreeds.put("DOG", Arrays.asList("泰迪", "金毛", "拉布拉多", "萨摩耶", "其他"));
        config.setPetBreeds(petBreeds);
        
        // 宠物体型
        config.setPetSizes(Arrays.asList(
            createDictItem("S", "小（<3kg）"),
            createDictItem("M", "中（3-10kg）"),
            createDictItem("L", "大（>10kg）")
        ));
        
        // 性别选项
        config.setGenders(Arrays.asList(
            createDictItem("MALE", "公"),
            createDictItem("FEMALE", "母"),
            createDictItem("UNKNOWN", "未知")
        ));
        
        // 申请状态
        config.setApplicationStatuses(Arrays.asList(
            createApplicationStatus("SUBMITTED", "已提交", 1),
            createApplicationStatus("UNDER_REVIEW", "审核中", 2),
            createApplicationStatus("INTERVIEW", "面谈中", 3),
            createApplicationStatus("HOME_VISIT", "家访中", 4),
            createApplicationStatus("APPROVED", "已通过", 5),
            createApplicationStatus("REJECTED", "已拒绝", 6),
            createApplicationStatus("CANCELLED", "已取消", 7)
        ));
        
        // 标签分类
        config.setTagCategories(Arrays.asList(
            createTagCategory("PERSONALITY", "性格"),
            createTagCategory("HEALTH", "健康"),
            createTagCategory("FEATURE", "特征"),
            createTagCategory("SPECIES", "物种")
        ));
        
        // 省份列表
        config.setProvinces(Arrays.asList(
            "北京", "上海", "广东", "浙江", "江苏", "山东", "河南", "河北", "四川", "湖北",
            "湖南", "福建", "安徽", "辽宁", "陕西", "江西", "重庆", "云南", "贵州", "广西"
        ));
        
        return config;
    }
    
    @Override
    public PageResult<NoticeResponse> getNotices(Integer pageNo, Integer pageSize) {
        Page<NoticeEntity> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<NoticeEntity> wrapper = new LambdaQueryWrapper<NoticeEntity>()
                .eq(NoticeEntity::getStatus, "PUBLISHED")
                .orderByDesc(NoticeEntity::getCreateTime);
        
        IPage<NoticeEntity> noticePage = noticeMapper.selectPage(page, wrapper);
        
        List<NoticeResponse> notices = noticePage.getRecords().stream()
                .map(this::convertToNoticeResponse)
                .collect(Collectors.toList());
        
        PageResult<NoticeResponse> result = new PageResult<>();
        result.setList(notices);
        result.setPageNo(pageNo);
        result.setPageSize(pageSize);
        result.setTotal(noticePage.getTotal());
        result.setTotalPages((int) noticePage.getPages());
        
        return result;
    }
    
    @Override
    public TagResponse getTags(String type, Boolean enabled) {
        LambdaQueryWrapper<TagEntity> wrapper = new LambdaQueryWrapper<>();
        
        if (type != null) {
            wrapper.eq(TagEntity::getTagType, type);
        }
        
        if (enabled != null) {
            wrapper.eq(TagEntity::getEnabled, enabled);
        }
        
        wrapper.orderByAsc(TagEntity::getId);
        
        List<TagEntity> tags = tagMapper.selectList(wrapper);
        
        TagResponse response = new TagResponse();
        Map<String, List<TagResponse.TagItem>> tagMap = tags.stream()
                .collect(Collectors.groupingBy(
                        TagEntity::getTagType,
                        Collectors.mapping(this::convertToTagItem, Collectors.toList())
                ));
        
        List<TagResponse.TagGroup> tagGroups = tagMap.entrySet().stream()
                .map(entry -> {
                    TagResponse.TagGroup group = new TagResponse.TagGroup();
                    group.setType(entry.getKey());
                    group.setItems(entry.getValue());
                    return group;
                })
                .collect(Collectors.toList());
        
        response.setTags(tagGroups);
        return response;
    }
    
    @Override
    public CaptchaResponse getCaptcha(Long timestamp) {
        // 生成随机验证码
        String captcha = generateRandomCaptcha();
        String captchaId = UUID.randomUUID().toString();
        
        // 存储到Redis，有效期5分钟
        String key = "captcha:" + captchaId;
        redisTemplate.opsForValue().set(key, captcha, 5, TimeUnit.MINUTES);
        
        // 生成验证码图片（这里简化处理，实际应该生成图片）
        String base64Image = generateCaptchaImage(captcha);
        
        CaptchaResponse response = new CaptchaResponse();
        response.setCaptchaId(captchaId);
        response.setImage(base64Image);
        response.setExpiresIn(300);
        
        return response;
    }
    
    @Override
    public void sendCode(SendCodeRequest request) {
        // 验证图片验证码
        String captchaKey = "captcha:" + request.getCaptchaId();
        String storedCaptcha = (String) redisTemplate.opsForValue().get(captchaKey);
        
        if (storedCaptcha == null || !storedCaptcha.equalsIgnoreCase(request.getCaptchaAnswer())) {
            throw new RuntimeException("验证码错误或已过期");
        }
        
        // 删除已使用的验证码
        redisTemplate.delete(captchaKey);
        
        // 生成6位数字验证码
        String code = String.format("%06d", new Random().nextInt(999999));
        
        // 存储验证码到Redis，有效期10分钟
        String key = "verify_code:" + (request.getPhone() != null ? request.getPhone() : request.getEmail()) + ":" + request.getType();
        redisTemplate.opsForValue().set(key, code, 10, TimeUnit.MINUTES);
        
        // 这里应该调用短信或邮件服务发送验证码
        // 简化处理，实际项目中需要集成短信/邮件服务商
        System.out.println("发送验证码到: " + (request.getPhone() != null ? request.getPhone() : request.getEmail()) + ", 验证码: " + code);
    }
    
    @Override
    public VerifyCodeResponse verifyCode(VerifyCodeRequest request) {
        String key = "verify_code:" + (request.getPhone() != null ? request.getPhone() : request.getEmail()) + ":verify";
        String storedCode = (String) redisTemplate.opsForValue().get(key);
        
        if (storedCode == null || !storedCode.equals(request.getCode())) {
            throw new RuntimeException("验证码错误或已过期");
        }
        
        // 删除已使用的验证码
        redisTemplate.delete(key);
        
        // 生成临时token
        String tempToken = "temp_token_" + UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("temp_token:" + tempToken, "valid", 30, TimeUnit.MINUTES);
        
        VerifyCodeResponse response = new VerifyCodeResponse();
        response.setToken(tempToken);
        response.setExpiresIn(1800);
        
        return response;
    }
    
    @Override
    public RegionResponse getRegions(Integer level, String parentCode) {
        // 这里应该从数据库或缓存中获取省市区数据
        // 简化处理，返回示例数据
        RegionResponse response = new RegionResponse();
        
        List<RegionResponse.Region> regions = new ArrayList<>();
        
        if (level == null || level == 1) {
            // 返回省份
            regions.add(createRegion("110000", "北京市", 1, null));
            regions.add(createRegion("120000", "天津市", 1, null));
            regions.add(createRegion("310000", "上海市", 1, null));
            regions.add(createRegion("440000", "广东省", 1, null));
        } else if (level == 2 && "110000".equals(parentCode)) {
            // 返回北京市的区县
            regions.add(createRegion("110100", "北京市", 2, "110000"));
            regions.add(createRegion("110101", "东城区", 3, "110100"));
            regions.add(createRegion("110102", "西城区", 3, "110100"));
            regions.add(createRegion("110105", "朝阳区", 3, "110100"));
        }
        
        response.setRegions(regions);
        return response;
    }
    
    @Override
    public GeocodeResponse geocode(GeocodeRequest request) {
        // 这里应该调用地图API进行地理编码
        // 简化处理，返回示例数据
        GeocodeResponse response = new GeocodeResponse();
        response.setAddress(request.getAddress());
        response.setLng(116.4074);
        response.setLat(39.9042);
        response.setProvince("北京市");
        response.setCity("北京市");
        response.setDistrict("朝阳区");
        response.setPrecision("street");
        return response;
    }
    
    @Override
    public DistanceResponse calculateDistance(DistanceRequest request) {
        // 计算两点之间的直线距离
        double distance = calculateHaversineDistance(
                request.getFrom().getLat(), request.getFrom().getLng(),
                request.getTo().getLat(), request.getTo().getLng()
        );
        
        DistanceResponse response = new DistanceResponse();
        response.setDistance(distance);
        response.setUnit("km");
        response.setDuration((int) (distance * 2)); // 简化的时间估算
        response.setDurationUnit("minute");
        
        return response;
    }
    
    @Override
    public NearbyOrgResponse getNearbyOrgs(Double lng, Double lat, Double distance, Integer pageNo, Integer pageSize) {
        // 这里应该从数据库中查询附近的机构
        // 简化处理，返回示例数据
        NearbyOrgResponse response = new NearbyOrgResponse();
        
        NearbyOrgResponse.NearbyOrg org = new NearbyOrgResponse.NearbyOrg();
        org.setId(1002L);
        org.setOrgName("爱心救助站");
        org.setLicenseNo("BJ20240001");
        org.setContactPhone("010-12345678");
        org.setAddress("北京市朝阳区xxx街xxx号");
        org.setLng(116.4);
        org.setLat(39.9);
        org.setDistance(0.5);
        org.setPetCount(18);
        org.setAdoptionCount(12);
        org.setRating(4.8);
        
        response.setList(Arrays.asList(org));
        response.setPageNo(pageNo);
        response.setPageSize(pageSize);
        response.setTotal(1L);
        response.setTotalPages(1);
        
        return response;
    }
    
    private SystemConfigResponse.CreditLevel createCreditLevel(int level, int minScore, int maxScore, String name, String icon) {
        SystemConfigResponse.CreditLevel creditLevel = new SystemConfigResponse.CreditLevel();
        creditLevel.setLevel(level);
        creditLevel.setMinScore(minScore);
        creditLevel.setMaxScore(maxScore);
        creditLevel.setName(name);
        creditLevel.setIcon(icon);
        return creditLevel;
    }
    
    private SystemConfigResponse.DictItem createDictItem(String value, String label) {
        SystemConfigResponse.DictItem item = new SystemConfigResponse.DictItem();
        item.setValue(value);
        item.setLabel(label);
        return item;
    }
    
    private SystemConfigResponse.ApplicationStatus createApplicationStatus(String value, String label, int step) {
        SystemConfigResponse.ApplicationStatus status = new SystemConfigResponse.ApplicationStatus();
        status.setValue(value);
        status.setLabel(label);
        status.setStep(step);
        return status;
    }
    
    private SystemConfigResponse.TagCategory createTagCategory(String type, String label) {
        SystemConfigResponse.TagCategory category = new SystemConfigResponse.TagCategory();
        category.setType(type);
        category.setLabel(label);
        return category;
    }
    
    private NoticeResponse convertToNoticeResponse(NoticeEntity entity) {
        NoticeResponse response = new NoticeResponse();
        response.setId(entity.getId());
        response.setTitle(entity.getTitle());
        response.setContent(entity.getContent());
        response.setStatus(entity.getStatus());
        response.setCreateTime(entity.getCreateTime());
        return response;
    }
    
    private TagResponse.TagItem convertToTagItem(TagEntity entity) {
        TagResponse.TagItem item = new TagResponse.TagItem();
        item.setId(entity.getId());
        item.setName(entity.getName());
        return item;
    }
    
    private RegionResponse.Region createRegion(String code, String name, Integer level, String parentCode) {
        RegionResponse.Region region = new RegionResponse.Region();
        region.setCode(code);
        region.setName(name);
        region.setLevel(level);
        region.setParentCode(parentCode);
        return region;
    }
    
    private String generateRandomCaptcha() {
        return String.valueOf(1000 + new Random().nextInt(9000));
    }
    
    private String generateCaptchaImage(String captcha) {
        // 实际项目中应该生成真实的验证码图片
        // 这里返回一个模拟的base64图片
        return "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==";
    }
    
    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371; // 地球半径（公里）
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS * c;
    }
}