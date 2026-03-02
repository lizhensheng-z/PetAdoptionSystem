package com.yr.pet.ai.config;

import com.alibaba.fastjson.JSON;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractEncoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * Reactive Encoder using FastJSON for WebClient request body serialization.
 */
public class FastJsonEncoder extends AbstractEncoder<Object> {

    public FastJsonEncoder() {
        super(MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
    }

    @Override
    public Flux<DataBuffer> encode(Publisher<?> inputStream,
                                   DataBufferFactory bufferFactory,
                                   ResolvableType elementType,
                                   MimeType mimeType,
                                   Map<String, Object> hints) {
        return Flux.from(inputStream)
                .map(value -> encodeValue(value, bufferFactory, elementType, mimeType, hints));
    }

    @Override
    public DataBuffer encodeValue(Object value,
                                  DataBufferFactory bufferFactory,
                                  ResolvableType valueType,
                                  MimeType mimeType,
                                  Map<String, Object> hints) {
        byte[] bytes = JSON.toJSONBytes(value);
        return bufferFactory.wrap(bytes);
    }

    @Override
    public boolean canEncode(ResolvableType elementType, MimeType mimeType) {
        return mimeType == null || MediaType.APPLICATION_JSON.isCompatibleWith(MediaType.asMediaType(mimeType));
    }
}
