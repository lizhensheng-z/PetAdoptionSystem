package com.yr.pet.ai.model.req;

import lombok.Data;
import java.util.List;
/**
 * AI聊天请求参数 request
 */
@Data
public class AiChatRequest {
    public static  final String DEFAULT_MODEL = "deepseek-chat";
    public static  final Integer DEFAULT_MAX_TOKENS = 2048;
    //temperature 创意写作 0.7‒1.2
    public static  final Double DEFAULT_TEMPERATURE = 0.7;
    public static final boolean OPEN_STREAM = true;
    private String model;

    private List<AiChatMessage> messages;

    private boolean stream;

    private Integer max_tokens;

    private Double temperature;

}
