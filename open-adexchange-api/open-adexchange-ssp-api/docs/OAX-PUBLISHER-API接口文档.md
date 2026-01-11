# OAX Publisher API 接口文档

## 概述

OAX Publisher API 是面向媒体方（Supply-Side Platform, SSP）的广告获取接口，允许媒体方通过该接口向 Open Ad Exchange 平台请求广告内容。本文档详细描述了接口的使用方法、请求参数和响应格式。

## 接口基本信息

- **接口地址**: `POST /v1/ads`
- **功能描述**: 媒体方向 Ad Exchange 平台请求广告内容
- **认证方式**: 无需认证
- **请求格式**: JSON
- **响应格式**: JSON

## 请求参数

### 请求头（Headers）
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| Content-Type | string | 是 | 固定值: application/json |

### 请求体（Request Body）

#### 主要字段
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | string | 是 | SSP广告请求ID，由SSP自动生成 |
| imp | array | 是 | 曝光对象数组，一次请求可包含多个imp |
| site | object | 否 | 站点对象，网站流量使用 |
| app | object | 否 | 移动应用对象，移动APP流量使用 |
| device | object | 是 | 设备对象 |
| debug | boolean | 否 | 是否调试模式 |
| test | boolean | 否 | 是否测试流量 |
| ext | object | 否 | 扩展字段，键值对形式 |

#### 曝光对象 (Imp)
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | string | 是 | SSP自动生成，IMP唯一标识 |
| tagid | string | 是 | 媒体广告位ID，由Ad-Exchange定义和分配 |
| ext | object | 否 | 扩展字段 |

#### 站点对象 (Site)
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| content | object | 否 | 网站内容对象 |

#### APP对象 (App)
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| ver | string | 否 | APP版本 |
| content | object | 否 | APP内容相关对象 |

#### 内容对象 (Content)
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| title | string | 否 | 广告展示上下文相关内容标题 |
| keywords | string | 否 | 广告展示上下文相关内容关键字 |

#### 设备对象 (Device)
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| ua | string | 否 | 设备User-Agent |
| geo | object | 否 | 地理位置对象 |
| ip | string | 否 | 设备IP地址 |
| ipv6 | string | 否 | IPv6地址 |
| deviceType | integer | 否 | 设备类型: 1-phone, 2-pad, 3-pc, 4-tv |
| make | string | 否 | 设备制造商 |
| model | string | 否 | 设备型号，如:iPhone |
| os | string | 否 | 操作系统，如:ios/Android |
| osv | string | 否 | 操作系统版本 |
| carrier | string | 否 | 运营商: 0-未知, 1-移动, 2-联通, 3-电信 |
| connectionType | integer | 否 | 网络连接类型: 1-WiFi, 2-2G, 3-3G, 4-4G, 5-5G, 0-未知 |
| ifa | string | 否 | 明文设备码，如安卓的IMEI或iOS的IDFA |
| didmd5 | string | 否 | MD5设备码 |
| mac | string | 否 | MAC地址明文 |
| macmd5 | string | 否 | MD5 MAC地址 |
| adid | string | 否 | 安卓ID |
| h | integer | 否 | 设备屏幕高度 |
| w | integer | 否 | 设备屏幕宽度 |

#### 地理位置对象 (Geo)
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| lat | float | 否 | 纬度 |
| lon | float | 否 | 经度 |

## 响应参数

### 成功响应 (HTTP Status: 200)

#### 主要字段
| 参数名 | 类型 | 描述 |
|--------|------|------|
| id | string | 请求ID，与BidRequest的ID保持一致 |
| ads | array | 针对单个广告位的竞价响应数组 |

#### 广告对象 (Ad)
| 参数名 | 类型 | 描述 |
|--------|------|------|
| impid | string | 曝光ID，关联竞价请求中Imp的ID |
| tagid | string | 广告位ID |
| crid | string | DSP平台创意ID |
| pm | array | 曝光监测URL列表 |
| cm | array | 点击监测URL列表 |
| ldp | string | 广告落地页URL |
| curl | string | 广告创意地址 |
| ct | integer | 点击类型: 1-浏览器打开, 2-安卓应用下载, 3-deeplink, 4-iOS应用 |
| bundle | string | 安卓应用包名或iOS的AppID，点击类型为安卓应用下载和iOS应用时，bundle必填 |
| adl | string | 应用下载地址 |
| dlk | string | DeepLink链接 |
| nativeAd | object | 原生广告响应对象 |

