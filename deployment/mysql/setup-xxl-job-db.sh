#!/bin/bash
set -e

echo "Creating xxl_job database and granting permissions..."

# 使用环境变量或默认值
MYSQL_USER=${MYSQL_USER:-openadx_user}
MYSQL_PASSWORD=${MYSQL_PASSWORD:-openadx_password}
MYSQL_DATABASE=${MYSQL_DATABASE:-open-adexchange}

# 创建数据库并授权
mysql -u root -p"$MYSQL_ROOT_PASSWORD" <<-EOSQL
    CREATE DATABASE IF NOT EXISTS xxl_job CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    
    -- 创建用户（如果不存在）并设置密码
    CREATE USER IF NOT EXISTS '$MYSQL_USER'@'%' IDENTIFIED BY '$MYSQL_PASSWORD';
    CREATE USER IF NOT EXISTS '$MYSQL_USER'@'localhost' IDENTIFIED BY '$MYSQL_PASSWORD';
    
    -- 授予对xxl_job数据库的权限
    GRANT ALL PRIVILEGES ON xxl_job.* TO '$MYSQL_USER'@'%';
    GRANT ALL PRIVILEGES ON xxl_job.* TO '$MYSQL_USER'@'localhost';
    
    -- 刷新权限
    FLUSH PRIVILEGES;
EOSQL

echo "Database and permissions setup completed."