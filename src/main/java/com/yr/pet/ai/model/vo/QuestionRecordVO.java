package com.yr.pet.ai.model.vo;

import lombok.Data;

import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

@Data
public class QuestionRecordVO {

    private Long id;
    private Long userId;
    private Integer type;
    private Long sessionId;
    private Long questionId;
    private String reqText;
    private String respText;
    private Long createBy;
    private Date createTime;
}
