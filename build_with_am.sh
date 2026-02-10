#!/bin/bash

# Open-ADX 项目构建脚本 (支持-am参数)
# 当构建独立模块时自动添加-am参数构建依赖模块

set -e

# 颜色输出定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 项目根目录
PROJECT_ROOT=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)

# Maven命令（使用绝对路径）
MVN_CMD="$PROJECT_ROOT/mvnw"

# 显示帮助信息
show_help() {
    echo "Open-ADX 项目构建脚本"
    echo ""
    echo "用法: $0 [选项] [模块名]"
    echo ""
    echo "选项:"
    echo "  -h, --help          显示此帮助信息"
    echo "  -p, --profile PROFILE  指定构建profile"
    echo "  -s, --skip-tests    跳过测试"
    echo "  -c, --clean         清理后再构建"
    echo "  --skip-gpg          跳过GPG签名"
    echo ""
    echo "可用模块: all mos dsp-api ssp-api tracking-api billing job"
}

# 解析命令行参数
PROFILE="default"
SKIP_TESTS=false
CLEAN=false
SKIP_GPG=false
MODULE="all"

while [[ $# -gt 0 ]]; do
    case "$1" in
        -h|--help) show_help; exit 0 ;;
        -p|--profile) PROFILE="$2"; shift 2 ;;
        -s|--skip-tests) SKIP_TESTS=true; shift ;;
        -c|--clean) CLEAN=true; shift ;;
        --skip-gpg) SKIP_GPG=true; shift ;;
        mos|dsp-api|ssp-api|tracking-api|billing|job) MODULE="$1"; shift ;;
        *) echo -e "${RED}未知参数: $1${NC}"; show_help; exit 1 ;;
    esac
done

# 构建参数组装
BUILD_ARGS=""
[[ "$CLEAN" == true ]] && BUILD_ARGS="$BUILD_ARGS clean"
BUILD_ARGS="$BUILD_ARGS install"
[[ "$SKIP_TESTS" == true ]] && BUILD_ARGS="$BUILD_ARGS -DskipTests"
[[ "$SKIP_GPG" == true ]] && BUILD_ARGS="$BUILD_ARGS -Dgpg.skip"

# Profile参数
case "$PROFILE" in
    docker) BUILD_ARGS="$BUILD_ARGS -Pdocker -Denv.id=docker" ;;
    default) ;;
    *) echo -e "${RED}不支持的profile: $PROFILE${NC}"; exit 1 ;;
esac

# 模块路径映射
get_module_path() {
    case "$1" in
        "mos") echo "open-adexchange-mos" ;;
        "dsp-api") echo "open-adexchange-api/open-adexchange-dsp-api" ;;
        "ssp-api") echo "open-adexchange-api/open-adexchange-ssp-api" ;;
        "tracking-api") echo "open-adexchange-api/open-adexchange-tracking-api" ;;
        "billing") echo "open-adexchange-billing" ;;
        "job") echo "open-adexchange-job" ;;
        *) echo "" ;;
    esac
}

# 构建单个模块（关键修复：添加-am参数）
build_module() {
    local module_name="$1"
    local module_path=$(get_module_path "$module_name")
    
    if [ -z "$module_path" ]; then
        echo -e "${RED}未知模块: $module_name${NC}"
        return 1
    fi
    
    echo -e "${BLUE}开始构建模块: $module_name ($module_path)${NC}"
    
    # 关键修复：当构建单个模块时添加-am参数
    local final_args="$BUILD_ARGS"
    if [ "$MODULE" != "all" ]; then
        final_args="$final_args -am"
    fi
    
    cd "$PROJECT_ROOT/$module_path" && "$MVN_CMD" $final_args
}

# 主逻辑
main() {
    echo -e "${YELLOW}========================================${NC}"
    echo -e "${YELLOW}Open-ADX 项目构建${NC}"
    echo -e "${YELLOW}========================================${NC}"
    echo -e "${BLUE}Profile: $PROFILE${NC}"
    echo -e "${BLUE}模块: $MODULE${NC}"
    echo -e "${BLUE}跳过测试: $SKIP_TESTS${NC}"
    echo -e "${BLUE}清理构建: $CLEAN${NC}"
    echo -e "${BLUE}跳过GPG签名: $SKIP_GPG${NC}"
    echo -e "${YELLOW}========================================${NC}"
    
    cd "$PROJECT_ROOT"
    
    # 构建指定模块
    local modules
    [[ "$MODULE" == "all" ]] && modules="mos dsp-api ssp-api tracking-api billing job" || modules="$MODULE"
    
    local failed=0
    for module in $modules; do
        if ! build_module "$module"; then
            echo -e "${RED}模块 $module 构建失败${NC}"
            ((failed++))
        else
            echo -e "${GREEN}模块 $module 构建成功${NC}"
        fi
        echo ""
    done
    
    [[ $failed -eq 0 ]] && echo -e "${GREEN}所有模块构建成功!${NC}" || { echo -e "${RED}有 $failed 个模块构建失败${NC}"; exit 1; }
}

main