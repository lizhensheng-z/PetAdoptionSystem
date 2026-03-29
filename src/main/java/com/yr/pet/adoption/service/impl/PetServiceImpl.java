package com.yr.pet.adoption.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yr.pet.adoption.common.PageResult;
import com.yr.pet.adoption.common.RedisUtil;
import com.yr.pet.adoption.exception.BizException;
import com.yr.pet.adoption.exception.ErrorCode;
import com.yr.pet.adoption.mapper.*;
import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.model.entity.*;
import com.yr.pet.adoption.service.PetService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 宠物服务实现类
 * @author yr
 * @since 2024-02-14
 */
@Service
public class PetServiceImpl extends ServiceImpl<PetMapper, PetEntity> implements PetService {

    private static final String PET_APPLICATION_COUNT_KEY = "pet:application:count:";
    private static final String PET_FAVORITE_COUNT_KEY = "pet:favorite:count:";
    private static final long CACHE_EXPIRE_TIME = 3600; // 1小时

    @Autowired
    private PetMediaMapper petMediaMapper;
    
    @Autowired
    private PetTagMapper petTagMapper;
    
    @Autowired
    private TagMapper tagMapper;
    
    @Autowired
    private OrgProfileMapper orgProfileMapper;
    
    @Autowired
    private RedisUtil redisUtil;
    
    @Autowired
    private AdoptionApplicationMapper adoptionApplicationMapper;
    
    @Autowired
    private UserFavoriteMapper userFavoriteMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Override
    public PageResult<PetListResponse> getPetList(PetListRequest request) {
        // 构建查询条件
        LambdaQueryWrapper<PetEntity> queryWrapper = new LambdaQueryWrapper<>();
        
        // 只查询已发布且审核通过的宠物
        queryWrapper.eq(PetEntity::getStatus, "PUBLISHED")
                   .eq(PetEntity::getAuditStatus, "APPROVED")
                   .eq(PetEntity::getDeleted, 0);

        // 关键词搜索
        if (StringUtils.hasText(request.getKeyword())) {
            String keyword = "%" + request.getKeyword() + "%";
            queryWrapper.and(wrapper -> wrapper
                .like(PetEntity::getName, keyword)
                .or()
                .like(PetEntity::getBreed, keyword)
                .or()
                .like(PetEntity::getPersonalityDesc, keyword)
            );
        }

        // 物种筛选
        if (StringUtils.hasText(request.getSpecies())) {
            queryWrapper.eq(PetEntity::getSpecies, request.getSpecies());
        }

        // 性别筛选
        if (StringUtils.hasText(request.getGender())) {
            queryWrapper.eq(PetEntity::getGender, request.getGender());
        }

        // 年龄范围筛选
        if (request.getAgeMin() != null) {
            queryWrapper.ge(PetEntity::getAgeMonth, request.getAgeMin());
        }
        if (request.getAgeMax() != null) {
            queryWrapper.le(PetEntity::getAgeMonth, request.getAgeMax());
        }

        // 体型筛选
        if (StringUtils.hasText(request.getSize())) {
            queryWrapper.eq(PetEntity::getSize, request.getSize());
        }

        // 疫苗状态筛选
        if (request.getVaccinated() != null) {
            queryWrapper.eq(PetEntity::getVaccinated, request.getVaccinated());
        }

        // 绝育状态筛选
        if (request.getSterilized() != null) {
            queryWrapper.eq(PetEntity::getSterilized, request.getSterilized());
        }

        // 排序
        if ("distance".equals(request.getSortBy())) {
            // 距离排序需要用户位置，先查询再排序
            if (request.getLng() != null && request.getLat() != null) {
                // 不在SQL层排序，在内存中排序
                queryWrapper.orderByDesc(PetEntity::getPublishedTime);
            } else {
                queryWrapper.orderByDesc(PetEntity::getPublishedTime);
            }
        } else if ("age_month".equals(request.getSortBy())) {
            // 按年龄排序
            if ("asc".equalsIgnoreCase(request.getOrder())) {
                queryWrapper.orderByAsc(PetEntity::getAgeMonth);
            } else {
                queryWrapper.orderByDesc(PetEntity::getAgeMonth);
            }
        } else if ("published_time".equals(request.getSortBy())) {
            if ("asc".equalsIgnoreCase(request.getOrder())) {
                queryWrapper.orderByAsc(PetEntity::getPublishedTime);
            } else {
                queryWrapper.orderByDesc(PetEntity::getPublishedTime);
            }
        } else {
            // 默认按发布时间降序
            queryWrapper.orderByDesc(PetEntity::getPublishedTime);
        }

        // 分页查询
        Page<PetEntity> page = new Page<>(request.getPage(), request.getPageSize());
        IPage<PetEntity> petPage = this.page(page, queryWrapper);

        // 转换为响应数据
        List<PetListResponse> petList = petPage.getRecords().stream()
            .map(pet -> convertToListResponse(pet, request.getLng(), request.getLat()))
            .collect(Collectors.toList());

        // 距离排序：在内存中按距离升序排列
        if ("distance".equals(request.getSortBy()) && request.getLng() != null && request.getLat() != null) {
            petList.sort((a, b) -> {
                if (a.getDistance() == null && b.getDistance() == null) return 0;
                if (a.getDistance() == null) return 1;
                if (b.getDistance() == null) return -1;
                return a.getDistance().compareTo(b.getDistance());
            });
        }

        return new PageResult<>(
            petList,
            (int) petPage.getCurrent(),
            (int) petPage.getSize(),
            petPage.getTotal()
        );
    }

