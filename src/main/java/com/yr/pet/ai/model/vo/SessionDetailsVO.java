package com.yr.pet.ai.model.vo;


import com.yr.pet.ai.model.entity.SessionDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SessionDetailsVO extends SessionDO {

   //包含session全部信息 + 会话下 用户与ai的全部对话记录
   // 一个会话下有多条 问+答 记录
    private  List<QuestionRecordVO> questionRecordVOS;

}
