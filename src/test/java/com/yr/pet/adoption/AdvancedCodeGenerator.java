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
import java.util.Scanner;

/**
 * 高级MyBatis-Plus代码生成器
 * 支持交互式配置和更灵活的生成选项
 */
public class AdvancedCodeGenerator {

    // 默认配置
    private static final String DEFAULT_DB_URL = "jdbc:mysql://localhost:3306/petAdoptionSystem?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8";
    private static final String DEFAULT_DB_USERNAME = "root";
    private static final String DEFAULT_DB_PASSWORD = "root";
    private static final String DEFAULT_PARENT_PACKAGE = "com.yr.pet.adoption";
    private static final String AUTHOR = "宗平";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== MyBatis-Plus 代码生成器 ===");
        
        // 数据库配置
        System.out.print("数据库URL (默认: " + DEFAULT_DB_URL + "): ");
        String dbUrl = scanner.nextLine().trim();
        if (dbUrl.isEmpty()) dbUrl = DEFAULT_DB_URL;
        
        System.out.print("数据库用户名 (默认: " + DEFAULT_DB_USERNAME + "): ");
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) username = DEFAULT_DB_USERNAME;
        
        System.out.print("数据库密码 (默认: " + DEFAULT_DB_PASSWORD + "): ");
        String password = scanner.nextLine().trim();
        if (password.isEmpty()) password = DEFAULT_DB_PASSWORD;
        
        // 包配置
        System.out.print("父包名 (默认: " + DEFAULT_PARENT_PACKAGE + "): ");
        String parentPackage = scanner.nextLine().trim();
        if (parentPackage.isEmpty()) parentPackage = DEFAULT_PARENT_PACKAGE;
        
        System.out.print("是否生成所有表? (y/n, 默认y): ");
        String generateAll = scanner.nextLine().trim();
        boolean isAllTables = generateAll.isEmpty() || generateAll.equalsIgnoreCase("y");
        
        List<String> tables = null;
        if (!isAllTables) {
            System.out.print("请输入要生成的表名，用逗号分隔: ");
            String tableInput = scanner.nextLine().trim();
            tables = Arrays.asList(tableInput.split(","));
        } else {
            tables = getAllTables();
        }
        
        System.out.print("是否覆盖已存在文件? (y/n, 默认n): ");
        String override = scanner.nextLine().trim();
        boolean fileOverride = override.equalsIgnoreCase("y");
        
        generateCode(dbUrl, username, password, parentPackage, tables, fileOverride);
        
        scanner.close();
    }

    /**
     * 执行代码生成
     */
    public static void generateCode(String dbUrl, String username, String password, 
                                  String parentPackage, List<String> tables, boolean fileOverride) {
        String projectPath = System.getProperty("user.dir");
        String outputDir = projectPath + "/src/main/java";
        String xmlOutputDir = projectPath + "/src/main/resources/mapper";

        FastAutoGenerator.create(dbUrl, username, password)
                .globalConfig(builder -> {
                    builder.author(AUTHOR)
                            .outputDir(outputDir)
                            .disableOpenDir()
                            .commentDate("yyyy-MM-dd")
                            ;
                })
                .packageConfig(builder -> {
                    builder.parent(parentPackage)
                            .moduleName("")
                            .entity("entity")
                            .service("service")
                            .serviceImpl("service.impl")
                            .mapper("mapper")
                            .xml("mapper.xml")
                            .controller("controller")
                            .pathInfo(Collections.singletonMap(OutputFile.xml, xmlOutputDir));
                })
                .strategyConfig(builder -> {
                    builder.addInclude(tables)
                            .addTablePrefix("sys_", "t_")
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
                .templateConfig(builder -> {
                    // 使用自定义模板（如果需要）
                    // builder.entity("/templates/entity.java")
                    //        .service("/templates/service.java")
                    //        .serviceImpl("/templates/serviceImpl.java")
                    //        .mapper("/templates/mapper.java")
                    //        .xml("/templates/mapper.xml")
                    //        .controller("/templates/controller.java");
                })
                .injectionConfig(builder -> {
                    // 自定义注入配置
                    // builder.customMap(Collections.singletonMap("test", "baomidou"));
                })
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();

        System.out.println("代码生成完成！");
        System.out.println("生成路径: " + outputDir);
        System.out.println("Mapper XML路径: " + xmlOutputDir);
    }

    /**
     * 获取所有业务表名
     */
    private static List<String> getAllTables() {
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