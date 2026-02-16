package com.yr.pet.adoption.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yr.pet.adoption.model.dto.NoticeCreateRequest;
import com.yr.pet.adoption.model.dto.NoticeDetailResponse;
import com.yr.pet.adoption.model.dto.NoticeListRequest;
import com.yr.pet.adoption.model.dto.NoticeListResponse;
import com.yr.pet.adoption.model.dto.NoticeUpdateRequest;
import com.yr.pet.adoption.model.entity.NoticeEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 系统公告表 服务类
 * </p>
 *
 * @author 榕
 * @since 2026-02-01
 */
public interface NoticeService extends IService<NoticeEntity> {

    /**
     * 获取公告列表（分页）
     * @param request 查询参数
     * @return 分页公告列表
     */
    IPage<NoticeListResponse> getNoticeList(NoticeListRequest request);

    /**
     * 获取公告详情
     * @param id 公告ID
     * @return 公告详情
     */
    NoticeDetailResponse getNoticeDetail(Long id);

    /**
     * 创建公告
     * @param request 创建参数
     * @return 创建后的公告ID
     */
    Long createNotice(NoticeCreateRequest request);

    /**
     * 更新公告
     * @param id 公告ID
     * @param request 更新参数
     */
    void updateNotice(Long id, NoticeUpdateRequest request);

    /**
     * 删除公告（软删除）
     * @param id 公告ID
     */
    void deleteNotice(Long id);

    /**
     * 更新公告状态
     * @param id 公告ID
     * @param status 新状态
     */
    void updateNoticeStatus(Long id, String status);

    /**
     * 获取已发布公告列表（用户端）
     * @param pageNo 页码
     * @param pageSize 每页条数
     * @return 分页公告列表
     */
    IPage<NoticeListResponse> getPublishedNoticeList(Integer pageNo, Integer pageSize);
}
