package com.yr.pet.adoption.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yr.pet.adoption.common.R;
import com.yr.pet.adoption.mapper.TagMapper;
import com.yr.pet.adoption.model.dto.TagListResponse;
import com.yr.pet.adoption.model.entity.TagEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签管理控制器
 * @author yr
 * @since 2026-02-15
 */
@RestController
@RequestMapping("/api")
@Tag(name = "标签管理", description = "标签字典管理接口")
public class TagController {

    @Autowired
    private TagMapper tagMapper;

    /**
     * 获取标签列表
     */
    @GetMapping("/tags")
    @Operation(summary = "获取标签列表", description = "获取标签字典列表，支持按类型筛选")
    public R<TagListResponse> getTagList(
            @Parameter(description = "标签类型：PERSONALITY/HEALTH/FEATURE", example = "PERSONALITY")
            @RequestParam(required = false) String tagType,
            
            @Parameter(description = "是否启用：0/1，默认1", example = "1")
            @RequestParam(defaultValue = "1") Integer enabled) {
        
        LambdaQueryWrapper<TagEntity> wrapper = new LambdaQueryWrapper<>();
        
        if (tagType != null) {
            wrapper.eq(TagEntity::getTagType, tagType);
        }
        
        wrapper.eq(TagEntity::getEnabled, enabled)
               .orderByAsc(TagEntity::getId);
        
        List<TagEntity> tagEntities = tagMapper.selectList(wrapper);
        
        List<TagListResponse.TagInfo> tagList = tagEntities.stream()
            .map(tag -> new TagListResponse.TagInfo(
                tag.getId(),
                tag.getName(),
                tag.getTagType(),
                tag.getEnabled()
            ))
            .collect(Collectors.toList());
        
        return R.ok(new TagListResponse(tagList));
    }
}
