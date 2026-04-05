package com.yr.pet.adoption.service.impl;

import com.yr.pet.adoption.mapper.AdoptionFlowLogMapper;
import com.yr.pet.adoption.mapper.UserMapper;
import com.yr.pet.adoption.model.entity.AdoptionFlowLogEntity;
import com.yr.pet.adoption.model.vo.ApplicationFlowLogVO;
import com.yr.pet.adoption.service.AdoptionFlowLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 领养申请流程日志服务实现类
 * @author yr
 * @since 2026-01-01
 */
@Service
public class AdoptionFlowLogServiceImpl implements AdoptionFlowLogService {

    @Autowired
    private AdoptionFlowLogMapper adoptionFlowLogMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<ApplicationFlowLogVO> getApplicationLogs(Long applicationId) {
        List<AdoptionFlowLogEntity> logs = adoptionFlowLogMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<AdoptionFlowLogEntity>()
                        .eq("application_id", applicationId)
                        .orderByAsc("create_time")
        );

        return logs.stream().map(log -> {
            ApplicationFlowLogVO vo = new ApplicationFlowLogVO();
            vo.setId(log.getId());
            vo.setApplicationId(log.getApplicationId());
            vo.setFromStatus(log.getFromStatus());
            vo.setToStatus(log.getToStatus());
            vo.setOperatorId(log.getOperatorId());
            vo.setRemark(log.getRemark());
            vo.setCreateTime(log.getCreateTime());

            // 设置操作者信息
            String operatorName = userMapper.selectById(log.getOperatorId()) != null ? 
                    userMapper.selectById(log.getOperatorId()).getUsername() : "系统";
            vo.setOperatorName(operatorName);

            return vo;
        }).collect(Collectors.toList());
    }
}