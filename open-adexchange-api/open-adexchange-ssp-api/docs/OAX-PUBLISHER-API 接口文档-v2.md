# OAX Publisher API 接口文档 v2.0

> **文档版本**: v2.0  
> **生成时间**: 2026-03-04  
> **基于代码版本**: 最新实现  

## 目录

1. [概述](#概述)
2. [接口基本信息](#接口基本信息)
3. [请求参数详解](#请求参数详解)
4. [响应参数详解](#响应参数详解)
5. [数据字典](#数据字典)
6. [请求示例](#请求示例)
7. [响应示例](#响应示例)
8. [错误处理](#错误处理)
9. [业务逻辑说明](#业务逻辑说明)
10. [技术规范](#技术规范)

---

## 概述

OAX Publisher API（SSP 接口）是 Open Ad Exchange 平台面向媒体方（Supply-Side Platform, SSP）提供的广告获取接口。该接口允许媒体方通过 HTTP 请求向 Ad Exchange 平台请求广告内容，支持多种广告格式和设备类型。

### 核心功能

- **实时广告请求**: 支持媒体方实时请求广告内容
- **多广告位支持**: 单次请求可包含多个广告位（imp）
- **多设备类型**: 支持网站、移动 APP 等多种流量类型
- **调试模式**: 支持 debug 和 test 模式，便于联调和测试
- **扩展字段**: 支持自定义扩展字段，满足个性化需求

---

## 接口基本信息

| 项目 | 值 |
|------|-----|
| **接口地址** | `POST /v1/ads` |
| **协议** | HTTP/HTTPS |
| **请求方法** | POST |
| **Content-Type** | `application/json` |
| **认证方式** | 无需认证（公网接口） |
| **请求格式** | JSON |
| **响应格式** | JSON |
| **字符编码** | UTF-8 |

---

## 请求参数详解

### 请求头（Headers）

| 参数名 | 类型 | 必填 | 默认值 | 描述 |
|--------|------|------|--------|------|
| Content-Type | string | 是 | - | 固定值：`application/json` |

### 请求体（Request Body）

请求体为 JSON 格式，根对象包含以下字段：

#### 根对象字段

| 字段名 | 类型 | 必填 | 默认值 | 描述 |
|--------|------|------|--------|------|
| id | string | **是** | - | SSP 广告请求 ID，由 SSP 自动生成，需保证唯一性 |
| imp | array\<Imp\> | **是** | - | 曝光对象数组，一次请求可包含多个广告位 |
| site | Site | 否 | null | 站点对象，网站流量使用 |
| app | App | 否 | null | 移动应用对象，APP 流量使用 |
| device | Device | **是** | - | 设备对象 |
| debug | boolean | 否 | false | 是否调试模式，true 时会记录详细日志 |
| test | boolean | 否 | false | 是否测试流量，true 时标记为测试数据 |
| ext | object | 否 | {} | 扩展字段，键值对形式，key 和 value 均为 string |

**注意**: 
- `site` 和 `app` 至少填写一个，根据流量类型选择
- `debug` 或 `test` 为 true 时，系统会记录详细的请求和响应日志

#### 曝光对象 (Imp)

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | string | **是** | SSP 自动生成，IMP 唯一标识，用于关联响应 |
| tagid | string | **是** | 媒体广告位 ID，由 Ad-Exchange 定义和分配 |
| ext | object | 否 | 扩展字段，键值对形式 |

#### 站点对象 (Site) - 网站流量使用

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| content | Content | 否 | 网站内容对象，用于上下文广告投放 |

#### APP 对象 (App) - 移动应用流量使用

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| ver | string | 否 | APP 版本号 |
| name | string | 否 | APP 名称 |
| bundle | string | 否 | APP bundle ID（包名） |
| content | Content | 否 | APP 内容相关对象，用于上下文广告投放 |
| ext | object | 否 | 扩展字段 |

#### 内容对象 (Content)

用于网站或 APP 的内容描述，支持上下文广告投放。

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| title | string | 否 | 广告展示上下文相关内容标题 |
| keywords | string | 否 | 广告展示上下文相关内容关键字，多个关键字用逗号分隔 |

#### 设备对象 (Device)

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| ua | string | 否 | 设备 User-Agent 字符串 |
| geo | Geo | 否 | 地理位置对象 |
| ip | string | 否 | 设备 IPv4 地址 |
| ipv6 | string | 否 | 设备 IPv6 地址 |
| deviceType | integer | 否 | 设备类型，见 [数据字典](#设备类型-deviceType) |
| make | string | 否 | 设备制造商，如：Apple, Huawei |
| model | string | 否 | 设备型号，如：iPhone 14 Pro |
| os | string | 否 | 操作系统，如：iOS, Android |
| osv | string | 否 | 操作系统版本，如：14.0, 11.0 |
| carrier | string | 否 | 运营商，见 [数据字典](#运营商-carrier) |
| connectionType | integer | 否 | 网络连接类型，见 [数据字典](#网络连接类型-connectionType) |
| ifa | string | 否 | 明文设备码，如安卓的 IMEI 或 iOS 的 IDFA |
| didmd5 | string | 否 | MD5 加密的设备码 |
| mac | string | 否 | MAC 地址明文 |
| macmd5 | string | 否 | MD5 加密的 MAC 地址 |
| adid | string | 否 | 安卓 ID（Android ID） |
| oaid | string | 否 | OAID（匿名设备标识符，建议尽量传） |
| oaidmd5 | string | 否 | MD5 加密的 OAID |
| appInstalled | array\<string\> | 否 | 已安装的App列表 |
| h | integer | 否 | 设备屏幕高度（像素） |
| w | integer | 否 | 设备屏幕宽度（像素） |

#### 地理位置对象 (Geo)

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| lat | float | 否 | 纬度，范围：-90 到 90 |
| lon | float | 否 | 经度，范围：-180 到 180 |

---

## 响应参数详解

### HTTP 状态码

| 状态码 | 含义 | 说明 |
|--------|------|------|
| 200 | OK | 请求成功，返回广告数据 |
| 204 | No Content | 请求成功但无广告返回 |
| 400 | Bad Request | 请求参数错误 |
| 401 | Unauthorized | 未授权（预留） |
| 403 | Forbidden | 禁止访问（预留） |
| 500 | Internal Server Error | 服务器内部错误 |

### 成功响应（HTTP 200）

响应体为 JSON 格式，根对象包含以下字段：

#### 根对象字段

| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | string | 请求 ID，与 BidRequest 的 ID 保持一致，用于关联请求和响应 |
| ads | array\<Ad\> | 针对单个广告位的竞价响应数组，可能为空数组 |

#### 广告对象 (Ad)

| 字段名 | 类型 | 描述 |
|--------|------|------|
| impid | string | 曝光 ID，关联竞价请求中 Imp 的 ID，用于匹配广告位 |
| tagid | string | 广告位 ID，与请求中的 tagid 一致 |
| crid | string | DSP 平台创意 ID，用于标识广告创意 |
| pm | array\<string\> | 曝光监测 URL 列表，广告展示时触发 |
| cm | array\<string\> | 点击监测 URL 列表，用户点击时触发 |
| dm | array\<string\> | 下载开始监测 URL 列表，应用下载开始时触发 |
| dsm | array\<string\> | 下载成功监测 URL 列表，应用下载成功时触发 |
| vpm | array\<string\> | 视频播放监测 URL 列表，视频开始播放时触发 |
| vpcm | array\<string\> | 视频播放完成监测 URL 列表，视频播放完成时触发 |
| ldp | string | 广告落地页 URL，用户点击后跳转的页面 |
| curl | string | 广告创意地址，图片或视频等素材的 URL |
| ct | integer | 点击类型，见 [数据字典](#点击类型-ct) |
| bundle | string | 安卓应用包名或 iOS 的 AppID，点击类型为应用下载时必填 |
| adl | string | 应用下载地址，应用商店或下载页面的 URL |
| dlk | string | DeepLink 链接，用于唤起 APP |
| nativeAd | NativeAd | 原生广告响应对象，原生广告时使用 |
| price | long | 广告成交价（单位：分） |

#### 原生广告对象 (NativeAd)

| 字段名 | 类型 | 描述 |
|--------|------|------|
| title | string | 广告标题 |
| icon | string | 图标 URL |
| desc | string | 广告描述（主描述） |
| mainImage | string | 主图 URL |
| images | array\<string\> | 图片 URL 列表，多图广告使用 |
| video | string | 视频 URL |
| ctaText | string | 行动号召按钮文本，如"立即购买"、"下载" |
| rating | string | 评分，如"4.8" |
| likes | string | 点赞数 |
| downloads | string | 下载数 |
| sponsored | string | 赞助方信息 |
| price | string | 价格 |
| salePrice | string | 销售价格 |
| phone | string | 联系电话 |
| address | string | 地址 |
| desc2 | string | 广告副描述（补充描述） |
| displayUrl | string | 展示 URL（广告主网站） |
| ext | object | 扩展字段，键值对形式 |

---

## 数据字典

### 设备类型 (deviceType)

| 值 | 含义 |
|-----|------|
| 1 | phone（手机） |
| 2 | pad（平板） |
| 3 | pc（个人电脑） |
| 4 | tv（智能电视） |

### 运营商 (carrier)

| 值 | 含义 |
|-----|------|
| 0 | 未知 |
| 1 | 中国移动 |
| 2 | 中国联通 |
| 3 | 中国电信 |

### 网络连接类型 (connectionType)

| 值 | 含义 |
|-----|------|
| 0 | 未知 |
| 1 | WiFi |
| 2 | 2G |
| 3 | 3G |
| 4 | 4G |
| 5 | 5G |

### 点击类型 (ct)

| 值 | 含义 |
|-----|------|
| 1 | 浏览器打开（跳转落地页） |
| 2 | 安卓应用下载 |
| 3 | DeepLink（唤起 APP） |
| 4 | iOS 应用下载 |

---

## 请求示例

### 示例 1：网站广告请求

```json
{
  "id": "req-1709529600001",
  "imp": [
    {
      "id": "imp-001",
      "tagid": "adx-tag-home-banner-001",
      "ext": {
        "adSize": "728x90"
      }
    },
    {
      "id": "imp-002",
      "tagid": "adx-tag-sidebar-002",
      "ext": {
        "adSize": "300x250"
      }
    }
  ],
  "site": {
    "content": {
      "title": "科技新闻首页",
      "keywords": "科技，互联网，人工智能，数码"
    }
  },
  "device": {
    "ua": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
    "ip": "203.0.113.45",
    "deviceType": 3,
    "make": "Dell",
    "model": "XPS 15",
    "os": "Windows",
    "osv": "11",
    "connectionType": 1,
    "w": 1920,
    "h": 1080
  },
  "test": false,
  "debug": false,
  "ext": {
    "pageUrl": "https://news.example.com/tech",
    "userId": "user-12345"
  }
}
```

### 示例 2：移动 APP 广告请求

```json
{
  "id": "req-1709529600002",
  "imp": [
    {
      "id": "imp-001",
      "tagid": "adx-tag-app-feed-001",
      "ext": {}
    }
  ],
  "app": {
    "ver": "3.2.1",
    "name": "健身助手",
    "bundle": "com.fitness.app",
    "content": {
      "title": "运动健康",
      "keywords": "健身，运动，减肥，健康"
    }
  },
  "device": {
    "ua": "Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X)",
    "ip": "198.51.100.78",
    "deviceType": 1,
    "make": "Apple",
    "model": "iPhone 13",
    "os": "iOS",
    "osv": "15.0",
    "carrier": "1",
    "connectionType": 5,
    "ifa": "A1B2C3D4-E5F6-7890-ABCD-EF1234567890",
    "geo": {
      "lat": 39.9042,
      "lon": 116.4074
    },
    "w": 1170,
    "h": 2532
  },
  "debug": true,
  "test": false
}
```

### 示例 3：调试模式请求

```json
{
  "id": "req-debug-001",
  "imp": [
    {
      "id": "imp-001",
      "tagid": "adx-tag-test-001"
    }
  ],
  "app": {
    "ver": "1.0.0",
    "name": "测试应用",
    "bundle": "com.test.app"
  },
  "device": {
    "ua": "TestAgent/1.0",
    "deviceType": 1,
    "os": "Android",
    "osv": "11.0"
  },
  "debug": true,
  "test": true
}
```

---

## 响应示例

### 示例 1：展示广告响应

```json
{
  "id": "req-1709529600001",
  "ads": [
    {
      "impid": "imp-001",
      "tagid": "adx-tag-home-banner-001",
      "crid": "dsp-creative-banner-001",
      "pm": [
        "https://tracker.dsp1.com/impression?ad=001",
        "https://tracker.adx.com/view/001"
      ],
      "cm": [
        "https://tracker.dsp1.com/click?ad=001"
      ],
      "ldp": "https://landingpage.example.com/promo",
      "curl": "https://creative.cdn.com/banner/728x90.jpg",
      "ct": 1,
      "price": 150
    },
    {
      "impid": "imp-002",
      "tagid": "adx-tag-sidebar-002",
      "crid": "dsp-creative-square-002",
      "pm": [
        "https://tracker.dsp2.com/impression?ad=002"
      ],
      "cm": [
        "https://tracker.dsp2.com/click?ad=002"
      ],
      "ldp": "https://shop.example.com/product",
      "curl": "https://creative.cdn.com/square/300x250.jpg",
      "ct": 1,
      "price": 80
    }
  ]
}
```

### 示例 2：应用下载广告响应

```json
{
  "id": "req-1709529600002",
  "ads": [
    {
      "impid": "imp-001",
      "tagid": "adx-tag-app-feed-001",
      "crid": "dsp-creative-app-001",
      "pm": [
        "https://tracker.dsp1.com/impression?app=001"
      ],
      "cm": [
        "https://tracker.dsp1.com/click?app=001"
      ],
      "dm": [
        "https://tracker.dsp1.com/download/start?app=001"
      ],
      "dsm": [
        "https://tracker.dsp1.com/download/complete?app=001"
      ],
      "ldp": "https://play.google.com/store/apps/details?id=com.game.rpg",
      "curl": "https://creative.cdn.com/app/icon.png",
      "ct": 2,
      "bundle": "com.game.rpg",
      "adl": "https://play.google.com/store/apps/details?id=com.game.rpg",
      "price": 300
    }
  ]
}
```

### 示例 3：原生广告响应

```json
{
  "id": "req-native-001",
  "ads": [
    {
      "impid": "imp-001",
      "tagid": "adx-tag-native-feed-001",
      "crid": "dsp-creative-native-001",
      "pm": [
        "https://tracker.dsp1.com/impression?native=001"
      ],
      "cm": [
        "https://tracker.dsp1.com/click?native=001"
      ],
      "ldp": "https://product.example.com/smartwatch",
      "ct": 1,
      "price": 200,
      "nativeAd": {
        "title": "智能手表限时促销",
        "icon": "https://creative.cdn.com/watch/icon.png",
        "desc": "最新款智能手表，健康监测，运动助手，长续航",
        "mainImage": "https://creative.cdn.com/watch/main.jpg",
        "images": [
          "https://creative.cdn.com/watch/img1.jpg",
          "https://creative.cdn.com/watch/img2.jpg",
          "https://creative.cdn.com/watch/img3.jpg"
        ],
        "video": "https://creative.cdn.com/watch/promo.mp4",
        "ctaText": "立即购买",
        "rating": "4.8",
        "downloads": "10 万+",
        "sponsored": "品牌旗舰店",
        "price": "¥1999",
        "salePrice": "¥1599",
        "displayUrl": "https://watch.example.com",
        "ext": {
          "color": "黑色",
          "warranty": "2 年质保"
        }
      }
    }
  ]
}
```

### 示例 4：无广告返回

```json
{
  "id": "req-no-ad-001",
  "ads": []
}
```

---

## 错误处理

### HTTP 错误响应

当请求失败时，HTTP 状态码将指示错误类型，响应体可能包含错误详情。

### 常见错误场景

#### 1. 请求参数无效（400 Bad Request）

**错误原因**:
- 请求 ID 为空或格式错误
- 缺少曝光对象（imp 数组为空）
- 广告位 ID（tagid）无效
- 设备信息缺失

**解决方案**:
```json
{
  "error": {
    "code": "INVALID_REQUEST",
    "message": "广告请求对象不能为空",
    "field": "request"
  }
}
```

#### 2. 缺少必要参数

**错误示例**:
```json
{
  "error": {
    "code": "MISSING_IMP",
    "message": "至少需要一个曝光对象",
    "field": "imp"
  }
}
```

#### 3. 广告位 ID 无效

```json
{
  "error": {
    "code": "INVALID_TAG_ID",
    "message": "广告位 ID 无效或未分配",
    "field": "imp[0].tagid"
  }
}
```

#### 4. 请求频率超限（预留）

```json
{
  "error": {
    "code": "RATE_LIMIT_EXCEEDED",
    "message": "请求频率超过限制",
    "retryAfter": 60
  }
}
```

### 错误码汇总表

| 错误码 | HTTP 状态码 | 描述 | 解决方案 |
|--------|-----------|------|----------|
| INVALID_REQUEST | 400 | 请求参数无效 | 检查请求参数格式和必填项 |
| MISSING_IMP | 400 | 缺少曝光对象 | 至少提供一个有效的曝光对象 |
| INVALID_TAG_ID | 400 | 广告位 ID 无效 | 检查 tagid 是否正确分配 |
| RATE_LIMIT_EXCEEDED | 429 | 请求频率超限 | 降低请求频率，稍后重试 |
| INTERNAL_ERROR | 500 | 服务器内部错误 | 联系技术支持，提供请求 ID |

---

## 业务逻辑说明

### 请求处理流程

1. **参数验证**: 系统首先验证请求参数的完整性和有效性
   - 检查请求 ID 是否存在且唯一
   - 验证至少包含一个曝光对象
   - 校验广告位 ID 的有效性

2. **竞价请求构建**: 将 SSP 请求转换为标准的 BidRequest 格式
   - 映射设备信息
   - 构建曝光对象
   - 添加扩展字段

3. **广告竞价**: 调用 Ad Exchange 引擎进行广告竞价
   - 匹配 DSP 资源
   - 执行竞价逻辑
   - 筛选最优广告

4. **响应构建**: 将竞价结果转换为 SSP 响应格式
   - 映射广告信息
   - 添加监测链接
   - 处理原生广告数据

5. **日志记录**: 根据 debug/test 标志决定是否记录详细日志

### 调试模式说明

当 `debug=true` 或 `test=true` 时：

- 系统会记录完整的请求和响应日志
- 日志格式：
  ```
  AdGetRequest: {request 对象 JSON}
  adGetResponse: {response 对象 JSON}
  ```
- 便于问题排查和联调测试
- **生产环境建议关闭 debug 模式**

### 测试流量说明

当 `test=true` 时：

- 标记该请求为测试流量
- 可能影响计费和数据统计
- 建议在测试环境使用
- 生产环境的测试流量应明确标识

---

## 技术规范

### 性能建议

1. **请求频率**: 
   - 建议 QPS 不超过 100 次/秒
   - 如需更高并发，请联系技术支持

2. **超时设置**:
   - 建议客户端超时设置为 500ms
   - 超时时应快速失败，避免阻塞

3. **重试策略**:
   - 仅在 5xx 错误时重试
   - 建议使用指数退避策略
   - 最多重试 2 次

### 缓存策略

1. **广告缓存**:
   - 不建议缓存广告内容
   - 每次广告展示都应发起新请求
   - 确保广告的时效性和准确性

2. **配置缓存**:
   - 广告位配置可适当缓存
   - 缓存时间建议不超过 5 分钟

### 安全建议

1. **防止滥用**:
   - 此接口面向公网，需防范恶意请求
   - 建议实施 IP 白名单（如适用）
   - 监控异常流量

2. **数据保护**:
   - 不要传输敏感个人信息
   - 设备码建议加密传输（使用 md5 字段）
   - 遵守隐私保护法规

### 最佳实践

1. **请求 ID 生成**:
   - 使用 UUID 或时间戳 + 随机数
   - 确保全局唯一性
   - 便于问题追踪和日志分析

2. **错误处理**:
   - 捕获所有网络异常
   - 实现降级策略（如无广告时的默认处理）
   - 记录错误日志，包含请求 ID

3. **监控告警**:
   - 监控请求成功率和响应时间
   - 设置异常告警阈值
   - 定期分析广告填充率

4. **版本兼容**:
   - 关注 API 版本更新
   - 向后兼容旧版本
   - 重大变更会提前通知

---

## 附录

### 版本历史

| 版本     | 日期         | 变更说明                       |
|--------|------------|----------------------------|
| v1.0   | -          | 初始版本                       |
| v2.0   | 2026-03-04 | 基于最新代码实现，完善字段说明和示例         |
| v2.0.1 | 2026-03-04 | device对象中增加 appInstalled字段 |
---

**文档结束**
