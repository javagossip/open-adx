@echo off
setlocal enabledelayedexpansion

:: Open-ADX 项目构建脚本 (Windows版本)

set "PROJECT_ROOT=%~dp0"
set "PROFILE=default"
set "SKIP_TESTS=false"
set "CLEAN=false"
set "SKIP_GPG=false"
set "MODULE=all"

:: 解析参数
:parse_args
if "%1"=="" goto main
if "%1"=="-h" goto show_help
if "%1"=="--help" goto show_help
if "%1"=="-p" (
    set "PROFILE=%2"
    shift
    shift
    goto parse_args
)
if "%1"=="--profile" (
    set "PROFILE=%2"
    shift
    shift
    goto parse_args
)
if "%1"=="-s" (
    set "SKIP_TESTS=true"
    shift
    goto parse_args
)
if "%1"=="--skip-tests" (
    set "SKIP_TESTS=true"
    shift
    goto parse_args
)
if "%1"=="-c" (
    set "CLEAN=true"
    shift
    goto parse_args
)
if "%1"=="--clean" (
    set "CLEAN=true"
    shift
    goto parse_args
)
if "%1"=="--skip-gpg" (
    set "SKIP_GPG=true"
    shift
    goto parse_args
)
if "%1"=="mos" (
    set "MODULE=mos"
    shift
    goto parse_args
)
if "%1"=="dsp-api" (
    set "MODULE=dsp-api"
    shift
    goto parse_args
)
if "%1"=="ssp-api" (
    set "MODULE=ssp-api"
    shift
    goto parse_args
)
if "%1"=="tracking-api" (
    set "MODULE=tracking-api"
    shift
    goto parse_args
)
if "%1"=="billing" (
    set "MODULE=billing"
    shift
    goto parse_args
)
if "%1"=="job" (
    set "MODULE=job"
    shift
    goto parse_args
)
echo 未知参数: %1
goto show_help

:show_help
echo Open-ADX 项目构建脚本 (Windows版本)
echo.
echo 用法: %0 [选项] [模块名]
echo.
echo 选项:
echo   -h, --help          显示帮助信息
echo   -p, --profile PROFILE  指定构建profile (默认: default)
echo   -s, --skip-tests    跳过测试
echo   -c, --clean         清理后再构建
echo   --skip-gpg          跳过GPG签名
echo.
echo 可用的profile:
echo   default             默认构建(打包jar包)
echo   docker              构建docker镜像
echo.
echo 可用的模块:
echo   all                 构建所有模块(默认)
echo   mos                 构建MOS模块
echo   dsp-api             构建DSP API模块
echo   ssp-api             构建SSP API模块
echo   tracking-api        构建Tracking API模块
echo   billing             构建Billing模块
echo   job                 构建Job模块
echo.
echo 示例:
echo   %0                          # 构建所有模块(默认)
echo   %0 mos                      # 只构建MOS模块
echo   %0 -p docker                # 使用docker profile构建所有模块
echo   %0 -p docker mos            # 使用docker profile只构建MOS模块
echo   %0 -c -s                    # 清理并跳过测试构建所有模块
exit /b 0

:main
echo ========================================
echo Open-ADX 项目构建
echo ========================================
echo Profile: %PROFILE%
echo 模块: %MODULE%
echo 跳过测试: %SKIP_TESTS%
echo 清理构建: %CLEAN%
echo 跳过GPG签名: %SKIP_GPG%
echo ========================================

:: 构建参数组装
set "BUILD_ARGS="
if "%CLEAN%"=="true" (
    set "BUILD_ARGS=%BUILD_ARGS% clean"
)
set "BUILD_ARGS=%BUILD_ARGS% install"
if "%SKIP_TESTS%"=="true" (
    set "BUILD_ARGS=%BUILD_ARGS% -DskipTests"
)
if "%SKIP_GPG%"=="true" (
    set "BUILD_ARGS=%BUILD_ARGS% -Dgpg.skip"
)

:: 根据profile添加参数
if "%PROFILE%"=="docker" (
    set "BUILD_ARGS=%BUILD_ARGS% -Pdocker -Denv.id=docker"
)

:: Maven命令
set "MVN_CMD=%PROJECT_ROOT%mvnw.cmd"

:: 进入项目根目录
cd /d "%PROJECT_ROOT%"

:: 执行构建
echo 开始构建...
"%MVN_CMD%" %BUILD_ARGS%

if %ERRORLEVEL% equ 0 (
    echo 构建成功!
) else (
    echo 构建失败!
    exit /b 1
)

echo ========================================