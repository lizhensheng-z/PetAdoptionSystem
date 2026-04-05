package com.yr.pet.adoption.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yr.pet.adoption.common.R;
import com.yr.pet.adoption.common.PageResult;
import com.yr.pet.adoption.exception.BizException;
import com.yr.pet.adoption.exception.ErrorCode;
import com.yr.pet.adoption.mapper.TagMapper;
import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.model.entity.TagEntity;
import com.yr.pet.adoption.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签管理控制器
 * @author yr
 * @since 2026-02-15
 */
@RestController
@RequestMapping("/api/admin/tags")
@Tag(name = "标签管理", description = "标签字典管理接口")
@Validated
public class TagController {

    @Autowired
    private TagService tagService;

    /**
     * 获取标签列表（分页）
     */
    @GetMapping
    @Operation(summary = "获取标签列表", description = "获取标签字典列表，支持分页和条件筛选")
    public R<PageResult<TagDetailResponse>> getTagList(
            @Parameter(description = "页码，从1开始", example = "1")
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            
            @Parameter(description = "每页条数", example = "10")
            @RequestParam(defaultValue = "10") @Min(1) Integer pageSize,
            
            @Parameter(description = "标签类型：PERSONALITY/HEALTH/FEATURE", example = "PERSONALITY")
            @RequestParam(required = false) String tagType,
            
            @Parameter(description = "是否启用：0/1，默认1", example = "1")
            @RequestParam(required = false) Boolean enabled,
            
            @Parameter(description = "标签名称关键字", example = "温顺")
            @RequestParam(required = false) String keyword) {
        
        LambdaQueryWrapper<TagEntity> wrapper = new LambdaQueryWrapper<>();
        
        if (tagType != null) {
            wrapper.eq(TagEntity::getTagType, tagType);
        }
        
        if (enabled != null) {
            wrapper.eq(TagEntity::getEnabled, enabled);
        }
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(TagEntity::getName, keyword.trim());
        }
        
        wrapper.orderByAsc(TagEntity::getId);
        
        Page<TagEntity> pageParam = new Page<>(page, pageSize);
        IPage<TagEntity> result = tagService.page(pageParam, wrapper);
        
        List<TagDetailResponse> tagList = result.getRecords().stream()
            .map(this::convertToDetailResponse)
            .collect(Collectors.toList());
        
        PageResult<TagDetailResponse> pageResult = new PageResult<>();
        pageResult.setList(tagList);
        pageResult.setPageNo(page);
        pageResult.setPageSize(pageSize);
        pageResult.setTotal(result.getTotal());
        pageResult.setTotalPages((int) result.getPages());
        
        return R.ok(pageResult);
    }

    /**
     * 获取所有标签列表（不分页）
     */
    @GetMapping("/all")
    @Operation(summary = "获取所有标签", description = "获取所有启用的标签列表，不分页")
    public R<TagListResponse> getAllTags(
            @Parameter(description = "标签类型：PERSONALITY/HEALTH/FEATURE", example = "PERSONALITY")
            @RequestParam(required = false) String tagType) {
        
        LambdaQueryWrapper<TagEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TagEntity::getEnabled, true);
        
        if (tagType != null) {
            wrapper.eq(TagEntity::getTagType, tagType);
        }
        
        wrapper.orderByAsc(TagEntity::getId);
        
        List<TagEntity> tagEntities = tagService.list(wrapper);
        
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

    /**
     * 获取标签详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取标签详情", description = "根据ID获取标签详细信息")
    public R<TagDetailResponse> getTagDetail(
            @Parameter(description = "标签ID", example = "1")
            @PathVariable Long id) {
        
        TagEntity tag = tagService.getById(id);
        if (tag == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "标签不存在");
        }
        
        return R.ok(convertToDetailResponse(tag));
    }

    /**
     * 创建标签
     */
    @PostMapping
//    @PreAuthorize("hasAuthority('tag:create')")
    @Operation(summary = "创建标签", description = "创建新的标签")
    public R<TagDetailResponse> createTag(@Valid @RequestBody TagCreateRequest request) {
        // 检查标签名称是否已存在
        if (tagService.existsByName(request.getName())) {
            throw new BizException(ErrorCode.RESOURCE_EXIST, "标签名称已存在");
        }
        
        TagEntity tag = new TagEntity();
        tag.setName(request.getName());
        tag.setTagType(request.getTagType());
        tag.setEnabled(request.getEnabled());
        
        tagService.save(tag);
        
        return R.ok(convertToDetailResponse(tag));
    }

    /**
     * 更新标签
     */
    @PutMapping("/{id}")
//    @PreAuthorize("hasAuthority('tag:update')")
    @Operation(summary = "更新标签", description = "更新标签信息")
    public R<TagDetailResponse> updateTag(
            @Parameter(description = "标签ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody TagUpdateRequest request) {
        

        
        TagEntity tag = tagService.getById(id);
        if (tag == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "标签不存在");
        }
        
        // 检查标签名称是否已存在（排除当前标签）
        if (tagService.existsByNameExcludeId(request.getName(), id)) {
            throw new BizException(ErrorCode.RESOURCE_EXIST, "标签名称已存在");
        }
        
        tag.setName(request.getName());
        tag.setTagType(request.getTagType());
        tag.setEnabled(request.getEnabled());
        
        tagService.updateById(tag);
        
        return R.ok(convertToDetailResponse(tag));
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAuthority('tag:delete')")
    @Operation(summary = "删除标签", description = "删除标签（逻辑删除）")
    public R<Void> deleteTag(
            @Parameter(description = "标签ID", example = "1")
            @PathVariable Long id) {
        
        TagEntity tag = tagService.getById(id);
        if (tag == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "标签不存在");
        }
        
        tagService.removeById(id);
        
        return R.ok();
    }

    /**
     * 切换标签启用状态
     */
    @PatchMapping("/{id}/toggle")
//    @PreAuthorize("hasAuthority('tag:update')")
    @Operation(summary = "切换标签状态", description = "切换标签的启用/禁用状态")
    public R<TagDetailResponse> toggleTagStatus(
            @Parameter(description = "标签ID", example = "1")
            @PathVariable Long id) {
        
        TagEntity tag = tagService.getById(id);
        if (tag == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "标签不存在");
        }
        
        tag.setEnabled(!tag.getEnabled());
        tagService.updateById(tag);
        
        return R.ok(convertToDetailResponse(tag));
    }

    private TagDetailResponse convertToDetailResponse(TagEntity tag) {
        TagDetailResponse response = new TagDetailResponse();
        BeanUtils.copyProperties(tag, response);
        return response;
    }
}
