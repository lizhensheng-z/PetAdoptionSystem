package com.yr.pet.adoption.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yr.pet.adoption.model.entity.CreditLogEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 信用日志Mapper接口
 * 对应数据库表：credit_log
 * 
 * @author 宗平
 * @since 2024-02-18
 */
@Mapper
public interface CreditLogMapper extends BaseMapper<CreditLogEntity> {

    /**
     * 分页查询用户的信用日志
     * 
     * @param page 分页对象
     * @param userId 用户ID
     * @param type 变动类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    IPage<CreditLogEntity> selectUserCreditLogs(
            Page<CreditLogEntity> page,
            @Param("userId") Long userId,
            @Param("type") String type,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * 获取用户的总打卡天数
     * 
     * @param userId 用户ID
     * @return 总打卡天数
     */
    Integer getTotalCheckinDays(@Param("userId") Long userId);

    /**
     * 获取用户的连续打卡天数
     * 
     * @param userId 用户ID
     * @return 连续打卡天数
     */
    Integer getConsecutiveCheckinDays(@Param("userId") Long userId);

    /**
     * 获取用户最后一次打卡日期
     * 
     * @param userId 用户ID
     * @return 最后打卡日期
     */
    LocalDateTime getLastCheckinDate(@Param("userId") Long userId);
}