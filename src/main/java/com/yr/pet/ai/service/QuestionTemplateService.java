package com.yr.pet.ai.service;


import cn.hutool.core.exceptions.ValidateException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yr.pet.adoption.common.UserContext;
import com.yr.pet.ai.mapper.QuestionTemplateMapper;
import com.yr.pet.ai.mapper.SessionMapper;
import com.yr.pet.ai.model.dto.QuestionTemplateDTO;
import com.yr.pet.ai.model.dto.TemplatePageQueryDTO;
import com.yr.pet.ai.model.entity.QuestionRecordDO;
import com.yr.pet.ai.model.entity.QuestionTemplateDO;
import com.yr.pet.ai.model.entity.SessionDO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 李振生
 */
@Service
@Slf4j
public class QuestionTemplateService extends ServiceImpl<QuestionTemplateMapper, QuestionTemplateDO> {
    @Resource
    private DeepSeekClient deepSeekClient;
    @Resource
    private QuestionRecordService questionRecordService;
    @Resource
    private SessionMapper sessionMapper;
    /**
     * 批量新增问题模板（带事务）
     * @param dtoList 待新增的数据列表
     * @return 保存后的实体列表（包含数据库生成的ID）
     */
    @Transactional(rollbackFor = Exception.class)
    public List<QuestionTemplateDO> batchCreate(List<QuestionTemplateDTO> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            return new ArrayList<>();
        }
        List<QuestionTemplateDO> entityList = new ArrayList<>(dtoList.size());
        for (QuestionTemplateDTO dto : dtoList) {
            QuestionTemplateDO entity = new QuestionTemplateDO();
            entity.setQuestion(dto.getQuestion());
            entity.setAnswer(dto.getAnswer());
            entity.setType(dto.getType());
            if(dto.getSortNo()!=null){
                entity.setSortNo(dto.getSortNo());
            }
            entity.setIfDelete(false); // 默认未删除

            entity.setCreateBy(UserContext.getUserId());
            entity.setCreateTime(new Date());
            entityList.add(entity);
        }
        // MyBatis-Plus 批量保存
        this.saveBatch(entityList);
        return entityList;
    }

    public void updateTemplate(QuestionTemplateDTO questionTemplateDTO) {
        QuestionTemplateDO one = this.getOne(new LambdaQueryWrapper<QuestionTemplateDO>().eq(QuestionTemplateDO::getId, questionTemplateDTO.getId()));
        if (one == null) {          // 数据不存在直接返回/抛异常
            throw new ValidateException("模板不存在");
        }
        if(one!=null){
            one.setQuestion(questionTemplateDTO.getQuestion());
            one.setAnswer(questionTemplateDTO.getAnswer());
            if(questionTemplateDTO.getType()!=null)
                one.setType(questionTemplateDTO.getType());
            if(questionTemplateDTO.getSortNo()!=null)
                one.setSortNo(questionTemplateDTO.getSortNo());
        }
        this.updateById(one);
    }

    public void deleteTemplate(Long id) {
        this.remove(new LambdaQueryWrapper<QuestionTemplateDO>().eq(QuestionTemplateDO::getId, id));
    }


    public Page<QuestionTemplateDO> pageQuestionTemplate(TemplatePageQueryDTO param) {
        Page<QuestionTemplateDO> page = new Page<>(param.getPageNum(), param.getPageSize());
        LambdaQueryWrapper<QuestionTemplateDO> queryWrapper = new LambdaQueryWrapper<>();
        // 添加查询条件
        if (param.getType() != null) {
            queryWrapper.eq(QuestionTemplateDO::getType, param.getType());
        }
        if (param.getQuestion() != null) {
            queryWrapper.like(QuestionTemplateDO::getQuestion, param.getQuestion());
        }
        // 排序
        queryWrapper.orderByAsc(QuestionTemplateDO::getSortNo);
        return this.page(page, queryWrapper);
    }

    public String selectByQuestion(String content,Long sessionId) {
        LambdaQueryWrapper<QuestionTemplateDO> eq = new LambdaQueryWrapper<QuestionTemplateDO>()
                .eq(QuestionTemplateDO::getQuestion, content);
        QuestionTemplateDO one = this.getOne(eq);
        if (one != null) {
            //创建会话
            SessionDO sessionDO1 = new SessionDO();
            sessionDO1.setTitle(content.length() > 10 ? content.substring(0,10) : content);
            sessionDO1.setIfDelete( false);
            sessionDO1.setCreateBy(UserContext.getUserId());
            sessionDO1.setCreateTime(new Date());
            sessionMapper.insert(sessionDO1);


            //记录用户 问题  答案到历史记录表
            //模板问题请求参数 --使用null代替 不属于真实提问，构建了影响ai回答的上下文
           Long questionRequestId = questionRecordService.saveUserQuestionRecord(content, sessionId, null);
            questionRecordService.saveQuestionTemplateAnswer(
                    questionRequestId,
                    one.getAnswer(),
                    QuestionRecordDO.STATUS_SUCCESS,
                    QuestionRecordDO.RESPONSE_SUCCESS
            );
            return one.getAnswer();
        }
        return null;
    }

    public QuestionTemplateDO getQuestionTemplateById(Long id) {
        LambdaQueryWrapper<QuestionTemplateDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionTemplateDO::getId, id);
        return this.getOne(wrapper);
    }

    public QuestionTemplateDO getDisclaimer() {
        return lambdaQuery()
                .eq(QuestionTemplateDO::getType, 2)
                .orderByDesc(QuestionTemplateDO::getId)
                .last("LIMIT 1")
                .one();
    }
}
