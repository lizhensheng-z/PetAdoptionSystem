package com.yr.pet.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.yr.pet.ai.model.entity.SessionDO;
import com.yr.pet.ai.model.vo.QuestionRecordVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author 李振生
 */
@Mapper
public interface SessionMapper extends BaseMapper<SessionDO> {
    List<QuestionRecordVO> getQuestionRecordsBySessionId(Long sessionId, Long userId);


}
