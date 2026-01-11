# oax广告引擎 SPI说明文档

## 概述

oax广告引擎提供了SPI（Service Provider Interface）机制，允许不同DSP（Demand-Side Platform）供应商进行自定义扩展。通过SPI机制，不同的DSP可以实现自己的业务逻辑，包括协议转换、宏替换、竞价价格编解码等功能。

## 核心SPI接口

### 1. RtbProtocolConverter

**功能**: 非标准协议的DSP协议转换扩展点
- 将ADX标准协议转换为DSP非标准协议
- 将DSP非标准协议转换为ADX标准协议

**方法**:
- `REQ to(BidRequest bidRequest)` - 将ADX标准BidRequest转换为DSP特定协议请求
- `BidResponse from(RSP rsp)` - 将DSP特定协议响应转换为ADX标准BidResponse

### 2. RtbProtocolInvoker

**功能**: RTB协议调用接口扩展点
- 针对非标准协议的DSP，实现按其特定协议发起RTB请求

**方法**:
- `RSP invoke(Dsp dsp, REQ request)` - 执行DSP特定协议的RTB请求

### 3. WinPriceCodec

**功能**: 竞价结果价格编码解码器
- 对竞价成功的价格信息进行编码和解码

**方法**:
- `String encode(long price, Dsp dsp)` - 将竞价价格编码为字符串
- `long decode(String price, Dsp dsp)` - 将编码后的价格字符串解码为长整型数值

### 4. MacroContextBuilder

**功能**: 宏替换上下文构建器
- 构建宏替换上下文，保存具体宏变量对应的值

**方法**:
- `MacroContext build(DspBid dspBid)` - 基于DSP竞价信息构建宏上下文

### 5. MacroProcessor

**功能**: 广告交易平台宏定义处理器
- 处理模板中的宏变量替换

**方法**:
- `String process(String template, MacroContext macroContext)` - 使用宏上下文处理模板并返回结果字符串

## 工厂类

### OaxSpiFactory

**功能**: SPI工厂类，用于获取各种SPI实现

**提供的方法**:
- `getMacroProcessor(String dspId)` - 获取指定DSP的宏处理器
- `getRtbProtocolConverter(String dspId)` - 获取指定DSP的协议转换器
- `getWinPriceCodec(String dspId)` - 获取指定DSP的价格编解码器
- `getRtbProtocolInvoker(String dspId)` - 获取指定DSP的协议调用器
- `getMacroContextBuilder(String dspId)` - 获取指定DSP的宏上下文构建器

**默认实现**: 当指定DSP的实现不存在时，会返回key为"default"的默认实现

## 数据模型

### MacroContext

**功能**: 宏上下文类，封装宏变量映射关系

**属性**:
- `macroValueMap`: 存储宏名称到值的映射关系

**方法**:
- `getMacroValue(String macroName)`: 根据宏名称获取对应值

## 扩展机制

所有SPI接口都使用了`@ExtensionPoint`注解，表明它们是可扩展的接口。
- 实现类需要注册到扩展注册表中，并使用对应的DSP ID作为键
- 可以为特定DSP提供定制化的实现
- 如果没有找到特定DSP的实现，则会使用默认实现

## 使用场景

1. **协议适配**: 不同DSP可能使用不同的协议格式，通过RtbProtocolConverter和RtbProtocolInvoker实现协议适配
2. **价格安全**: 通过WinPriceCodec对竞价价格进行加密保护
3. **动态替换**: 在回调URL或其他地方使用宏变量进行动态内容替换
4. **灵活配置**: 允许每个DSP有不同的处理逻辑，提高系统的灵活性