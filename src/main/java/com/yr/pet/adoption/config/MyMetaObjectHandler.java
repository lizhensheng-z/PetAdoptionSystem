package com.yr.pet.adoption.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 自动填充处理器
 * 用于自动填充实体类中的创建时间和更新时间字段
 *
 * @author yr
 * @since 2024-02-14
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时的自动填充策略
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("开始插入自动填充...");
        
        // 创建时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        
        // 更新时间（如果有的话）
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        
        log.debug("插入自动填充完成");
    }

    /**
     * 更新时的自动填充策略
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("开始更新自动填充...");
        
        // 更新时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        
        log.debug("更新自动填充完成");
    }
}