# DSP平台API对接文档

## 1. 概述

本文档描述了DSP平台如何与广告交易平台进行对接，包括广告主管理和创意管理两部分API接口。

## 2. 接口规范

### 2.1 基础规范

- 请求协议：HTTP/HTTPS
- 数据格式：JSON
- 字符编码：UTF-8
- 请求方法：POST/GET/PUT等
- 认证方式：JWT Token（在请求Header中携带）

### 2.2 响应格式

所有接口均采用统一的响应格式：

```json
{
  "success": true,
  "code": "00000",
  "message": "成功",
  "data": {}
}
```

字段说明：
- success: boolean类型，表示请求是否成功
- code: string类型，业务状态码
- message: string类型，响应消息
- data: object类型，响应数据

## 3. 广告主管理接口

### 3.1 添加广告主

#### 接口地址
`POST /v1/advertisers`

#### 接口描述
添加广告主信息，广告主添加成功后返回广告交易平台生成的广告主编码。

#### 请求参数
| 参数名 | 类型 | 必填 | 描述 |
| --- | --- | --- | --- |
| advertiserName | string | 是 | 广告主名称 |
| advertiserId | string | 否 | DSP平台广告主ID |
| companyName | string | 是 | 公司全称 |
| industryCode | string | 是 | 行业代码 |
| businessLicenseNo | string | 是 | 统一社会信用代码 |
| legalPersonName | string | 是 | 法人姓名 |
| registeredAddress | string | 是 | 注册地址 |
| contactName | string | 否 | 联系人名称 |
| contactPhone | string | 否 | 联系人电话 |
| contactEmail | string | 否 | 联系人邮箱 |
| businessLicenseUrl | string | 是 | 营业执照url |
| legalPersonIdUrl | string | 否 | 法人身份证url |
| advertiserIndustryLicenses | array | 否 | 广告主行业资质列表 |

其中advertiserIndustryLicenses数组元素包含以下字段：
| 参数名 | 类型 | 必填 | 描述 |
| --- | --- | --- | --- |
| industryCode | string | 否 | 行业代码 |
| licenseName | string | 否 | 资质名称 |
| licenseUrl | string | 是 | 资质地址 |

#### 请求示例
```json
{
  "advertiserName": "测试广告主",
  "companyName": "测试科技有限公司",
  "industryCode": "I101",
  "businessLicenseNo": "91110108MA01XXXXXX",
  "legalPersonName": "张三",
  "registeredAddress": "北京市海淀区中关村大街1号",
  "contactName": "李四",
  "contactPhone": "13800138000",
  "contactEmail": "zhangsan@test.com",
  "businessLicenseUrl": "https://test.com/license.jpg",
  "legalPersonIdUrl": "https://test.com/idcard.jpg",
  "advertiserIndustryLicenses": [
    {
      "industryCode": "I101",
      "licenseName": "互联网广告发布资质",
      "licenseUrl": "https://test.com/license1.jpg"
    }
  ]
}
```

#### 响应参数
返回广告交易平台生成的广告主编码。

#### 响应示例
```json
{
  "success": true,
  "code": "00000",
  "message": "成功",
  "data": "ADVT20210101000001"
}
```

### 3.2 更新广告主

#### 接口地址
`PUT /v1/advertisers`

#### 接口描述
更新广告主信息。

#### 请求参数
同添加广告主接口，但advertiserId为必填项。

#### 响应参数
boolean类型，更新成功返回true，否则返回false。

#### 响应示例
```json
{
  "success": true,
  "code": "00000",
  "message": "成功",
  "data": true
}
```

### 3.3 获取广告主审核状态

#### 接口地址
`GET /v1/advertisers/{advertiserId}/audit-status`

#### 接口描述
获取指定广告主的审核状态。

#### 请求参数
| 参数名 | 类型 | 必填 | 描述 |
| --- | --- | --- | --- |
| advertiserId | string | 是 | DSP平台广告主ID |

#### 响应参数
| 参数名 | 类型 | 描述 |
| --- | --- | --- |
| advertiserId | string | DSP平台广告主ID |
| auditResult | object | 审核结果对象 |

auditResult对象包含以下字段：
| 参数名 | 类型 | 描述 |
| --- | --- | --- |
| auditStatus | string | 审核状态，可能值：PENDING(待审核)、APPROVED(审核通过)、REJECTED(审核拒绝) |
| auditReason | string | 审核结果，审核不通过的原因 |
| auditTime | long | 审核时间戳 |

#### 响应示例
```json
{
  "success": true,
  "code": "00000",
  "message": "成功",
  "data": {
    "advertiserId": "DSP001",
    "auditResult": {
      "auditStatus": "APPROVED",
      "auditReason": "",
      "auditTime": 1609459200000
    }
  }
}
```

### 3.4 批量获取广告主审核状态

#### 接口地址
`GET /v1/advertisers/audit-status`

