package com.yr.pet.ai.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class DeepSeekConfig {


    @Value("${ai.deepseek.api-key}")
    private String apiKey;


    public static final String BASE_URL = "https://api.deepseek.com";
    public static final String BALANCE_URI = "/user/balance";
    public static final String URI = "/chat/completions";

    public static final String HEADER = "Authorization";
    public static final String BEARER = "Bearer ";
}
