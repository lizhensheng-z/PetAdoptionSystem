package com.yr.pet.ai.model.vo;


import com.yr.pet.ai.model.entity.SessionDO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DaySessionVO {
    private String day;
    private List<SessionDO> sessions;  // 当天所有会话
}