#### 接口描述
批量获取多个广告主的审核状态。

#### 请求参数
| 参数名 | 类型 | 必填 | 描述 |
| --- | --- | --- | --- |
| advertiserIds | array | 是 | DSP平台广告主ID列表 |

#### 响应参数
返回广告主审核结果对象数组，每个对象结构同单个查询接口。

#### 响应示例
```json
{
  "success": true,
  "code": "00000",
  "message": "成功",
  "data": [
    {
      "advertiserId": "DSP001",
      "auditResult": {
        "auditStatus": "APPROVED",
        "auditReason": "",
        "auditTime": 1609459200000
      }
    },
    {
      "advertiserId": "DSP002",
      "auditResult": {
        "auditStatus": "PENDING",
        "auditReason": "",
        "auditTime": 1609459200000
      }
    }
  ]
}
```

## 4. 创意管理接口

### 4.1 创建广告创意素材

#### 接口地址
`POST /v1/creatives`

#### 接口描述
创建广告创意素材。

#### 请求参数
| 参数名 | 类型 | 必填 | 描述 |
| --- | --- | --- | --- |
| creativeId | string | 否 | DSP平台创意ID |
| advertiserId | string | 是 | DSP平台广告主ID |
| name | string | 是 | 广告创意名称 |
| creativeType | string | 是 | 创意类型，可能值：BANNER、VIDEO、AUDIO、NATIVE |
| creativeUrl | string | 是 | 创意地址 |
| landingPage | string | 是 | 广告落地页地址 |
| nativeAd | object | 否 | 原生/信息流广告数据 |

nativeAd对象包含以下字段：
| 参数名 | 类型 | 必填 | 描述 |
| --- | --- | --- | --- |
| templateCode | string | 是 | native广告模板ID，由广告交易平台提供 |
| assets | map | 是 | native广告属性，具体属性需要根据模板的不同而变化 |

#### 请求示例
```json
{
  "advertiserId": "DSP001",
  "name": "测试创意",
  "creativeType": "BANNER",
  "creativeUrl": "https://test.com/creative.jpg",
  "landingPage": "https://test.com/landingpage"
}
```

#### 响应参数
返回广告交易平台生成的创意编码。

#### 响应示例
```json
{
  "success": true,
  "code": "00000",
  "message": "成功",
  "data": "CRTV20210101000001"
}
```

### 4.2 更新广告创意素材

#### 接口地址
`PUT /v1/creatives`

#### 接口描述
更新广告创意素材。

#### 请求参数
同创建广告创意素材接口，但creativeId为必填项。

#### 响应参数
boolean类型，更新成功返回true，否则返回false。

#### 响应示例
```json
{
  "success": true,
  "code": "00000",
  "message": "成功",
  "data": true
}
```

### 4.3 获取单个广告素材审核状态

#### 接口地址
`GET /v1/creatives/{creativeId}/audit-status`

#### 接口描述
获取指定广告素材的审核状态。

#### 请求参数
| 参数名 | 类型 | 必填 | 描述 |
| --- | --- | --- | --- |
| creativeId | string | 是 | DSP平台创意ID |

#### 响应参数
| 参数名 | 类型 | 描述 |
| --- | --- | --- |
| creativeId | string | DSP平台素材ID |
| auditResult | object | 审核结果对象 |

auditResult对象结构同广告主审核结果。

#### 响应示例
```json
{
  "success": true,
  "code": "00000",
  "message": "成功",
  "data": {
    "creativeId": "CRT001",
    "auditResult": {
      "auditStatus": "APPROVED",
      "auditReason": "",
      "auditTime": 1609459200000
    }
  }
}
```

### 4.4 批量获取广告素材审核状态

#### 接口地址
`GET /v1/creatives/audit-status`

#### 接口描述
批量获取多个广告素材的审核状态。

#### 请求参数
| 参数名 | 类型 | 必填 | 描述 |
| --- | --- | --- | --- |
| creativeIds | array | 是 | DSP平台创意ID列表 |

#### 响应参数
返回创意审核结果对象数组，每个对象结构同单个查询接口。

#### 响应示例
```json
{
  "success": true,
  "code": "00000",
  "message": "成功",
  "data": [
    {
      "creativeId": "CRT001",
      "auditResult": {
        "auditStatus": "APPROVED",
        "auditReason": "",
        "auditTime": 1609459200000
      }
    },
    {
      "creativeId": "CRT002",
      "auditResult": {
        "auditStatus": "PENDING",
        "auditReason": "",
        "auditTime": 1609459200000
      }
    }
  ]
}
```

## 5. 错误码定义

| 错误码 | 描述 |
| --- | --- |
| 00000 | 成功 |
| A0001 | 参数错误 |
| A0002 | 广告主不存在 |
| A0003 | 创意不存在 |
| A0004 | 权限不足 |
| A0005 | 系统内部错误 |