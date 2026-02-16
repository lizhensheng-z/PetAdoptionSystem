package com.yr.pet.adoption.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yr.pet.adoption.exception.BizException;
import com.yr.pet.adoption.exception.ErrorCode;
import com.yr.pet.adoption.model.dto.NoticeCreateRequest;
import com.yr.pet.adoption.model.dto.NoticeDetailResponse;
import com.yr.pet.adoption.model.dto.NoticeListRequest;
import com.yr.pet.adoption.model.dto.NoticeListResponse;
import com.yr.pet.adoption.model.dto.NoticeUpdateRequest;
import com.yr.pet.adoption.model.entity.NoticeEntity;
import com.yr.pet.adoption.mapper.NoticeMapper;
import com.yr.pet.adoption.service.NoticeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统公告表 服务实现类
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, NoticeEntity> implements NoticeService {

    @Override
    public IPage<NoticeListResponse> getNoticeList(NoticeListRequest request) {
        Page<NoticeEntity> page = new Page<>(request.getPageNo(), request.getPageSize());
        
        LambdaQueryWrapper<NoticeEntity> queryWrapper = new LambdaQueryWrapper<>();
        
        // 标题模糊搜索
        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            queryWrapper.like(NoticeEntity::getTitle, request.getTitle().trim());
        }
        
        // 状态筛选
        if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
            queryWrapper.eq(NoticeEntity::getStatus, request.getStatus().trim());
        }
        
        // 按创建时间降序排序
        queryWrapper.orderByDesc(NoticeEntity::getCreateTime);
        
        IPage<NoticeEntity> entityPage = this.page(page, queryWrapper);
        
        List<NoticeListResponse> responseList = entityPage.getRecords().stream()
                .map(this::convertToListResponse)
                .collect(Collectors.toList());
        
        Page<NoticeListResponse> responsePage = new Page<>(
                entityPage.getCurrent(),
                entityPage.getSize(),
                entityPage.getTotal()
        );
        responsePage.setRecords(responseList);
        
        return responsePage;
    }

    @Override
    public NoticeDetailResponse getNoticeDetail(Long id) {
        NoticeEntity entity = this.getById(id);
        if (entity == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "公告不存在");
        }
        return convertToDetailResponse(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createNotice(NoticeCreateRequest request) {
        NoticeEntity entity = new NoticeEntity();
        entity.setTitle(request.getTitle().trim());
        entity.setContent(request.getContent().trim());
        entity.setStatus(request.getStatus() != null ? request.getStatus() : "DRAFT");
        
        this.save(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNotice(Long id, NoticeUpdateRequest request) {
        NoticeEntity entity = this.getById(id);
        if (entity == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "公告不存在");
        }
        
        if ("REMOVED".equals(entity.getStatus())) {
            throw new BizException(ErrorCode.OPERATION_NOT_ALLOWED, "已删除的公告不能编辑");
        }
        
        entity.setTitle(request.getTitle().trim());
        entity.setContent(request.getContent().trim());
        entity.setStatus(request.getStatus());
        
        this.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotice(Long id) {
        NoticeEntity entity = this.getById(id);
        if (entity == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "公告不存在");
        }
        
        // 软删除，将状态改为REMOVED
        entity.setStatus("REMOVED");
        this.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNoticeStatus(Long id, String status) {
        NoticeEntity entity = this.getById(id);
        if (entity == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "公告不存在");
        }
        
        if ("REMOVED".equals(entity.getStatus()) && !"REMOVED".equals(status)) {
            throw new BizException(ErrorCode.OPERATION_NOT_ALLOWED, "已删除的公告不能恢复");
        }
        
        entity.setStatus(status);
        this.updateById(entity);
    }

    @Override
    public IPage<NoticeListResponse> getPublishedNoticeList(Integer pageNo, Integer pageSize) {
        Page<NoticeEntity> page = new Page<>(pageNo, pageSize);
        
        LambdaQueryWrapper<NoticeEntity> queryWrapper = new LambdaQueryWrapper<NoticeEntity>()
                .eq(NoticeEntity::getStatus, "PUBLISHED")
                .orderByDesc(NoticeEntity::getCreateTime);
        
        IPage<NoticeEntity> entityPage = this.page(page, queryWrapper);
        
        List<NoticeListResponse> responseList = entityPage.getRecords().stream()
                .map(this::convertToListResponse)
                .collect(Collectors.toList());
        
        Page<NoticeListResponse> responsePage = new Page<>(
                entityPage.getCurrent(),
                entityPage.getSize(),
                entityPage.getTotal()
        );
        responsePage.setRecords(responseList);
        
        return responsePage;
    }
    
    private NoticeListResponse convertToListResponse(NoticeEntity entity) {
        NoticeListResponse response = new NoticeListResponse();
        response.setId(entity.getId());
        response.setTitle(entity.getTitle());
        
        // 内容摘要，截取前100个字符
        String content = entity.getContent();
        if (content != null && content.length() > 100) {
            response.setContentSummary(content.substring(0, 100) + "...");
        } else {
            response.setContentSummary(content);
        }
        
        response.setStatus(entity.getStatus());
        response.setStatusText(getStatusText(entity.getStatus()));
        response.setCreateTime(entity.getCreateTime());
        response.setUpdateTime(entity.getUpdateTime());
        
        // 判断是否为7天内的新公告
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        response.setIsNew(entity.getCreateTime().isAfter(sevenDaysAgo));
        
        return response;
    }
    
    private NoticeDetailResponse convertToDetailResponse(NoticeEntity entity) {
        NoticeDetailResponse response = new NoticeDetailResponse();
        response.setId(entity.getId());
        response.setTitle(entity.getTitle());
        response.setContent(entity.getContent());
        response.setStatus(entity.getStatus());
        response.setStatusText(getStatusText(entity.getStatus()));
        response.setCreateTime(entity.getCreateTime());
        response.setUpdateTime(entity.getUpdateTime());
        return response;
    }
    
    private String getStatusText(String status) {
        switch (status) {
            case "DRAFT":
                return "草稿";
            case "PUBLISHED":
                return "已发布";
            case "REMOVED":
                return "已删除";
            default:
                return status;
        }
    }
}
