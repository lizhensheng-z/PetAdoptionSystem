package com.yr.pet.adoption.config;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 全局配置类
 *
 * 功能：
 * 1. 分页插件配置
 * 2. 逻辑删除配置
 * 3. 乐观锁配置
 * 4. 防全表更新删除插件
 * 5. SQL 性能分析（开发环境）
 */
@Slf4j
@Configuration
@MapperScan("com.yr.pet.adoption.mapper")  // 扫描 mapper 包
public class MybatisPlusConfig {

    /**
     * MyBatis-Plus 拦截器链配置
     * 注意：插件顺序很重要！
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 1. 分页插件（必须）- 支持多种数据库分页
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        // 设置单页最大限制数量，默认 500 条，-1 不受限
        paginationInnerInterceptor.setMaxLimit(500L);
        // 开启 count 的 join 优化,只针对部分 left join
        paginationInnerInterceptor.setOptimizeJoin(true);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);

        // 2. 乐观锁插件（可选，仅在使用 @Version 注解时生效）
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        // 3. 防全表更新/删除插件（推荐生产环境启用）
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        log.info("✅ MyBatis-Plus 拦截器链初始化完成");
        return interceptor;
    }


}
