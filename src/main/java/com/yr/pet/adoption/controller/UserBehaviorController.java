package com.yr.pet.adoption.controller;

import com.yr.pet.adoption.model.dto.BehaviorRecordRequest;
import com.yr.pet.adoption.service.UserBehaviorService;
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
 * 用户行为埋点接口控制器
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@RestController
@RequestMapping("/api")
@Tag(name = "行为埋点", description = "用户行为记录相关接口")
public class UserBehaviorController {

    @Autowired
    private UserBehaviorService userBehaviorService;

    /**
     * 记录用户行为
     */
    @PostMapping("/behavior")
    @PreAuthorize("hasAuthority('behavior:write')")
    @Operation(summary = "记录用户行为", description = "记录用户的浏览、收藏、申请等行为用于推荐")
    public R recordBehavior(@Valid @RequestBody BehaviorRecordRequest request) {
        Long userId = UserContext.getUserId();
        userBehaviorService.recordBehavior(userId, request);
        return R.ok("行为记录成功");
    }
}
