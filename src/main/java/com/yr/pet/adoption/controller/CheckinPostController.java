package com.yr.pet.adoption.controller;

import com.yr.pet.adoption.common.PageResult;
import com.yr.pet.adoption.model.dto.*;
import com.yr.pet.adoption.service.CheckinPostService;
import com.yr.pet.adoption.common.R;
import com.yr.pet.adoption.common.UserContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * <p>
 * 打卡与信用接口控制器
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@RestController
@RequestMapping("/api")
@Tag(name = "打卡管理", description = "领养后打卡相关接口")
public class CheckinPostController {

    @Autowired
    private CheckinPostService checkinPostService;

    /**
     * 创建领养后打卡
     */
    @PostMapping("/checkins")
    @PreAuthorize("hasAuthority('checkin:create')")
    @Operation(summary = "创建打卡", description = "领养后创建打卡记录，同时计算信用分")
    public R<CheckinResponse> createCheckin(@Valid @RequestBody CheckinCreateRequest request) {
        Long userId = UserContext.getUserId();
        CheckinResponse response = checkinPostService.createCheckin(userId, request);
        return R.ok(response);
    }

    /**
     * 获取我的打卡列表
     */
    @GetMapping("/checkins/my")
    @PreAuthorize("hasAuthority('checkin:my')")
    @Operation(summary = "获取我的打卡列表", description = "获取当前用户的打卡记录列表")
    public R<PageResult<CheckinListItem>> getMyCheckins(
            @RequestParam(required = false) Long petId,
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = UserContext.getUserId();
        PageResult<CheckinListItem> result = checkinPostService.getMyCheckins(userId, petId, pageNo, pageSize);
        return R.ok(result);
    }

    /**
     * 获取打卡详情
     */
    @GetMapping("/checkins/{checkinId}")
    @Operation(summary = "获取打卡详情", description = "获取指定打卡记录的详细信息")
    public R<CheckinDetailResponse> getCheckinDetail(@PathVariable Long checkinId) {
        Long userId = UserContext.getUserId();
        CheckinDetailResponse response = checkinPostService.getCheckinDetail(userId, checkinId);
        return R.ok(response);
    }

    /**
     * 删除打卡
     */
    @DeleteMapping("/checkins/{checkinId}")
    @Operation(summary = "删除打卡", description = "删除指定的打卡记录")
    public R deleteCheckin(@PathVariable Long checkinId) {
        Long userId = UserContext.getUserId();
        checkinPostService.deleteCheckin(userId, checkinId);
        return R.ok("删除成功");
    }
}