#### 原生广告对象 (NativeAd)
| 参数名 | 类型 | 描述 |
|--------|------|------|
| templateId | string | 原生广告模板ID，一个原生广告位可能支持多个广告模板 |
| assets | object | 原生广告属性，键值对形式 |

##### Assets 对象可能包含的字段
| 参数名 | 类型 | 描述 |
|--------|------|------|
| title | string | 广告标题 |
| mainImage | string | 主图URL |
| icon | string | 图标URL |
| images | array | 图片URL列表 |
| videos | array | 视频URL列表 |
| cta | string | 行动号召按钮文本 |
| desc | string | 广告描述 |
| desc2 | string | 广告副描述 |
| sponsored | string | 赞助方信息 |
| rating | string | 评分 |
| likes | string | 点赞数 |
| price | string | 价格 |
| salePrice | string | 销售价格 |
| downloads | string | 下载数 |
| phone | string | 电话 |
| address | string | 地址 |
| displayUrl | string | 展示URL |

### 响应状态码

#### HTTP状态码
- `200`: 请求成功
- `400`: 请求参数错误
- `401`: 未授权
- `403`: 禁止访问
- `500`: 服务器内部错误

## 请求示例

### 完整请求示例
```json
{
  "id": "req-1234567890",
  "imp": [
    {
      "id": "imp-001",
      "tagid": "adx-tag-001",
      "ext": {}
    }
  ],
  "site": {
    "content": {
      "title": "新闻首页",
      "keywords": "新闻,资讯,热点"
    }
  },
  "device": {
    "ua": "Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X)",
    "ip": "192.168.1.1",
    "deviceType": 1,
    "make": "Apple",
    "model": "iPhone",
    "os": "iOS",
    "osv": "14.0",
    "geo": {
      "lat": 39.9042,
      "lon": 116.4074
    },
    "h": 800,
    "w": 600
  },
  "test": false,
  "debug": false,
  "ext": {
    "custom_field": "custom_value"
  }
}
```

### 响应示例
```json
{
  "id": "req-1234567890",
  "ads": [
    {
      "impid": "imp-001",
      "tagid": "adx-tag-001",
      "crid": "dsp-creative-001",
      "pm": [
        "https://tracker.example.com/impression"
      ],
      "cm": [
        "https://tracker.example.com/click"
      ],
      "ldp": "https://landingpage.example.com",
      "curl": "https://creative.example.com/banner.jpg",
      "ct": 1,
      "nativeAd": {
        "templateId": "template-native-001",
        "assets": {
          "title": "智能手表促销",
          "desc": "最新款智能手表，健康监测，运动助手",
          "main_image": "https://creative.example.com/watch.jpg",
          "icon": "https://creative.example.com/watch_icon.jpg",
            "cta": "立即购买",
            "price": "¥1999",
            "rating": "4.8"
        }
      }
    }
  ]
}
```

## 注意事项

1. **请求频率限制**: 为保证服务质量，建议每秒请求数不超过100次
2. **ID生成规则**: 请确保请求ID的唯一性，避免重复请求
3. **缓存策略**: 建议对广告内容进行合理缓存，减少重复请求
4. **安全考虑**: 此接口面向公网，注意防止恶意请求和滥用
5. **错误处理**: 当收到错误响应时，请根据错误码进行相应处理
6. **测试环境**: 请在测试环境中充分验证后再上线生产

## 常见错误码

| 错误码 | 描述 | 解决方案 |
|--------|------|----------|
| INVALID_REQUEST | 请求参数无效 | 检查请求参数格式和必填项 |
| MISSING_IMP | 缺少曝光对象 | 至少需要一个有效的曝光对象 |
| INVALID_TAG_ID | 广告位ID无效 | 检查tagid是否正确分配 |
| RATE_LIMIT_EXCEEDED | 请求频率超限 | 降低请求频率 |

## 技术支持

如有疑问或需要技术支持，请联系:
- 邮箱: support@openadx.com
- 工单系统: https://support.openadx.com
- API文档: https://docs.openadx.com