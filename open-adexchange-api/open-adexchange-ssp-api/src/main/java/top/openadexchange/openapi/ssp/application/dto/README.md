# SSP API DTO 类说明

## 概述

此目录包含根据 protobuf 定义生成的 Java DTO 类，用于 SSP（Supply-Side Platform）广告交易平台的媒体方接口。

## 文件结构

- `AdFetchRequestDto.java` - 对应 protobuf 中的 `AdFetchRequest` 消息
- `AdFetchResponseDto.java` - 对应 protobuf 中的 `AdFetchResponse` 消息

## DTO 类映射关系

### AdFetchRequestDto
对应 protobuf 中的 `AdFetchRequest` 消息，包含以下嵌套类：
- `ImpDto` - 对应 `Imp` 消息
- `AppDto` - 对应 `App` 消息
- `SiteDto` - 对应 `Site` 消息
- `ContentDto` - 对应 `Content` 消息
- `DeviceDto` - 对应 `Device` 消息
- `GeoDto` - 对应 `Geo` 消息

### AdFetchResponseDto
对应 protobuf 中的 `AdFetchResponse` 消息，包含以下嵌套类：
- `NativeAdDto` - 对应 `NativeAd` 消息
- `SeatBidDto` - 对应 `SeatBid` 消息
- `BidDto` - 对应 `Bid` 消息（在 SeatBidDto 内部）

## 字段映射规则

1. **基本类型**:
   - `string` -> `String`
   - `bool` -> `Boolean`
   - `float` -> `Float`
   - `uint32` -> `Integer`

2. **复合类型**:
   - `repeated` -> `List<T>`
   - `map<string, string>` -> `Map<String, String>`

3. **命名转换**:
   - protobuf 风格的字段名转换为 Java 驼峰命名法
   - 例如: `device_type` -> `deviceType`

## 使用示例

```java
// 创建广告请求对象
AdFetchRequestDto request = new AdFetchRequestDto();
request.setId("request-123");

// 设置设备信息
AdFetchRequestDto.DeviceDto device = new AdFetchRequestDto.DeviceDto();
device.setUa("Mozilla/5.0...");
device.setIp("192.168.1.1");
request.setDevice(device);

// 设置曝光信息
AdFetchRequestDto.ImpDto imp = new AdFetchRequestDto.ImpDto();
imp.setId("imp-1");
imp.setTagid("tag-123");
request.setImp(Arrays.asList(imp));
```

## 注意事项

1. 所有 DTO 类都使用 Lombok 的 `@Data` 注解，自动生成 getter/setter 方法
2. 嵌套对象作为静态内部类实现，便于使用和管理
3. 可选字段使用包装类型，便于判断是否设置
4. 遵循 Java 命名规范，将 protobuf 的下划线命名转换为驼峰命名