    @Override
    public PetDetailResponse getPetDetail(Long id) {
        PetEntity pet = this.getById(id);
        if (pet == null || pet.getDeleted() == 1) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "宠物不存在");
        }
        


        return convertToDetailResponse(pet);
    }

    @Override
    public PageResult<PetListResponse> getRecommendPets(Long userId, Integer pageNo, Integer pageSize,
                                                       BigDecimal lng, BigDecimal lat) {
        // 1. 分析用户偏好（基于历史行为）
        UserPreferenceInfo preference = analyzeUserPreference(userId);

        // 2. 获取用户已申请的宠物ID列表，用于排除
        List<Long> appliedPetIds = getAppliedPetIds(userId);

        // 3. 构建查询条件
        LambdaQueryWrapper<PetEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PetEntity::getStatus, "PUBLISHED")
                   .eq(PetEntity::getAuditStatus, "APPROVED")
                   .eq(PetEntity::getDeleted, 0);

        // 排除已申请的宠物
        if (appliedPetIds != null && !appliedPetIds.isEmpty()) {
            queryWrapper.notIn(PetEntity::getId, appliedPetIds);
        }

        // 按发布时间降序
        queryWrapper.orderByDesc(PetEntity::getPublishedTime);

        // 4. 查询宠物列表（多查一些用于排序）
        Page<PetEntity> page = new Page<>(pageNo, pageSize);
        IPage<PetEntity> petPage = this.page(page, queryWrapper);

        // 5. 计算推荐分数并排序
        List<PetListResponse> petList = petPage.getRecords().stream()
            .map(pet -> {
                PetListResponse response = convertToListResponse(pet, lng, lat);
                int score = calculateRecommendScore(pet, preference, lng, lat);
                response.setMatchScore(score);
                return response;
            })
            .sorted((a, b) -> b.getMatchScore().compareTo(a.getMatchScore()))
            .collect(Collectors.toList());

        return new PageResult<>(petList, pageNo, pageSize, petPage.getTotal());
    }

    /**
     * 分析用户偏好（基于收藏、申请历史和用户手动设置）
     */
    private UserPreferenceInfo analyzeUserPreference(Long userId) {
        if (userId == null) {
            return null;
        }

        UserPreferenceInfo preference = new UserPreferenceInfo();

        // ========== 1. 读取用户手动设置的偏好 ==========
        UserEntity user = userMapper.selectById(userId);
        if (user != null && user.getPreferenceJson() != null && !user.getPreferenceJson().isEmpty()) {
            try {
                Map<String, Object> userPreference = objectMapper.readValue(user.getPreferenceJson(), Map.class);
                preference.setPetTypes((List<String>) userPreference.get("petTypes"));
                preference.setAgeRange((List<Integer>) userPreference.get("ageRange"));
                preference.setGender((String) userPreference.get("gender"));
                preference.setTags((List<String>) userPreference.get("tags"));
                preference.setDistance((Integer) userPreference.get("distance"));
                preference.setSizes((List<String>) userPreference.get("sizes"));
                preference.setHealthRequirements((List<String>) userPreference.get("healthRequirements"));
            } catch (Exception e) {
                // 解析失败，忽略手动设置的偏好
            }
        }

        // ========== 2. 分析用户收藏行为 ==========
        LambdaQueryWrapper<UserFavoriteEntity> favoriteWrapper = new LambdaQueryWrapper<>();
        favoriteWrapper.eq(UserFavoriteEntity::getUserId, userId);
        List<UserFavoriteEntity> favorites = userFavoriteMapper.selectList(favoriteWrapper);

        if (!favorites.isEmpty()) {
            // 获取收藏宠物的物种和品种统计
            Map<String, Long> speciesCount = new HashMap<>();
            Map<String, Long> breedCount = new HashMap<>();

            for (UserFavoriteEntity favorite : favorites) {
                PetEntity pet = this.getById(favorite.getPetId());
                if (pet != null) {
                    if (pet.getSpecies() != null) {
                        speciesCount.merge(pet.getSpecies(), 1L, Long::sum);
                    }
                    if (pet.getBreed() != null) {
                        breedCount.merge(pet.getBreed(), 1L, Long::sum);
                    }
                }
            }

            // 找出最常收藏的物种和品种
            preference.setFavoriteSpecies(speciesCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null));

            preference.setFavoriteBreed(breedCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null));
        }

        // ========== 3. 分析用户申请行为 ==========
        LambdaQueryWrapper<AdoptionApplicationEntity> appWrapper = new LambdaQueryWrapper<>();
        appWrapper.eq(AdoptionApplicationEntity::getUserId, userId);
        List<AdoptionApplicationEntity> applications = adoptionApplicationMapper.selectList(appWrapper);

        if (!applications.isEmpty()) {
            Map<String, Long> speciesCount = new HashMap<>();
            Map<String, Long> breedCount = new HashMap<>();

            for (AdoptionApplicationEntity app : applications) {
                PetEntity pet = this.getById(app.getPetId());
                if (pet != null) {
                    if (pet.getSpecies() != null) {
                        speciesCount.merge(pet.getSpecies(), 1L, Long::sum);
                    }
                    if (pet.getBreed() != null) {
                        breedCount.merge(pet.getBreed(), 1L, Long::sum);
                    }
                }
            }

            preference.setAppliedSpecies(speciesCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null));

            preference.setAppliedBreed(breedCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null));
        }

        return preference;
    }

    /**
     * 获取用户已申请的宠物ID列表
     */
    private List<Long> getAppliedPetIds(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<AdoptionApplicationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdoptionApplicationEntity::getUserId, userId)
               .select(AdoptionApplicationEntity::getPetId);

        return adoptionApplicationMapper.selectList(wrapper).stream()
            .map(AdoptionApplicationEntity::getPetId)
            .distinct()
            .collect(Collectors.toList());
    }

    /**
     * 计算推荐分数
     * 维度：偏好匹配(40分) + 新鲜度(20分) + 距离(20分) + 热度(10分) + 随机(10分)
     */
    private int calculateRecommendScore(PetEntity pet, UserPreferenceInfo preference,
                                       BigDecimal userLng, BigDecimal userLat) {
        int score = 0;
        Random random = new Random();

        // 1. 偏好匹配 (40分)
        if (preference != null) {
            // 1.1 宠物类型匹配 (10分) - 用户手动设置
            if (preference.getPetTypes() != null && !preference.getPetTypes().isEmpty()) {
                if (preference.getPetTypes().contains(pet.getSpecies())) {
                    score += 10;
                }
            } else {
                // 如果用户未设置，使用行为分析
                if (preference.getFavoriteSpecies() != null &&
                    preference.getFavoriteSpecies().equals(pet.getSpecies())) {
                    score += 8;
                } else if (preference.getAppliedSpecies() != null &&
                           preference.getAppliedSpecies().equals(pet.getSpecies())) {
                    score += 5;
                }
            }

            // 1.2 性别匹配 (5分) - 用户手动设置
            if (preference.getGender() != null && !preference.getGender().isEmpty()) {
                if (preference.getGender().equals(pet.getGender())) {
                    score += 5;
                }
            }

            // 1.3 年龄范围匹配 (5分) - 用户手动设置
            if (preference.getAgeRange() != null && preference.getAgeRange().size() == 2) {
                int minAge = preference.getAgeRange().get(0);
                int maxAge = preference.getAgeRange().get(1);
                if (pet.getAgeMonth() != null &&
                    pet.getAgeMonth() >= minAge && pet.getAgeMonth() <= maxAge) {
                    score += 5;
                }
            }

            // 1.4 体型匹配 (5分) - 用户手动设置
            if (preference.getSizes() != null && !preference.getSizes().isEmpty()) {
                if (pet.getSize() != null && preference.getSizes().contains(pet.getSize())) {
                    score += 5;
                }
            }

            // 1.5 品种匹配 (10分) - 行为分析
            if (preference.getFavoriteBreed() != null &&
                preference.getFavoriteBreed().equals(pet.getBreed())) {
                score += 10;
            } else if (preference.getAppliedBreed() != null &&
                       preference.getAppliedBreed().equals(pet.getBreed())) {
                score += 6;
            }

            // 1.6 标签匹配 (5分) - 用户手动设置
            if (preference.getTags() != null && !preference.getTags().isEmpty() && pet.getId() != null) {
                // 获取宠物的标签
                List<String> petTags = getPetTagNames(pet.getId());
                long matchCount = petTags.stream()
                    .filter(tag -> preference.getTags().contains(tag))
                    .count();
                if (matchCount > 0) {
                    score += Math.min(5, (int) matchCount * 2);
                }
            }
        }

        // 2. 新鲜度 (20分) - 最近7天发布得满分，每超7天减3分
        if (pet.getPublishedTime() != null) {
            long daysSincePublish = java.time.temporal.ChronoUnit.DAYS.between(
                pet.getPublishedTime().toLocalDate(), java.time.LocalDate.now());
            int freshScore = Math.max(0, 20 - (int)(daysSincePublish / 7) * 3);
            score += freshScore;
        }

        // 3. 距离因素 (20分) - 5km内满分，每增加5km减3分
        if (userLng != null && userLat != null && pet.getLng() != null && pet.getLat() != null) {
            BigDecimal distance = calculateDistance(userLng, userLat, pet.getLng(), pet.getLat());
            if (distance != null) {
                // 考虑用户设置的距离偏好
                int maxDistance = (preference != null && preference.getDistance() != null)
                    ? preference.getDistance() : 50;
                // 如果超出用户偏好距离，大幅减分
                if (distance.intValue() > maxDistance) {
                    score -= 10;
                }
                int distanceScore = Math.max(0, 20 - distance.divide(BigDecimal.valueOf(5), 0, RoundingMode.DOWN).intValue() * 3);
                score += distanceScore;
            }
        }

        // 4. 热度因素 (10分) - 基于收藏数
        int favoriteCount = getFavoriteCount(pet.getId());
        int hotScore = Math.min(10, favoriteCount);
        score += hotScore;

        // 5. 随机因子 (10分) - 增加推荐多样性
        score += random.nextInt(11);

        return Math.min(100, Math.max(0, score));
    }

    /**
     * 获取宠物的标签名称列表
     */
    private List<String> getPetTagNames(Long petId) {
        LambdaQueryWrapper<PetTagEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PetTagEntity::getPetId, petId);
        List<PetTagEntity> petTags = petTagMapper.selectList(wrapper);

        if (petTags.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> tagIds = petTags.stream()
            .map(PetTagEntity::getTagId)
            .collect(Collectors.toList());

        List<TagEntity> tags = tagMapper.selectBatchIds(tagIds);
        return tags.stream()
            .map(TagEntity::getName)
            .filter(StringUtils::hasText)
            .collect(Collectors.toList());
    }

    /**
     * 用户偏好信息内部类
     */
    private static class UserPreferenceInfo {
        // 基于用户行为分析的偏好
        private String favoriteSpecies;  // 用户最常收藏的物种
        private String favoriteBreed;    // 用户最常收藏的品种
        private String appliedSpecies;   // 用户最常申请的物种
        private String appliedBreed;     // 用户最常申请的品种

        // 用户手动设置的偏好
        private List<String> petTypes;           // 宠物类型偏好
        private List<Integer> ageRange;          // 年龄范围 [最小, 最大]
        private String gender;                   // 性别偏好
        private List<String> tags;               // 标签偏好
        private Integer distance;                // 距离偏好（公里）
        private List<String> sizes;              // 体型偏好
        private List<String> healthRequirements; // 健康要求

        // Getter and Setter
        public String getFavoriteSpecies() { return favoriteSpecies; }
        public void setFavoriteSpecies(String favoriteSpecies) { this.favoriteSpecies = favoriteSpecies; }
        public String getFavoriteBreed() { return favoriteBreed; }
        public void setFavoriteBreed(String favoriteBreed) { this.favoriteBreed = favoriteBreed; }
        public String getAppliedSpecies() { return appliedSpecies; }
        public void setAppliedSpecies(String appliedSpecies) { this.appliedSpecies = appliedSpecies; }
        public String getAppliedBreed() { return appliedBreed; }
        public void setAppliedBreed(String appliedBreed) { this.appliedBreed = appliedBreed; }
        public List<String> getPetTypes() { return petTypes; }
        public void setPetTypes(List<String> petTypes) { this.petTypes = petTypes; }
        public List<Integer> getAgeRange() { return ageRange; }
        public void setAgeRange(List<Integer> ageRange) { this.ageRange = ageRange; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        public Integer getDistance() { return distance; }
        public void setDistance(Integer distance) { this.distance = distance; }
        public List<String> getSizes() { return sizes; }
        public void setSizes(List<String> sizes) { this.sizes = sizes; }
        public List<String> getHealthRequirements() { return healthRequirements; }
        public void setHealthRequirements(List<String> healthRequirements) { this.healthRequirements = healthRequirements; }
    }

    @Override
    public PetSuggestResponse getSearchSuggestions(String keyword) {
        PetSuggestResponse response = new PetSuggestResponse();
        
        if (!StringUtils.hasText(keyword) || keyword.length() < 2) {
            return response;
        }

        // 获取品种建议
        LambdaQueryWrapper<PetEntity> breedWrapper = new LambdaQueryWrapper<>();
        breedWrapper.select(PetEntity::getBreed)
                   .like(PetEntity::getBreed, "%" + keyword + "%")
                   .groupBy(PetEntity::getBreed)
                   .last("LIMIT 10");
        List<String> breeds = this.list(breedWrapper).stream()
            .map(PetEntity::getBreed)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        // 获取宠物名称建议
        LambdaQueryWrapper<PetEntity> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.select(PetEntity::getName)
                   .like(PetEntity::getName, "%" + keyword + "%")
                   .groupBy(PetEntity::getName)
                   .last("LIMIT 10");
        List<String> names = this.list(nameWrapper).stream()
            .map(PetEntity::getName)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        // 获取标签建议
        LambdaQueryWrapper<TagEntity> tagWrapper = new LambdaQueryWrapper<>();
        tagWrapper.select(TagEntity::getName)
                 .like(TagEntity::getName, "%" + keyword + "%")
                 .eq(TagEntity::getEnabled, 1)
                 .groupBy(TagEntity::getName)
                 .last("LIMIT 10");
        List<String> tags = tagMapper.selectList(tagWrapper).stream()
            .map(TagEntity::getName)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        response.setBreeds(breeds);
        response.setKeywords(names);
        response.setTags(tags);

        return response;
    }

    @Override
    public List<SimilarPetResponse> getSimilarPets(Long petId, Integer limit) {
        PetEntity currentPet = this.getById(petId);
        if (currentPet == null || currentPet.getDeleted() == 1) {
            return Collections.emptyList();
        }

        // 构建相似度查询条件
        LambdaQueryWrapper<PetEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(PetEntity::getId, petId)
                   .eq(PetEntity::getStatus, "PUBLISHED")
                   .eq(PetEntity::getAuditStatus, "APPROVED")
                   .eq(PetEntity::getDeleted, 0)
                   .orderByDesc(PetEntity::getPublishedTime)
                   .last("LIMIT " + (limit != null ? limit : 6));

        // 优先匹配相同物种和品种
        if (StringUtils.hasText(currentPet.getSpecies())) {
            queryWrapper.eq(PetEntity::getSpecies, currentPet.getSpecies());
        }
        if (StringUtils.hasText(currentPet.getBreed())) {
            queryWrapper.or().like(PetEntity::getBreed, "%" + currentPet.getBreed() + "%");
        }

        List<PetEntity> similarPets = this.list(queryWrapper);
        
        return similarPets.stream()
            .map(pet -> {
                SimilarPetResponse response = new SimilarPetResponse();
                BeanUtils.copyProperties(pet, response);
                
                // 设置图片和标签
                List<String> images = getPetImages(pet.getId());
                if (!images.isEmpty()) {
                    response.setCoverUrl(images.get(0));
                }
                
                List<String> tags = getPetTags(pet.getId());
                response.setTags(tags);
                
                // 设置机构信息
                OrgProfileEntity orgProfile = orgProfileMapper.selectById(pet.getOrgUserId());
                if (orgProfile != null) {
                    response.setOrgName(orgProfile.getOrgName());
                }
                
                // 计算相似度分数（基于物种、品种、年龄、性别等）
                int score = calculateSimilarityScore(currentPet, pet);
                response.setSimilarityScore(score);
                
                return response;
            })
            .sorted((a, b) -> b.getSimilarityScore().compareTo(a.getSimilarityScore()))
            .collect(Collectors.toList());
    }

    /**
     * 计算宠物相似度分数
     */
    private int calculateSimilarityScore(PetEntity current, PetEntity target) {
        int score = 50; // 基础分数
        
        if (current.getSpecies() != null && current.getSpecies().equals(target.getSpecies())) {
            score += 20;
        }
        
        if (current.getBreed() != null && current.getBreed().equals(target.getBreed())) {
            score += 15;
        }
        
        if (current.getGender() != null && current.getGender().equals(target.getGender())) {
            score += 10;
        }
        
        if (current.getSize() != null && current.getSize().equals(target.getSize())) {
            score += 10;
        }
        
        // 年龄相近加分
        if (current.getAgeMonth() != null && target.getAgeMonth() != null) {
            int ageDiff = Math.abs(current.getAgeMonth() - target.getAgeMonth());
            if (ageDiff <= 3) {
                score += 10;
            } else if (ageDiff <= 6) {
                score += 5;
            }
        }
        
        return Math.min(score, 100);
    }

    /**
     * 将PetEntity转换为PetListResponse
     */
    private PetListResponse convertToListResponse(PetEntity pet, BigDecimal userLng, BigDecimal userLat) {
        PetListResponse response = new PetListResponse();
        BeanUtils.copyProperties(pet, response);

        // 设置图片列表
        List<String> images = getPetImages(pet.getId());
        response.setImages(images);

        // 设置标签列表
        List<String> tags = getPetTags(pet.getId());
        response.setTags(tags);

        // 设置机构信息
        OrgProfileEntity orgProfile = orgProfileMapper.selectById(pet.getOrgUserId());
        if (orgProfile != null) {
            response.setOrgName(orgProfile.getOrgName());
        }

        // 计算距离
        if (userLng != null && userLat != null && pet.getLng() != null && pet.getLat() != null) {
            BigDecimal distance = calculateDistance(userLng, userLat, pet.getLng(), pet.getLat());
            response.setDistance(distance);
        }



        return response;
    }

    /**
     * 将PetEntity转换为PetDetailResponse
     */
    private PetDetailResponse convertToDetailResponse(PetEntity pet) {
        PetDetailResponse response = new PetDetailResponse();
        BeanUtils.copyProperties(pet, response);

        // 设置媒体文件列表
        List<PetDetailResponse.PetMediaResponse> mediaList = getPetMediaList(pet.getId());
        response.setMediaList(mediaList);

        // 设置标签列表
        List<String> tags = getPetTags(pet.getId());
        response.setTags(tags);

        // 设置机构信息
        OrgProfileEntity orgProfile = orgProfileMapper.selectById(pet.getOrgUserId());
        if (orgProfile != null) {
            response.setOrgName(orgProfile.getOrgName());
        }



        return response;
    }

    /**
     * 获取宠物图片列表
     */
    private List<String> getPetImages(Long petId) {
        LambdaQueryWrapper<PetMediaEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PetMediaEntity::getPetId, petId)
               .eq(PetMediaEntity::getMediaType, "IMAGE")
               .orderByAsc(PetMediaEntity::getSort);
        
        return petMediaMapper.selectList(wrapper).stream()
            .map(PetMediaEntity::getUrl)
            .collect(Collectors.toList());
    }

    /**
     * 获取宠物媒体文件列表
     */
    private List<PetDetailResponse.PetMediaResponse> getPetMediaList(Long petId) {
        LambdaQueryWrapper<PetMediaEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PetMediaEntity::getPetId, petId)
               .orderByAsc(PetMediaEntity::getSort);
        
        return petMediaMapper.selectList(wrapper).stream()
            .map(media -> {
                PetDetailResponse.PetMediaResponse mediaResponse = new PetDetailResponse.PetMediaResponse();
                mediaResponse.setId(media.getId());
                mediaResponse.setUrl(media.getUrl());
                mediaResponse.setMediaType(media.getMediaType());
                mediaResponse.setSort(media.getSort());
                return mediaResponse;
            })
            .collect(Collectors.toList());
    }

    /**
     * 获取宠物标签列表
     */
    private List<String> getPetTags(Long petId) {
        // 查询宠物标签关联
        LambdaQueryWrapper<PetTagEntity> petTagWrapper = new LambdaQueryWrapper<>();
        petTagWrapper.eq(PetTagEntity::getPetId, petId);
        List<Long> tagIds = petTagMapper.selectList(petTagWrapper).stream()
            .map(PetTagEntity::getTagId)
            .collect(Collectors.toList());

        if (tagIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 查询标签名称
        LambdaQueryWrapper<TagEntity> tagWrapper = new LambdaQueryWrapper<>();
        tagWrapper.in(TagEntity::getId, tagIds)
                 .eq(TagEntity::getEnabled, 1);
        
        return tagMapper.selectList(tagWrapper).stream()
            .map(TagEntity::getName)
            .collect(Collectors.toList());
    }

/**
     * 计算两点之间的距离（公里）
     */
    private BigDecimal calculateDistance(BigDecimal lng1, BigDecimal lat1, BigDecimal lng2, BigDecimal lat2) {
        if (lng1 == null || lat1 == null || lng2 == null || lat2 == null) {
            return null;
        }

        // 使用Haversine公式计算距离
        double earthRadius = 6371; // 地球半径（公里）
        
        double lat1Rad = Math.toRadians(lat1.doubleValue());
        double lat2Rad = Math.toRadians(lat2.doubleValue());
        double deltaLat = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
        double deltaLng = Math.toRadians(lng2.doubleValue() - lng1.doubleValue());

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        return BigDecimal.valueOf(distance).setScale(1, RoundingMode.HALF_UP);
    }

    // ==================== 机构宠物管理方法实现 ====================

    @Override
    public PetCreateResponse createPet(Long orgUserId, PetCreateRequest request) {
        // 1. 验证机构用户身份
        OrgProfileEntity orgProfile = orgProfileMapper.selectById(orgUserId);
        if (orgProfile == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "机构资料不存在");
        }

        // 2. 创建宠物实体
        PetEntity pet = new PetEntity();
        BeanUtils.copyProperties(request, pet);

        // 设置机构用户ID
        pet.setOrgUserId(orgUserId);

        // 设置状态为草稿
        pet.setStatus("DRAFT");
        pet.setAuditStatus("NONE");

        // 设置默认位置（如果未提供，使用机构位置）
        if (pet.getLng() == null && orgProfile.getLng() != null) {
            pet.setLng(orgProfile.getLng());
        }
        if (pet.getLat() == null && orgProfile.getLat() != null) {
            pet.setLat(orgProfile.getLat());
        }

        pet.setDeleted(0);

        // 3. 保存宠物信息
        this.save(pet);

        // 4. 处理标签关联
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            savePetTags(pet.getId(), request.getTagIds());
        }

        // 5. 返回创建结果
        PetCreateResponse response = new PetCreateResponse();
        response.setId(pet.getId());
        response.setStatus(pet.getStatus());
        response.setAuditStatus(pet.getAuditStatus());
        response.setCreateTime(pet.getCreateTime());

        return response;
    }

    @Override
    public PetCreateResponse createPetV2(Long orgUserId, PetCreateRequestV2 request) {
        // 1. 验证机构用户身份
        OrgProfileEntity orgProfile = orgProfileMapper.selectById(orgUserId);
        if (orgProfile == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "机构资料不存在");
        }

        // 2. 创建宠物实体
        PetEntity pet = new PetEntity();
        
        // 复制基本属性
        pet.setName(request.getName());
        pet.setSpecies(request.getSpecies());
        pet.setBreed(request.getBreed());
        pet.setGender(request.getGender());
        pet.setSize(request.getSize());
        pet.setColor(request.getColor());
        pet.setAdoptRequirements(request.getAdoptRequirements());
        
        // 计算总月龄

            pet.setAgeMonth(request.getAgeMonth());


        // 设置健康信息
        if (request.getHealth() != null) {
            PetCreateRequestV2.Health health = request.getHealth();
            pet.setSterilized(health.getSterilized());
            pet.setVaccinated(health.getVaccinated());
            pet.setDewormed(health.getDewormed());
            pet.setHealthDesc(health.getHealthDesc());
        }

        // 设置性格描述
        if (request.getPersonality() != null) {
            pet.setPersonalityDesc(request.getPersonality().getDesc());
        }

        // 设置位置信息
        if (request.getLocation() != null) {
            PetCreateRequestV2.Location location = request.getLocation();
            pet.setLng(location.getLng());
            pet.setLat(location.getLat());
        }

        // 设置封面URL
        if (request.getCoverUrl() != null) {
            pet.setCoverUrl(request.getCoverUrl());
        }

        // 设置机构用户ID
        pet.setOrgUserId(orgUserId);

        // 设置状态（使用请求中的状态或默认为草稿）
        pet.setStatus(request.getStatus() != null ? request.getStatus() : "DRAFT");
        pet.setAuditStatus(request.getAuditStatus() != null ? request.getAuditStatus() : "NONE");

        // 设置默认位置（如果未提供，使用机构位置）
        if (pet.getLng() == null && orgProfile.getLng() != null) {
            pet.setLng(orgProfile.getLng());
        }
        if (pet.getLat() == null && orgProfile.getLat() != null) {
            pet.setLat(orgProfile.getLat());
        }

        pet.setDeleted(0);

        // 3. 保存宠物信息
        this.save(pet);

        // 4. 处理标签关联
        List<Long> tagIds = request.getPersonality().getTags();
        if (tagIds != null && !tagIds.isEmpty()) {
            savePetTags(pet.getId(), tagIds);
        }

        // 5. 返回创建结果
        PetCreateResponse response = new PetCreateResponse();
        response.setId(pet.getId());
        response.setStatus(pet.getStatus());
        response.setAuditStatus(pet.getAuditStatus());
        response.setCreateTime(pet.getCreateTime());

        return response;
    }

    @Override
    public void updatePet(Long orgUserId, Long petId, PetUpdateRequest request) {
        // 1. 验证宠物是否存在且属于当前机构
        PetEntity pet = this.getById(petId);
        if (pet == null || pet.getDeleted() == 1) {
            throw new BizException(ErrorCode.NOT_FOUND, "宠物不存在");
        }

        if (!pet.getOrgUserId().equals(orgUserId)) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权修改该宠物信息");
        }

        // 2. 检查宠物状态,已发布的宠物不能直接修改
        if ("PUBLISHED".equals(pet.getStatus())) {
            throw new BizException(ErrorCode.OPERATION_NOT_ALLOWED, "已发布的宠物不能直接修改，请先下架");
        }

        // 3. 更新宠物信息
        BeanUtils.copyProperties(request, pet);
        this.updateById(pet);

        // 4. 更新标签关联
        if (request.getTagIds() != null) {
            // 删除旧标签
            LambdaQueryWrapper<PetTagEntity> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(PetTagEntity::getPetId, petId);
            petTagMapper.delete(deleteWrapper);

            // 添加新标签
            if (!request.getTagIds().isEmpty()) {
                savePetTags(petId, request.getTagIds());
            }
        }
    }

    @Override
    public void deletePet(Long orgUserId, Long petId) {
        // 1. 验证宠物是否存在且属于当前机构
        PetEntity pet = this.getById(petId);
        if (pet == null || pet.getDeleted() == 1) {
            throw new BizException(ErrorCode.NOT_FOUND, "宠物不存在");
        }
        
        if (!pet.getOrgUserId().equals(orgUserId)) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权删除该宠物");
        }

        // 2. 检查是否有进行中的领养申请
        // TODO: 可以在这里添加检查逻辑

        // 3. 逻辑删除宠物
        this.removeById(petId);
    }

    @Override
    public PageResult<OrgPetListResponse> getOrgPetList(Long orgUserId, OrgPetQueryRequest request) {
        // 1. 构建查询条件
        LambdaQueryWrapper<PetEntity> queryWrapper = new LambdaQueryWrapper<>();

        // 只查询当前机构的宠物
        queryWrapper.eq(PetEntity::getOrgUserId, orgUserId)
                   .eq(PetEntity::getDeleted, 0);

        // 状态筛选
        if (StringUtils.hasText(request.getStatus())) {
            queryWrapper.eq(PetEntity::getStatus, request.getStatus());
        }

        // 审核状态筛选
        if (StringUtils.hasText(request.getAuditStatus())) {
            queryWrapper.eq(PetEntity::getAuditStatus, request.getAuditStatus());
        }

        // 排序
        if ("createTime".equals(request.getSortBy())) {
            if ("asc".equalsIgnoreCase(request.getOrder())) {
                queryWrapper.orderByAsc(PetEntity::getCreateTime);
            } else {
                queryWrapper.orderByDesc(PetEntity::getCreateTime);
            }
        } else {
            // 默认按创建时间降序
            queryWrapper.orderByDesc(PetEntity::getCreateTime);
        }

        // 2. 分页查询
        Page<PetEntity> page = new Page<>(request.getPageNo(), request.getPageSize());
        IPage<PetEntity> petPage = this.page(page, queryWrapper);

        // 3. 转换为响应数据
        List<OrgPetListResponse> petList = petPage.getRecords().stream()
            .map(pet -> convertToOrgPetListResponse(pet))
            .collect(Collectors.toList());

        return new PageResult<>(
            petList,
            (int) petPage.getCurrent(),
            (int) petPage.getSize(),
            petPage.getTotal()
        );
    }

    @Override
    public PetDetailResponse getOrgPetDetail(Long orgUserId, Long petId) {
        // 1. 验证宠物是否存在且属于当前机构
        PetEntity pet = this.getById(petId);
        if (pet == null || pet.getDeleted() == 1) {
            throw new BizException(ErrorCode.NOT_FOUND, "宠物不存在");
        }
        
        if (!pet.getOrgUserId().equals(orgUserId)) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权查看该宠物信息");
        }

        // 2. 返回宠物详情
        return convertToDetailResponse(pet);
    }

    /**
     * 保存宠物标签关联
     */
    private void savePetTags(Long petId, List<Long> tagIds) {
        List<PetTagEntity> petTags = tagIds.stream()
            .map(tagId -> {
                PetTagEntity petTag = new PetTagEntity();
                petTag.setPetId(petId);
                petTag.setTagId(tagId);
                return petTag;
            })
            .collect(Collectors.toList());

        // 逐个插入（简化实现，实际可以使用批量插入优化）
        for (PetTagEntity petTag : petTags) {
            petTagMapper.insert(petTag);
        }
    }

    @Override
    public PetMediaEntity savePetMedia(Long orgUserId, Long petId, PetMediaRequest request) {
        // 1. 验证宠物是否存在且属于当前机构
        PetEntity pet = this.getById(petId);
        if (pet == null || pet.getDeleted() == 1) {
            throw new BizException(ErrorCode.NOT_FOUND, "宠物不存在");
        }
        
        if (!pet.getOrgUserId().equals(orgUserId)) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权操作该宠物");
        }

        // 2. 创建媒体实体
        PetMediaEntity media = new PetMediaEntity();
        media.setPetId(petId);
        media.setUrl(request.getUrl());
        media.setMediaType(request.getMediaType());
        media.setSort(request.getSort());
        media.setDeleted(0);

        // 3. 保存媒体信息
        petMediaMapper.insert(media);

        // 4. 如果是封面图，更新宠物封面URL
        if (Boolean.TRUE.equals(request.getIsCover())) {
            pet.setCoverUrl(request.getUrl());
            this.updateById(pet);
        }

        return media;
    }

    @Override
    public void deletePetMedia(Long orgUserId, Long petId, Long mediaId) {
        // 1. 验证宠物是否存在且属于当前机构
        PetEntity pet = this.getById(petId);
        if (pet == null || pet.getDeleted() == 1) {
            throw new BizException(ErrorCode.NOT_FOUND, "宠物不存在");
        }
        
        if (!pet.getOrgUserId().equals(orgUserId)) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权操作该宠物");
        }

        // 2. 验证媒体是否存在
        PetMediaEntity media = petMediaMapper.selectById(mediaId);
        if (media == null || media.getDeleted() == 1) {
            throw new BizException(ErrorCode.NOT_FOUND, "媒体文件不存在");
        }

        // 3. 检查是否是唯一的封面图
        if (pet.getCoverUrl() != null && pet.getCoverUrl().equals(media.getUrl())) {
            // 检查是否还有其他媒体文件
            LambdaQueryWrapper<PetMediaEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PetMediaEntity::getPetId, petId)
                   .ne(PetMediaEntity::getId, mediaId)
                   .eq(PetMediaEntity::getDeleted, 0);
            
            long count = petMediaMapper.selectCount(wrapper);
            if (count == 0) {
                throw new BizException(ErrorCode.OPERATION_NOT_ALLOWED, "不能删除唯一的封面图，请先设置其他图片为封面");
            }
        }

        // 4. 逻辑删除媒体文件
        media.setDeleted(1);
        petMediaMapper.updateById(media);

        // 5. 如果删除的是封面图，更新宠物封面URL
        if (pet.getCoverUrl() != null && pet.getCoverUrl().equals(media.getUrl())) {
            // 查找新的封面图
            LambdaQueryWrapper<PetMediaEntity> newCoverWrapper = new LambdaQueryWrapper<>();
            newCoverWrapper.eq(PetMediaEntity::getPetId, petId)
                          .eq(PetMediaEntity::getDeleted, 0)
                          .orderByAsc(PetMediaEntity::getSort)
                          .last("LIMIT 1");
            
            PetMediaEntity newCover = petMediaMapper.selectOne(newCoverWrapper);
            if (newCover != null) {
                pet.setCoverUrl(newCover.getUrl());
            } else {
                pet.setCoverUrl(null);
            }
            this.updateById(pet);
        }
    }

    @Override
    public void submitPetAudit(Long orgUserId, Long petId) {
        // 1. 验证宠物是否存在且属于当前机构
        PetEntity pet = this.getById(petId);
        if (pet == null || pet.getDeleted() == 1) {
            throw new BizException(ErrorCode.NOT_FOUND, "宠物不存在");
        }
        
        if (!pet.getOrgUserId().equals(orgUserId)) {
            throw new BizException(ErrorCode.FORBIDDEN, "无权操作该宠物");
        }

        // 2. 检查宠物状态
        if (!"DRAFT".equals(pet.getStatus())) {
            throw new BizException(ErrorCode.OPERATION_NOT_ALLOWED, "只有草稿状态的宠物才能提交审核");
        }

        // 3. 检查必填信息是否完整
        if (!StringUtils.hasText(pet.getName()) || 
            !StringUtils.hasText(pet.getSpecies()) || 
            !StringUtils.hasText(pet.getBreed()) ||
            pet.getAgeMonth() == null ||
            !StringUtils.hasText(pet.getGender()) ||
            !StringUtils.hasText(pet.getSize()) ||
            !StringUtils.hasText(pet.getColor())) {
            throw new BizException(ErrorCode.PARAM_ERROR, "请完善宠物基本信息");
        }

        // 4. 检查是否有封面图
        if (!StringUtils.hasText(pet.getCoverUrl())) {
            throw new BizException(ErrorCode.PARAM_ERROR, "请上传宠物封面图");
        }

        // 5. 检查媒体文件数量
        LambdaQueryWrapper<PetMediaEntity> mediaWrapper = new LambdaQueryWrapper<>();
        mediaWrapper.eq(PetMediaEntity::getPetId, petId)
                   .eq(PetMediaEntity::getDeleted, 0);
        long mediaCount = petMediaMapper.selectCount(mediaWrapper);
        if (mediaCount < 3) {
            throw new BizException(ErrorCode.PARAM_ERROR, "请至少上传3张宠物照片");
        }

        // 6. 更新宠物状态
        pet.setStatus("PENDING_AUDIT");
        pet.setAuditStatus("PENDING");
        this.updateById(pet);
    }

    /**
     * 获取宠物申请数量（带Redis缓存）
     */
    private Integer getApplicationCount(Long petId) {
        String key = PET_APPLICATION_COUNT_KEY + petId;
        
        // 先从Redis获取
        Object count = redisUtil.get(key);
        if (count != null) {
            return (Integer) count;
        }
        
        // Redis中没有，从数据库查询
        LambdaQueryWrapper<AdoptionApplicationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdoptionApplicationEntity::getPetId, petId);
        int applicationCount = adoptionApplicationMapper.selectCount(wrapper).intValue();
        
        // 存入Redis，设置过期时间
        redisUtil.set(key, applicationCount, CACHE_EXPIRE_TIME);
        
        return applicationCount;
    }
    
    /**
     * 获取宠物收藏数量（带Redis缓存）
     */
    private Integer getFavoriteCount(Long petId) {
        String key = PET_FAVORITE_COUNT_KEY + petId;
        
        // 先从Redis获取
        Object count = redisUtil.get(key);
        if (count != null) {
            return (Integer) count;
        }
        
        // Redis中没有，从数据库查询
        LambdaQueryWrapper<UserFavoriteEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavoriteEntity::getPetId, petId);
        int favoriteCount = userFavoriteMapper.selectCount(wrapper).intValue();
        
        // 存入Redis，设置过期时间
        redisUtil.set(key, favoriteCount, CACHE_EXPIRE_TIME);
        
        return favoriteCount;
    }
    
    /**
     * 清除宠物申请数量缓存
     */
    public void clearApplicationCountCache(Long petId) {
        String key = PET_APPLICATION_COUNT_KEY + petId;
        redisUtil.del(key);
    }
    
    /**
     * 清除宠物收藏数量缓存
     */
    public void clearFavoriteCountCache(Long petId) {
        String key = PET_FAVORITE_COUNT_KEY + petId;
        redisUtil.del(key);
    }
    
    /**
     * 增加宠物申请数量（用于申请创建时）
     */
    public void incrementApplicationCount(Long petId) {
        String key = PET_APPLICATION_COUNT_KEY + petId;
        redisUtil.incr(key, 1);
    }
    
    /**
     * 增加/减少宠物收藏数量（用于收藏操作时）
     */
    public void updateFavoriteCount(Long petId, int delta) {
        String key = PET_FAVORITE_COUNT_KEY + petId;
        if (delta > 0) {
            redisUtil.incr(key, delta);
        } else {
            redisUtil.decr(key, Math.abs(delta));
        }
    }

    /**
     * 将PetEntity转换为OrgPetListResponse
     */
    private OrgPetListResponse convertToOrgPetListResponse(PetEntity pet) {
        OrgPetListResponse response = new OrgPetListResponse();
        BeanUtils.copyProperties(pet, response);
  
        // 设置封面图片
        if (pet.getCoverUrl() != null) {
            response.setCoverUrl(pet.getCoverUrl());
        } else {
            // 如果没有设置封面,使用第一张图片
            List<String> images = getPetImages(pet.getId());
            if (!images.isEmpty()) {
                response.setCoverUrl(images.get(0));
            }
        }

        // 设置申请数量和收藏数量
        response.setApplicationCount(getApplicationCount(pet.getId()));
        response.setFavoriteCount(getFavoriteCount(pet.getId()));

        return response;
    }
}