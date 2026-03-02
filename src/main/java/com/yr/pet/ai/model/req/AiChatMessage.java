package com.yr.pet.ai.model.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiChatMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    //写死不能变
    public static final String ROLE_SYSTEM = "system";
    public static final String ROLE_ASSISTANT= "assistant";
    public static final String ROLE_USER = "user";
    //能变
    public static final String CONTENT_SYSTEM = "请扮演法律援助小助手的角色，当有人问你是谁时，请告诉用户你是法律援助小助手，专门提供法律咨询和帮助。你的职责是解答与法律相关的问题，提供基本的法律信息和指导，但不提供具体的法律意见或服务。请只提供文字回复，不要附带图片、视频或其他非文字内容。";
    private String role;
    private String content;
}
