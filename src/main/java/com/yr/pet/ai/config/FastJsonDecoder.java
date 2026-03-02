package com.yr.pet.ai.config;

import com.alibaba.fastjson.JSON;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractDecoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;


public class FastJsonDecoder extends AbstractDecoder<Object> {

    public FastJsonDecoder() {
        super(MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
    }

    @Override
    public Flux<Object> decode(Publisher<DataBuffer> inputStream,
                               ResolvableType elementType,
                               MimeType mimeType,
                               Map<String, Object> hints) {
        return Flux.from(inputStream).map(dataBuffer -> decodeDataBuffer(dataBuffer, elementType));
    }

    @Override
    public Mono<Object> decodeToMono(Publisher<DataBuffer> inputStream,
                                     ResolvableType elementType,
                                     MimeType mimeType,
                                     Map<String, Object> hints) {
        return Flux.from(inputStream)
                .reduce(this::join)
                .map(buffer -> decodeDataBuffer(buffer, elementType));
    }

    private DataBuffer join(DataBuffer previous, DataBuffer next) {
        DataBuffer combined = previous.factory().allocateBuffer(previous.readableByteCount() + next.readableByteCount());
        combined.write(previous);
        combined.write(next);
        DataBufferUtils.release(previous);
        DataBufferUtils.release(next);
        return combined;
    }

    private Object decodeDataBuffer(DataBuffer dataBuffer, ResolvableType elementType) {
        try {
            byte[] bytes = new byte[dataBuffer.readableByteCount()];
            dataBuffer.read(bytes);
            Class<?> clazz = elementType.toClass();
            return JSON.parseObject(bytes, clazz);
        } finally {
            DataBufferUtils.release(dataBuffer);
        }
    }

    @Override
    public boolean canDecode(ResolvableType elementType, MimeType mimeType) {
        return mimeType == null || MediaType.APPLICATION_JSON.isCompatibleWith(MediaType.asMediaType(mimeType));
    }
}
