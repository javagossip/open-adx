# OAX-RTB 接口规范

## 1. 概述

本文档定义 **OAX-RTB（Open Ad Exchange – Real Time Bidding）** 接口规范。该规范基于用户提供的 `rtb.proto` 文件生成，整体设计参考 OpenRTB 思想，但字段与结构以 OAX 自有定义为准。

- **通信协议**：HTTP/1.1
- **请求方式**：POST
- **连接方式**：Keep-Alive
- **数据格式**：Protocol Buffers（proto2）
- **字符编码**：UTF-8

本接口用于 ADX 向 DSP 发起实时竞价请求，DSP 返回竞价响应结果。

---

## 2. 接口说明

### 2.1 竞价接口

- **URL**：由 DSP 自行提供
- **Method**：POST
- **Content-Type**：application/x-protobuf

ADX 将 `BidRequest` 序列化为 Protobuf 二进制数据，通过 HTTP POST 发送至 DSP。

DSP 接收到请求后，需在超时时间内返回 `BidResponse`。

---

## 3. BidRequest（竞价请求）

### 3.1 BidRequest 结构

```proto
message BidRequest {
  required string id = 1;
  repeated Imp imp = 2;
  optional Site site = 3;
  optional App app = 4;
  optional Device device = 5;
  optional User user = 6;
}
```

### 3.2 字段说明

| 字段 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| id | string | 是 | 竞价请求 ID，由 ADX 生成 |
| imp | Imp[] | 是 | 广告位对象数组，至少包含一个 |
| site | Site | 否 | 网站流量场景使用 |
| app | App | 否 | 移动应用流量场景使用 |
| device | Device | 否 | 设备信息 |
| user | User | 否 | 用户信息 |

> 说明：`site` 与 `app` 二选一，分别代表 Web 与 App 流量。

---

## 4. Imp（广告位对象）

```proto
message Imp {
  required string id = 1;
  optional Banner banner = 2;
  optional Native native = 3;
  optional uint32 bidfloor = 4;
}
```

### 字段说明

| 字段 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| id | string | 是 | 广告位 ID |
| banner | Banner | 否 | Banner 广告请求 |
| native | Native | 否 | 原生广告请求 |
| bidfloor | uint32 | 否 | 底价（单位：分） |

---

## 5. Banner（横幅广告）

```proto
message Banner {
  required uint32 w = 1;
  required uint32 h = 2;
}
```

| 字段 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| w | uint32 | 是 | 广告宽度（像素） |
| h | uint32 | 是 | 广告高度（像素） |

---

## 6. Native（原生广告请求）

```proto
message Native {
  repeated NativeAsset assets = 1;
}
```

### NativeAsset

```proto
message NativeAsset {
  required uint32 id = 1;
  required uint32 required = 2;
  optional Title title = 3;
  optional Img img = 4;
  optional Data data = 5;
}
```

| 字段 | 说明 |
|---|---|
| title | 标题资源 |
| img | 图片资源 |
| data | 数据资源 |

---

## 7. Site / App

### Site

```proto
message Site {
  optional string id = 1;
  optional string name = 2;
  optional string domain = 3;
}
```

### App

```proto
message App {
  optional string id = 1;
  optional string name = 2;
  optional string bundle = 3;
}
```

---

## 8. Device（设备信息）

```proto
message Device {
  optional string ua = 1;
  optional string ip = 2;
  optional uint32 os = 3;
  optional string osv = 4;
  optional string model = 5;
}
```

| 字段 | 说明 |
|---|---|
| ua | User-Agent |
| ip | 客户端 IP |
| os | 操作系统类型 |
| osv | 操作系统版本 |
| model | 设备型号 |

---

## 9. User（用户信息）

```proto
message User {
  optional string id = 1;
}
```

---

## 10. BidResponse（竞价响应）

```proto
message BidResponse {
  required string id = 1;
  repeated SeatBid seatbid = 2;
}
```

### SeatBid / Bid

```proto
message SeatBid {
  repeated Bid bid = 1;
}

message Bid {
  required string id = 1;
  required string impid = 2;
  required uint32 price = 3;
  optional string adid = 4;
  optional uint32 click_type = 10;
  optional string bundle = 11;
  optional NativeAd nativeAd = 13;
  optional string app_download_url = 14;
  optional string app_name = 15;
  optional string deeplink = 16;
}
```

### Bid 字段说明

| 字段 | 说明 |
|---|---|
| id | DSP 生成的出价 ID |
| impid | 对应的 Imp.id |
| price | 出价，单位：分 |
| adid | 广告 ID |
| click_type | 点击类型（1 浏览器打开 / 2 安卓下载 / 3 Deeplink / 4 iOS 应用） |
| bundle | Android 包名或 iOS AppID |
| nativeAd | 原生广告响应 |
| app_download_url | 应用下载地址 |
| app_name | 应用名称 |
| deeplink | Deeplink 地址 |

---

## 11. NativeAd（原生广告响应）

DSP 在原生广告场景下，通过 `NativeAd` 返回对应的素材资源，与请求中的 NativeAsset 一一对应。

---

## 12. 超时与异常处理

- DSP 需在 ADX 约定的超时时间内返回响应（通常 ≤ 100ms）
- 超时或返回空响应视为放弃竞价
- HTTP 状态码建议：
  - 200：正常响应
  - 204：无广告可返回

---

## 13. 附录

- 本协议不完全等同 OpenRTB，请以本文档字段定义为准
- DSP 对接时应严格按照 Protobuf 字段定义进行序列化 / 反序列化

