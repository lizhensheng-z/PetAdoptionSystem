package com.yr.pet.adoption.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yr.pet.adoption.common.R;
import com.yr.pet.adoption.exception.ErrorCode;
import com.yr.pet.adoption.model.dto.NoticeCreateRequest;
import com.yr.pet.adoption.model.dto.NoticeDetailResponse;
import com.yr.pet.adoption.model.dto.NoticeListRequest;
import com.yr.pet.adoption.model.dto.NoticeListResponse;
import com.yr.pet.adoption.model.dto.NoticeStatusUpdateRequest;
import com.yr.pet.adoption.model.dto.NoticeUpdateRequest;
import com.yr.pet.adoption.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 公告管理控制器
 * 提供公告的增删改查接口
 *
 * @author 榕
 * @since 2026-02-01
 */
@RestController
@RequestMapping("/api")
@Tag(name = "公告管理", description = "系统公告管理相关接口")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    /**
     * 获取公告列表（管理端）
     */
    @GetMapping("/admin/notices")
//    @PreAuthorize("hasAuthority('admin:notice:view')")
    @Operation(summary = "获取公告列表", description = "获取公告列表，支持分页和筛选")
    public R<IPage<NoticeListResponse>> getNoticeList(@Validated NoticeListRequest request) {
        IPage<NoticeListResponse> result = noticeService.getNoticeList(request);
        return R.ok(result);
    }

    /**
     * 获取公告详情（管理端）
     */
    @GetMapping("/admin/notices/{id}")
//    @PreAuthorize("hasAuthority('admin:notice:view')")
    @Operation(summary = "获取公告详情", description = "根据ID获取公告详情")
    public R<NoticeDetailResponse> getNoticeDetail(@PathVariable Long id) {
        NoticeDetailResponse detail = noticeService.getNoticeDetail(id);
        return R.ok(detail);
    }

    /**
     * 创建公告
     */
    @PostMapping("/admin/notices")
//    @PreAuthorize("hasAuthority('admin:notice:create')")
    @Operation(summary = "创建公告", description = "创建新公告")
    public R<Long> createNotice(@Validated @RequestBody NoticeCreateRequest request) {
        Long noticeId = noticeService.createNotice(request);
        return R.ok(noticeId);
    }

    /**
     * 更新公告
     */
    @PutMapping("/admin/notices/{id}")
//    @PreAuthorize("hasAuthority('admin:notice:update')")
    @Operation(summary = "更新公告", description = "更新公告信息")
    public R<Void> updateNotice(@PathVariable Long id, @Validated @RequestBody NoticeUpdateRequest request) {
        noticeService.updateNotice(id, request);
        return R.ok();
    }

    /**
     * 删除公告（软删除）
     */
    @DeleteMapping("/admin/notices/{id}")
//    @PreAuthorize("hasAuthority('admin:notice:delete')")
    @Operation(summary = "删除公告", description = "软删除公告，将状态改为REMOVED")
    public R<Void> deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return R.ok();
    }

    /**
     * 更新公告状态
     */
    @PatchMapping("/admin/notices/{id}/status")
//    @PreAuthorize("hasAuthority('admin:notice:update')")
    @Operation(summary = "更新公告状态", description = "修改公告状态（发布/下架）")
    public R<Void> updateNoticeStatus(@PathVariable Long id, @Validated @RequestBody NoticeStatusUpdateRequest request) {
        noticeService.updateNoticeStatus(id, request.getStatus());
        return R.ok();
    }

    /**
     * 获取已发布公告列表（用户端）
     */
    @GetMapping("/notices")
    @Operation(summary = "获取已发布公告列表", description = "获取所有已发布的公告列表（用户端）")
    public R<IPage<NoticeListResponse>> getPublishedNoticeList(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<NoticeListResponse> result = noticeService.getPublishedNoticeList(pageNo, pageSize);
        return R.ok(result);
    }

    /**
     * 获取公告详情（用户端）
     */
    @GetMapping("/notices/{id}")
    @Operation(summary = "获取公告详情", description = "获取公告详情（用户端）")
    public R<NoticeDetailResponse> getUserNoticeDetail(@PathVariable Long id) {
        NoticeDetailResponse detail = noticeService.getNoticeDetail(id);
        // 只返回已发布的公告
        if (!"PUBLISHED".equals(detail.getStatus())) {
            return R.fail(ErrorCode.NOT_FOUND, "公告不存在或已下架");
        }
        return R.ok(detail);
    }
}
