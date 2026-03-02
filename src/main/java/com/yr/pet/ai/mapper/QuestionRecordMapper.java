package com.yr.pet.ai.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yr.pet.ai.model.entity.QuestionRecordDO;
import com.yr.pet.ai.model.vo.QuestionRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 李振生
 */
@Mapper
public interface QuestionRecordMapper extends BaseMapper<QuestionRecordDO> {
    // QuestionRecordMapper.java（MyBatis-Plus）
    List<QuestionRecordVO> queryDayDetails(@Param("userId") Long userId,
                                           @Param("day") String day);
}
