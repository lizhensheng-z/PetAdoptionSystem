package com.yr.pet.adoption;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * MyBatis-Plus 代码生成器
 * 用于自动生成实体类、Mapper接口、Service接口及实现类、Controller类
 */
public class CodeGenerator {

    // 数据库连接配置
    private static final String DB_URL = "jdbc:mysql://localhost:3306/petAdoptionSystem?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "12345678";

    // 项目包配置
    private static final String PARENT_PACKAGE = "com.yr.pet.adoption";
    private static final String MODULE_NAME = "";

    // 作者信息
    private static final String AUTHOR = "榕";

    public static void main(String[] args) {
        generateCode();
    }

    /**
     * 执行代码生成
     */
    public static void generateCode() {
        String projectPath = System.getProperty("user.dir");
        String outputDir = projectPath + "/src/main/java";
        String xmlOutputDir = projectPath + "/src/main/resources/mapper";

        FastAutoGenerator.create(DB_URL, DB_USERNAME, DB_PASSWORD)
                // 全局配置
                .globalConfig(builder -> {
                    builder.author(AUTHOR)
                            .outputDir(outputDir)
                            .disableOpenDir()
                            .commentDate("yyyy-MM-dd");
//                            .fileOverride(); // 覆盖已生成文件
                })
                // 包配置
                .packageConfig(builder -> {
                    builder.parent(PARENT_PACKAGE)
                            .moduleName(MODULE_NAME)
                            .entity("entity")
                            .service("service")
                            .serviceImpl("service.impl")
                            .mapper("mapper")
                            .xml("mapper.xml")
                            .controller("controller")
                            .pathInfo(Collections.singletonMap(OutputFile.xml, xmlOutputDir));
                })
                // 策略配置
                .strategyConfig(builder -> {
                    builder.addInclude(getTables()) // 设置需要生成的表名
                            .addTablePrefix("sys_", "t_") // 设置过滤表前缀
                            .entityBuilder()
                            .enableLombok()
                            .enableTableFieldAnnotation()
                            .enableChainModel()
                            .enableActiveRecord()
                            .versionColumnName("version")
                            .logicDeleteColumnName("deleted")
                            .logicDeletePropertyName("deleted")
                            .naming(NamingStrategy.underline_to_camel)
                            .columnNaming(NamingStrategy.underline_to_camel)
                            .addTableFills(
                                    new Column("create_time", FieldFill.INSERT),
                                    new Column("update_time", FieldFill.INSERT_UPDATE)
                            )
                            .idType(IdType.AUTO)
                            .formatFileName("%sEntity")
                            .serviceBuilder()
                            .formatServiceFileName("%sService")
                            .formatServiceImplFileName("%sServiceImpl")
                            .mapperBuilder()
                            .superClass(BaseMapper.class)
                            .enableMapperAnnotation()
                            .enableBaseResultMap()
                            .enableBaseColumnList()
                            .formatMapperFileName("%sMapper")
                            .formatXmlFileName("%sMapper")
                            .controllerBuilder()
                            .enableHyphenStyle()
                            .enableRestStyle()
                            .formatFileName("%sController");
                })
                // 模板引擎配置，使用Freemarker
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();

        System.out.println("代码生成完成！");
    }

    /**
     * 获取需要生成的表名列表
     * 根据你的数据库结构，包含所有业务表
     */
    private static List<String> getTables() {
        return Arrays.asList(
                // 系统管理表
                "sys_user",
                "sys_role",
                "sys_permission",
                "sys_user_role",
                "sys_role_permission",
                "sys_config",
                "sys_notice",
                "audit_log",

                // 机构表
                "org_profile",

                // 宠物相关表
                "pet",
                "pet_media",
                "tag",
                "pet_tag",
                "pet_audit",

                // 领养流程表
                "adoption_application",
                "adoption_flow_log",

                // 用户行为表
                "user_favorite",
                "user_behavior",

                // 信用系统表
                "checkin_post",
                "credit_account",
                "credit_log"
        );
    }
}