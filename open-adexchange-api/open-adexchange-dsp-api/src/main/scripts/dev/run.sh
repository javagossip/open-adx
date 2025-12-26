#!/bin/bash

# 定义进程名称和JAR包路径
PROCESS_NAME="oax-openapi-dsp"
JAR_PATH="oax-openapi-dsp.jar"

echo "开始检查并管理 ${PROCESS_NAME} 进程..."

# 1. 判断名称为helxpay-boot的进程是否存在，如果存在则kill掉（非暴力kill）
PID=$(ps -ef | grep ${PROCESS_NAME} | grep -v grep | awk '{print $2}')

if [ -n "${PID}" ]; then
    echo "${PROCESS_NAME} 进程 (PID: ${PID}) 正在运行，尝试停止..."
    kill ${PID} # 非暴力kill
    sleep 5 # 等待进程优雅关闭，可以根据实际情况调整等待时间

    # 再次检查进程是否已经停止
    if ps -p ${PID} > /dev/null; then
        echo "进程 ${PROCESS_NAME} (PID: ${PID}) 未能在指定时间内停止，尝试强制杀死..."
        kill -9 ${PID} # 强制杀死
        sleep 2
    fi

    if ! ps -p ${PID} > /dev/null; then
        echo "${PROCESS_NAME} 进程已成功停止。"
    else
        echo "警告：${PROCESS_NAME} 进程未能停止，请手动检查。"
        exit 1 # 进程未停止，退出脚本
    fi
else
    echo "未发现 ${PROCESS_NAME} 进程正在运行。"
fi

# 2. 进程停止之后，执行启动命令
echo "正在启动 ${PROCESS_NAME} 进程..."
nohup java -Xms1g -Xmx1g -XX:+UseZGC -jar ${JAR_PATH} > /data/logs/oax-openapi-dsp_stdout.log 2>&1 &

if [ $? -eq 0 ]; then
    echo "${PROCESS_NAME} 进程已成功启动。"
else
    echo "错误：${PROCESS_NAME} 进程启动失败。"
fi

exit 0