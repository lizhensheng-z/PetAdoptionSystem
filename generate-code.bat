@echo off
chcp 65001

REM MyBatis-Plus代码生成器运行脚本（Windows版）
REM PetAdoptionSystem代码生成器

echo ==========================================
echo   PetAdoptionSystem 代码生成器
echo ==========================================
echo.

REM 检查Java环境
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ 错误：未找到Java环境，请先安装Java 17+
    pause
    exit /b 1
)

REM 检查Maven环境
mvn -version >nul 2>&1
if errorlevel 1 (
    echo ❌ 错误：未找到Maven环境，请先安装Maven
    pause
    exit /b 1
)

:menu
echo 请选择代码生成方式：
echo 1) 一键生成所有表代码（推荐）
echo 2) 交互式生成（自定义配置）
echo 3) 基础生成器
echo 4) 查看使用说明
echo 5) 退出
echo.

set /p choice=请输入选项 [1-5]: 

if "%choice%"=="1" (
    echo 🚀 开始一键生成所有表代码...
    mvn test-compile exec:java -D"exec.mainClass"="com.yr.pet.adoption.SimpleCodeGenerator"
    goto :complete
)

if "%choice%"=="2" (
    echo 🔧 启动交互式代码生成器...
    mvn test-compile exec:java -D"exec.mainClass"="com.yr.pet.adoption.AdvancedCodeGenerator"
    goto :complete
)

if "%choice%"=="3" (
    echo 📋 启动基础代码生成器...
    mvn test-compile exec:java -D"exec.mainClass"="com.yr.pet.adoption.CodeGenerator"
    goto :complete
)

if "%choice%"=="4" (
    echo 📖 查看使用说明...
    if exist "CODE_GENERATOR_GUIDE.md" (
        type CODE_GENERATOR_GUIDE.md
    ) else (
        echo 使用说明文件不存在，请查看项目文档
    )
    pause
    goto :menu
)

if "%choice%"=="5" (
    echo 👋 再见！
    pause
    exit /b 0
)

echo ❌ 无效选项，请输入1-5之间的数字
goto :menu

:complete
echo.
echo ==========================================
echo   代码生成完成！
echo ==========================================
pause