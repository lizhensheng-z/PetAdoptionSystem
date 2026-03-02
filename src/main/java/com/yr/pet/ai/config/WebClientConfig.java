package com.yr.pet.ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.DecoderHttpMessageReader;
import org.springframework.http.codec.EncoderHttpMessageWriter;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(this::configureCodecs)
                .build();
        return WebClient.builder()
                .exchangeStrategies(strategies)
                .build();
    }

    private void configureCodecs(ClientCodecConfigurer codecConfigurer) {
        FastJsonEncoder fastJsonEncoder = new FastJsonEncoder();
        FastJsonDecoder fastJsonDecoder = new FastJsonDecoder();
        codecConfigurer.customCodecs().register(new EncoderHttpMessageWriter<>(fastJsonEncoder));
        codecConfigurer.customCodecs().register(new DecoderHttpMessageReader<>(fastJsonDecoder));
        // 可选：调整内存限制
        codecConfigurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024);
    }
}
