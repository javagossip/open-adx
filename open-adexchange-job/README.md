# open-adexchange-job

任务调度服务，基于 XXL-Job。

## GraalVM Native 镜像

本模块支持使用 GraalVM Native Image 构建原生可执行文件及 Docker 镜像（更小体积、更快启动）。
采用 **Dockerfile 多阶段构建**，在容器内完成编译，无需本机安装 GraalVM。

### 分环境说明

构建参数 `ENV_ID` 控制**构建时**资源（log4j2.xml 等），可选值：`dev` / `test` / `prod` / `docker`（默认 `docker`）。

运行时通过环境变量 `SPRING_PROFILES_ACTIVE` 选择 `application-*.properties`，与 JVM 镜像用法一致。

### 构建原生 Docker 镜像

在**项目根目录**执行：

```bash
# 默认构建（docker 环境）
docker build -f open-adexchange-job/Dockerfile.native \
  -t registry.cn-beijing.aliyuncs.com/oax/oax-job:native .

# 指定环境构建（如生产环境）
docker build -f open-adexchange-job/Dockerfile.native \
  --build-arg ENV_ID=prod \
  -t registry.cn-beijing.aliyuncs.com/oax/oax-job:native-prod .

# 开发环境
docker build -f open-adexchange-job/Dockerfile.native \
  --build-arg ENV_ID=dev \
  -t registry.cn-beijing.aliyuncs.com/oax/oax-job:native-dev .
```

### 推送镜像到仓库

```bash
docker login registry.cn-beijing.aliyuncs.com -u <ACR_USERNAME> -p <ACR_PASSWORD>
docker push registry.cn-beijing.aliyuncs.com/oax/oax-job:native-prod
```

### 仅构建本地原生可执行文件（不构建镜像）

需本机安装 GraalVM JDK 21+（含 `native-image`）：

```bash
# 生产环境
./mvnw -Pnative -Denv.id=prod -pl open-adexchange-job -am package -DskipTests

# 开发环境
./mvnw -Pnative -Denv.id=dev -pl open-adexchange-job -am package -DskipTests
```

可执行文件生成在：`open-adexchange-job/target/oax-job`。

### 使用原生镜像运行

```bash
docker run --rm -p 8082:8082 \
  -e SPRING_PROFILES_ACTIVE=docker \
  registry.cn-beijing.aliyuncs.com/oax/oax-job:native
```

环境变量、端口、健康检查等与 `deployment/docker-compose.yml` 中 `openadx-job` 配置保持一致即可。
