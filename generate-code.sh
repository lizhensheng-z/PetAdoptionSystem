#!/bin/bash

# MyBatis-Plus代码生成器运行脚本
# PetAdoptionSystem代码生成器

echo "=========================================="
echo "  PetAdoptionSystem 代码生成器"
echo "=========================================="
echo ""

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "❌ 错误：未找到Java环境，请先安装Java 17+"
    exit 1
fi

# 检查Maven环境
if ! command -v mvn &> /dev/null; then
    echo "❌ 错误：未找到Maven环境，请先安装Maven"
    exit 1
fi

echo "请选择代码生成方式："
echo "1) 一键生成所有表代码（推荐）"
echo "2) 交互式生成（自定义配置）"
echo "3) 基础生成器"
echo "4) 查看使用说明"
echo "5) 退出"
echo ""

read -p "请输入选项 [1-5]: " choice

case $choice in
    1)
        echo "🚀 开始一键生成所有表代码..."
        mvn test-compile exec:java -Dexec.mainClass="com.yr.pet.adoption.SimpleCodeGenerator"
        ;;
    2)
        echo "🔧 启动交互式代码生成器..."
        mvn test-compile exec:java -Dexec.mainClass="com.yr.pet.adoption.AdvancedCodeGenerator"
        ;;
    3)
        echo "📋 启动基础代码生成器..."
        mvn test-compile exec:java -Dexec.mainClass="com.yr.pet.adoption.CodeGenerator"
        ;;
    4)
        echo "📖 查看使用说明..."
        if [ -f "CODE_GENERATOR_GUIDE.md" ]; then
            cat CODE_GENERATOR_GUIDE.md
        else
            echo "使用说明文件不存在，请查看项目文档"
        fi
        ;;
    5)
        echo "👋 再见！"
        exit 0
        ;;
    *)
        echo "❌ 无效选项，请输入1-5之间的数字"
        exit 1
        ;;
esac

echo ""
echo "=========================================="
echo "  代码生成完成！"
echo "=========================================="