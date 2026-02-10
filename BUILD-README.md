# Open-ADX 项目构建脚本说明

## 概述

本项目提供了跨平台的构建脚本，支持Linux/Mac和Windows系统，可以方便地构建Open-ADX项目的各个模块，并支持不同的构建profile。

## 脚本文件

- `build.sh` - Linux/Mac系统的构建脚本
- `build.bat` - Windows系统的构建脚本

## 支持的模块

| 模块名 | 描述 | 端口 |
|--------|------|------|
| mos | MOS管理后台模块 | 9090 |
| dsp-api | DSP接口模块 | 9091 |
| ssp-api | SSP接口模块 | 8080 |
| tracking-api | 数据追踪模块 | 8083 |
| billing | 计费模块 | 8080 |
| job | 定时任务模块 | 8082 |

## 支持的Profile

| Profile | 描述 |
|---------|------|
| default | 默认构建，生成jar包 |
| docker | Docker镜像构建 |

## 使用方法

### Linux/Mac系统

```bash
# 给脚本添加执行权限
chmod +x build.sh

# 查看帮助信息
./build.sh --help

# 构建所有模块(默认)
./build.sh

# 构建特定模块
./build.sh mos
./build.sh dsp-api

# 使用docker profile构建
./build.sh -p docker

# 跳过测试构建
./build.sh -s

# 清理后构建
./build.sh -c

# 跳过GPG签名
./build.sh --skip-gpg

# 组合使用多种选项
./build.sh -c -s -p docker mos
```

### Windows系统

```cmd
# 查看帮助信息
build.bat --help

# 构建所有模块(默认)
build.bat

# 构建特定模块
build.bat mos
build.bat dsp-api

# 使用docker profile构建
build.bat -p docker

# 跳过测试构建
build.bat -s

# 清理后构建
build.bat -c

# 跳过GPG签名
build.bat --skip-gpg

# 组合使用多种选项
build.bat -c -s -p docker mos
```

## 参数说明

### 通用参数

- `-h, --help`: 显示帮助信息
- `-p, --profile PROFILE`: 指定构建profile (default|docker)
- `-s, --skip-tests`: 跳过测试执行
- `-c, --clean`: 执行clean操作后再构建
- `--skip-gpg`: 跳过GPG签名验证

### 模块参数

可以直接指定要构建的模块名称：
- `mos`: MOS管理后台模块
- `dsp-api`: DSP接口模块
- `ssp-api`: SSP接口模块
- `tracking-api`: 数据追踪模块
- `billing`: 计费模块
- `job`: 定时任务模块
- `all`: 构建所有模块(默认)

## 构建流程

### 默认构建(default profile)
1. 编译源码
2. 运行单元测试(除非使用-s参数)
3. 打包生成jar文件
4. 安装到本地Maven仓库

### Docker构建(docker profile)
1. 执行默认构建的所有步骤
2. 使用Jib插件构建Docker镜像
3. 推送镜像到指定的镜像仓库

## 注意事项

1. **GPG签名**: 本地构建时建议使用`--skip-gpg`参数跳过GPG签名
2. **依赖顺序**: 构建所有模块时会自动处理模块间的依赖关系
3. **环境要求**: 需要安装Java 8+和Maven 3.6+
4. **Docker构建**: 需要配置好Docker环境和镜像仓库认证

## 常见问题

### Q: 构建时报GPG签名错误怎么办？
A: 使用`--skip-gpg`参数跳过GPG签名，或者配置好GPG密钥。

### Q: 如何只构建某个特定模块？
A: 直接在命令后面加上模块名，如：`./build.sh mos`

### Q: Docker镜像推送到哪里？
A: 根据各模块pom.xml中的jib配置，推送到对应的阿里云镜像仓库。

### Q: 构建失败如何排查？
A: 可以使用`-c`参数清理后重新构建，或查看具体的错误日志。

## 示例命令

```bash
# 快速构建所有模块(跳过测试和GPG签名)
./build.sh -s --skip-gpg

# 清理并构建Docker镜像
./build.sh -c -p docker

# 只构建核心业务模块
./build.sh mos dsp-api ssp-api

# 生产环境构建(包含测试)
./build.sh -p docker
```